package unification.tool.module.extent.output.uml.sql;

import clojure.lang.IPersistentVector;
import clojure.lang.PersistentArrayMap;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.common.sql.extent.ExtentSQLModel;
import unification.tool.module.extent.input.uml.sql.Association;
import unification.tool.module.extent.input.uml.sql.InputExtentTable;
import unification.tool.module.extent.input.uml.sql.Instance;
import unification.tool.module.extent.output.IOutputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.sql.Mapping;
import unification.tool.module.model.sql.SQLDataModelModule;
import unification.tool.module.model.sql.Table;

import java.util.HashSet;
import java.util.Set;

public class OutputExtentUMLToSQLModule implements IOutputExtentModelModule, ExtentSQLModel {

    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final SQLDataModelModule sqlDataModelModule;
    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final IntermediateUMLModelModule intermediateModelModule;

    private final String username;
    private final String password;
    private final String schema;

    private Set<OutputExtentTable> tables = new HashSet<>();

    private OutputExtentUMLToSQLModule(SQLDataModelModule dataModelModule, String extentFilePath,
                                       IntermediateUMLModelManagerModule intermediateModelManagerModule,
                                       IPersistentVector dataSourceAccess){

        this.intermediateModelManagerModule = intermediateModelManagerModule;
        this.intermediateModelModule = intermediateModelManagerModule.getModelModule();
        this.sqlDataModelModule = dataModelModule;


        if (dataSourceAccess.length() != 1) {
            throw new IllegalStateException("Invalid access vector: wrong size");
        }

        try{
            PersistentArrayMap credentialsMap = (PersistentArrayMap) dataSourceAccess.valAt(0);
            username = PARSER.stringFromMap(credentialsMap,"user-name");
            password = PARSER.stringFromMap(credentialsMap,"password");
            schema = PARSER.stringFromMap(credentialsMap,"database-name");
        }catch(ClassCastException e){
            throw new IllegalStateException("Invalid access vector: at least one of the passed credentials is not a String");
        }

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.output.uml.sql.parser", extentFilePath);


        PARSER.findAllItemsByType(parsedConfiguration, "foreach").forEach(forEachMap -> {

            String umlClassname = PARSER.keywordNameFromMap(forEachMap,"name");


            PARSER.findAllItemsFromMapValueByType(forEachMap,"body","add-entity").forEach(addEntityMap -> {
                String tableDefinition = PARSER.vectorFromMap(addEntityMap, "table").valAt(0).toString();
                Table table = dataModelModule.getTable(tableDefinition);
                OutputExtentTable extentTable = new OutputExtentTable(table, umlClassname);

                PARSER.findAllItemsFromMapValueByType(addEntityMap,"mappings","mapping").forEach(mappingMap -> {
                    String columnDefinition = PARSER.vectorFromMap(mappingMap,"column").valAt(0).toString();
                    String columnName = table.getColumn(columnDefinition).getName();
                    String attributeName = PARSER.stringFromMap(mappingMap,"name");
                    Mapping mapping = new Mapping(columnName,attributeName.substring(1));

                    extentTable.addMapping(mapping);
                });

                //TODO mapping-relation

                tables.add(extentTable);
                System.out.println(tableDefinition);
            });
        });

        PARSER.findAllItemsByType(parsedConfiguration, "association").forEach(forEachMap -> {
            String associationDefinition = PARSER.stringFromMap(forEachMap,"name");
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

    public IntermediateUMLModelManagerModule getIntermediateModelManagerModule(){
        return intermediateModelManagerModule;
    }

    public Set<OutputExtentTable> getTables() {
        return tables;
    }

    public static IOutputExtentModelModule newInstance(IDataModelModule dataModelModule, String extentFilePath,
                                                       IIntermediateModelManagerModule intermediateModelManagerModule,
                                                       IPersistentVector dataSourceAccess) {
        if (dataModelModule instanceof SQLDataModelModule) {
            if (intermediateModelManagerModule instanceof IntermediateUMLModelManagerModule) {
                return new OutputExtentUMLToSQLModule((SQLDataModelModule) dataModelModule, extentFilePath,
                        (IntermediateUMLModelManagerModule) intermediateModelManagerModule, dataSourceAccess);
            } else {
                throw new IllegalArgumentException("An instance of IntermediateUMLModelManagerModule should be passed");
            }
        } else {
            throw new IllegalArgumentException("An instance of SQLLDataModelModule should be passed");
        }
    }
}
