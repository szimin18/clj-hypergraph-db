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
import unification.tool.module.model.xml.XMLDataModelModule;

import java.util.List;

public class InputExtentSQLToUMLModule implements IInputExtentModelModule {

    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelModule intermediateModelModule;
    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;

    private final SQLToUMLMapper mapper;

    private final String username;
    private final String password;
    private final String schema;


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

        mapper = new SQLToUMLMapper();

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.input.uml.sql.parser", extentFilePath);

        PARSER.findAllItemsByType(parsedConfiguration, "foreach").forEach(forEachMap -> {
            IPersistentVector table = PARSER.vectorFromMap(forEachMap, "table");
            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-instance").forEach(addInstanceMap -> {
                String className = PARSER.keywordNameFromMap(addInstanceMap, "name");
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping").forEach(mappingMap -> {
                    IPersistentVector mappingPath = PARSER.vectorFromMap(mappingMap, "path");
                    String attributeName = PARSER.keywordNameFromMap(mappingMap, "name");
                });
            });
            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-instance").forEach(addInstanceMap -> {
                String className = PARSER.keywordNameFromMap(addInstanceMap, "name");
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping").forEach(mappingMap -> {
                    IPersistentVector mappingPath = PARSER.vectorFromMap(mappingMap, "path");
                    String attributeName = PARSER.keywordNameFromMap(mappingMap, "name");
                });
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
            throw new IllegalArgumentException("An instance of XMLDataModelModule should be passed");
        }
    }


    static final class SQLToUMLMapper {


    }
}
