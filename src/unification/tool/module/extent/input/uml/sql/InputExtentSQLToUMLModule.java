package unification.tool.module.extent.input.uml.sql;

import clojure.lang.IPersistentVector;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.common.sql.extent.ExtentSQLModel;
import unification.tool.module.extent.input.IInputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.sql.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class InputExtentSQLToUMLModule implements IInputExtentModelModule, ExtentSQLModel {

    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelModule intermediateModelModule;
    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;

    private final String username;
    private final String password;
    private final String schema;

    private Set<InputExtentTable> tables = new HashSet<>();

    private InputExtentSQLToUMLModule(SQLDataModelModule dataModelModule, String extentFilePath,
                                      IntermediateUMLModelManagerModule intermediateModelManagerModule,
                                      IPersistentVector dataSourceAccess) {
        this.intermediateModelModule = intermediateModelManagerModule.getModelModule();
        this.intermediateModelManagerModule = intermediateModelManagerModule;

        if (dataSourceAccess.length() != 3) {
            throw new IllegalStateException("Invalid access vector: wrong size");
        }

        try {
            username = (String) dataSourceAccess.valAt(0);
            password = (String) dataSourceAccess.valAt(1);
            schema = (String) dataSourceAccess.valAt(2);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Invalid access vector: at least one of the passed credentials is not a String");
        }

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.input.uml.sql.parser", extentFilePath);


        PARSER.findAllItemsByType(parsedConfiguration, "foreach").forEach(forEachMap -> {

            String tableDefinition = PARSER.vectorFromMap(forEachMap, "table").valAt(0).toString();
            Table table = dataModelModule.getTable(tableDefinition);
            InputExtentTable extentTable = new InputExtentTable(table);

            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-instance").forEach(addInstanceMap -> {
                String className = PARSER.keywordNameFromMap(addInstanceMap, "name");
                Instance instance = new Instance(className);

                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping").forEach((mappingMap -> {
                    String columnDefinition = PARSER.vectorFromMap(mappingMap, "column").valAt(0).toString();
                    String columnName = table.getColumn(columnDefinition).getName();
                    String attributeName = PARSER.stringFromMap(mappingMap,"name");
                    Mapping mapping = new Mapping(columnName,attributeName.substring(1));

                    instance.addMapping(columnName, mapping);
                }));

                //TODO primary-keys differentiation
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping-pk").forEach((mappingMap -> {
                    String columnDefinition = PARSER.vectorFromMap(mappingMap, "column").valAt(0).toString();
                    String columnName = table.getColumn(columnDefinition).getName();
                    String attributeName = PARSER.stringFromMap(mappingMap,"name");
                    Mapping mapping = new Mapping(columnName,attributeName.substring(1));

                    instance.addMapping(columnName, mapping);
                }));

                extentTable.addInstance(instance);

            });


            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-association").forEach(addAssociationMap -> {
                String associationName = PARSER.keywordNameFromMap(addAssociationMap, "name");
                Association association = new Association(associationName);

                PARSER.findAllItemsFromMapValueByType(addAssociationMap, "roles", "role").forEach((roleMap -> {
                    String roleName = PARSER.vectorFromMap(roleMap, "name").valAt(0).toString().substring(1);
                    String columnDefinition = PARSER.objectFromMap(roleMap, "column").toString();
                    Column column = table.getColumn(columnDefinition);
                    String columnName = column.getName();
                    Role role = new Role(columnName, roleName);

                    association.addRole(columnName, role);
                }));

                extentTable.addAssoctiation(association);
            });

            tables.add(extentTable);

        });

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSchema() {
        return schema;
    }

    public Collection<InputExtentTable> getTables(){
        return tables;
    }

    public static InputExtentSQLToUMLModule newInstance(
            IDataModelModule dataModelModule, String extentFilePath, IIntermediateModelManagerModule intermediateModelManagerModule,
            IPersistentVector dataSourceAccess) {
        if (dataModelModule instanceof SQLDataModelModule) {
            if (intermediateModelManagerModule instanceof IntermediateUMLModelManagerModule) {
                return new InputExtentSQLToUMLModule((SQLDataModelModule) dataModelModule, extentFilePath,
                        (IntermediateUMLModelManagerModule) intermediateModelManagerModule, dataSourceAccess);
            } else {
                throw new IllegalArgumentException("An instance of IntermediateUMLModelManagerModule should be passed");
            }
        } else {
            throw new IllegalArgumentException("An instance of SQLDataModelModule should be passed");
        }
    }


    public IntermediateUMLModelManagerModule.UMLClassInstance addInstance(Instance instance, ResultSet rs) throws SQLException{
        Map<String,Collection<Object>> attributes = new HashMap<>();
        String className = instance.getUMLClassname();

        for(Mapping mapping : instance.getMappings()){
            try {
                Collection<Object> duplicates = attributes.get(mapping.getAttributeName());
                if(duplicates != null){
                    duplicates.add(rs.getString(mapping.getColumnName()));
                }else {
                    String value = rs.getString(mapping.getColumnName());
                    if(value != null) {
                        List<Object> list = new LinkedList<>();
                        list.add(value);
                        attributes.put(mapping.getAttributeName(), list);
                    }
                }
            }catch (SQLException e){
                throw new SQLException("Failed with column: "+mapping.getColumnName(),e.getCause());
            }
        }
        //TODO Remove after changing newInstance Impl
        IntermediateUMLModelManagerModule.UMLClassInstance umlInstance = intermediateModelManagerModule.findInstanceByAttributes(className, attributes);
        if(umlInstance == null){
            umlInstance = intermediateModelManagerModule.newClassInstance(className, attributes);
        }

        return umlInstance;
    }




}
