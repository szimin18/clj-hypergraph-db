package unification.tool.module.model.sql;

import clojure.lang.IPersistentVector;
import clojure.lang.RT;
import clojure.lang.Seqable;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.model.IDataModelModule;

import java.util.*;

public class SQLDataModelModule implements IDataModelModule {

    private static final CommonModelParser PARSER = CommonModelParser.getInstance();
    private static final Map<String, SQLDataModelModule> parsedModels = new HashMap<>();

    private final IPersistentVector accessVector;

    private Map<String,Table> tables = new HashMap<>();



    @Override
    public IPersistentVector getAccessVector() {
        return accessVector;
    }

    private SQLDataModelModule(String dataModelPath) {
        Seqable parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.model.sql.parser", dataModelPath);

        String user = null;
        String password = null;
        String schema = null;

        List<Object> databaseMaps = PARSER.findAllItemsByType(parsedConfiguration, "database");
        if (!databaseMaps.isEmpty()) {
            List<Object> credentialItems = PARSER.findAllItemsFromMapValueByType(databaseMaps.get(0), "metadata",
                    "credentials");
            try {
                if (!credentialItems.isEmpty()) {
                    user = PARSER.stringFromMap(credentialItems.get(0), "user-name");
                    password = PARSER.stringFromMap(credentialItems.get(0), "password");
                    schema = PARSER.stringFromMap(credentialItems.get(0), "database-name");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (user != null && password != null && schema != null) {
            accessVector = RT.vector(user,password,schema);
        } else {
            accessVector = null;
        }


        PARSER.findAllItemsByType(parsedConfiguration,"table").forEach((tableMap ->{
            String tableName = PARSER.stringFromMap(tableMap, "table-name");
            String tableDefinition = PARSER.stringFromMap(tableMap,"table-definition");
            Table table = new Table(tableName,tableDefinition);
            Seqable columns = PARSER.seqableFromMap(tableMap, "columns");

            PARSER.findAllItemsByType(columns,"column").forEach((columnMap ->{
                String columnName = PARSER.stringFromMap(columnMap,"column-name");
                String columnDefinition = PARSER.stringFromMap(columnMap,"column-definition");

                //TODO field flags, currently unused, required for validation
                Seqable flags = PARSER.seqableFromMap(columnMap,"flags");

                Column column = new Column(columnName,columnDefinition);
                table.addColumn(column,columnDefinition);

            }));

            tables.put(tableDefinition,table);

        }));
    }

    public static IDataModelModule getInstance(String dataModelPath) {
        SQLDataModelModule parsedModel = parsedModels.get(dataModelPath);
        if (parsedModel == null) {
            parsedModel = new SQLDataModelModule(dataModelPath);
            parsedModels.put(dataModelPath, parsedModel);
        }
        return parsedModel;
    }

    public Collection<Table> getTables(){
        return tables.values();
    }

    public Collection<String> getTableDefinitions(){
        return tables.keySet();
    }

    public Table getTable(String tableDefinition){
        return tables.get(tableDefinition);
    }

}
