package unification.tool.module.extent.input.uml.sql;

import clojure.lang.IPersistentVector;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.extent.input.IInputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.IIntermediateModelModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.sql.SQLDataModelModule;
import unification.tool.module.model.sql.Table;
import unification.tool.module.model.sql.Column;

import java.util.HashSet;
import java.util.Set;


public class InputExtentSQLToUMLModule implements IInputExtentModelModule {

    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelModule intermediateModelModule;
    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;

    private final SQLToUMLMapper mapper;

    private final String username;
    private final String password;
    private final String schema;

    private Set<InputExtentTable> tables = new HashSet<>();


    private InputExtentSQLToUMLModule(SQLDataModelModule dataModelModule, String extentFilePath,
                                      IntermediateUMLModelModule intermediateModelModule,
                                      IntermediateUMLModelManagerModule intermediateModelManagerModule,
                                      IPersistentVector dataSourceAccess) {
        this.intermediateModelModule = intermediateModelModule;
        this.intermediateModelManagerModule = intermediateModelManagerModule;

        if (dataSourceAccess.length() != 3) {
            throw new IllegalStateException("Invalid access vector: wrong size");
        }

        try{
            username = (String) dataSourceAccess.valAt(0);
            password = (String) dataSourceAccess.valAt(1);
            schema = (String) dataSourceAccess.valAt(2);
        }catch(ClassCastException e){
            throw new IllegalStateException("Invalid access vector: at least one of the passed credentials is not a String");
        }

        /*for(Table table : dataModelModule.getTables()){
            System.out.println(table.getName()+ " - " + table.getDefinition());
            for(Column column : table.getColumns()){
                System.out.println("    " + column.getName() + " - " + column.getDefinition());
            }
        }*/


        mapper = new SQLToUMLMapper();


        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.input.uml.sql.parser", extentFilePath);


        PARSER.findAllItemsByType(parsedConfiguration, "foreach").forEach(forEachMap -> {

            String tableDefinition = PARSER.vectorFromMap(forEachMap, "table").valAt(0).toString();
            Table table = dataModelModule.getTable(tableDefinition);
            InputExtentTable extentTable = new InputExtentTable(table);

            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-instance").forEach(addInstanceMap -> {
                String className = PARSER.keywordNameFromMap(addInstanceMap, "name");
                Instance instance = new Instance(className);

                PARSER.findAllItemsFromMapValueByType(addInstanceMap,"mappings","mapping").forEach((mappingMap -> {
                    String columnDefinition = PARSER.vectorFromMap(mappingMap,"column").valAt(0).toString();
                    String columnName = table.getColumn(columnDefinition).getName();
                    String attributeName = PARSER.stringFromMap(mappingMap,"name");
                    Mapping mapping = new Mapping(columnName,attributeName);

                    instance.addMapping(columnName,mapping);
                }));

                //TODO primary-keys differentiation
                PARSER.findAllItemsFromMapValueByType(addInstanceMap,"mappings","mapping-pk").forEach((mappingMap -> {
                    String columnDefinition = PARSER.vectorFromMap(mappingMap,"column").valAt(0).toString();
                    String columnName = table.getColumn(columnDefinition).getName();
                    String attributeName = PARSER.stringFromMap(mappingMap,"name");
                    Mapping mapping = new Mapping(columnName,attributeName);

                    instance.addMapping(columnName,mapping);
                }));

                extentTable.addInstance(instance);

            });

            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-association").forEach(addAssociationMap -> {
                String associationName = PARSER.keywordNameFromMap(addAssociationMap,"name");
                Association association = new Association(associationName);

                PARSER.findAllItemsFromMapValueByType(addAssociationMap,"roles","role").forEach((roleMap -> {
                    String roleName = PARSER.vectorFromMap(roleMap,"name").valAt(0).toString().substring(1);
                    String columnDefinition = PARSER.objectFromMap(roleMap,"column").toString();
                    Column column = table.getColumn(columnDefinition);
                    String columnName = column.getName();
                    Role role = new Role(columnName,roleName);

                    association.addRole(columnName,role);
                }));
            });
        });

    }


    public static InputExtentSQLToUMLModule newInstance(
            IDataModelModule dataModelModule, String extentFilePath, IIntermediateModelModule intermediateModelModule,
            IIntermediateModelManagerModule intermediateModelManagerModule, IPersistentVector dataSourceAccess) {
        if (dataModelModule instanceof SQLDataModelModule) {
            if (intermediateModelManagerModule instanceof IntermediateUMLModelManagerModule) {
                if (intermediateModelModule instanceof IntermediateUMLModelModule) {
                    return new InputExtentSQLToUMLModule((SQLDataModelModule) dataModelModule, extentFilePath,
                            (IntermediateUMLModelModule) intermediateModelModule,
                            (IntermediateUMLModelManagerModule) intermediateModelManagerModule, dataSourceAccess);
                } else {
                    throw new IllegalArgumentException("An instance of IntermediateUMLModelModule should be passed");
                }
            } else {
                throw new IllegalArgumentException("An instance of IntermediateUMLModelManagerModule should be passed");
            }
        } else {
            throw new IllegalArgumentException("An instance of SQLDataModelModule should be passed");
        }
    }


    static final class SQLToUMLMapper {


    }

}
