package unification.tool.module.extent.output.uml.sql;

import clojure.lang.*;
import javafx.scene.control.Tab;
import org.apache.log4j.Logger;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.common.sql.extent.ExtentSQLModel;
import unification.tool.module.extent.output.IOutputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.sql.Mapping;
import unification.tool.module.model.sql.SQLDataModelModule;
import unification.tool.module.model.sql.Table;

import java.util.*;
import java.util.stream.Collectors;

public class OutputExtentUMLToSQLModule implements IOutputExtentModelModule, ExtentSQLModel {

    private static final Logger logger = Logger.getLogger(OutputExtentUMLToSQLModule.class);

    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final SQLDataModelModule sqlDataModelModule;
    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final IntermediateUMLModelModule intermediateModelModule;

    private final String username;
    private final String password;
    private final String schema;

    private Set<OutputExtentTable> tables = new HashSet<>();
    private Set<OutputExtentInnerTable> innerTable = new HashSet<>();

    private Map<String, ValueProvider> variables = new HashMap<>();
    private Map<String, IFn> functions = new HashMap<>();

    private OutputExtentUMLToSQLModule(SQLDataModelModule dataModelModule, String extentFilePath,
                                       IntermediateUMLModelManagerModule intermediateModelManagerModule,
                                       IPersistentVector dataSourceAccess) {

        this.intermediateModelManagerModule = intermediateModelManagerModule;
        this.intermediateModelModule = intermediateModelManagerModule.getModelModule();
        this.sqlDataModelModule = dataModelModule;


        if (dataSourceAccess.length() != 1) {
            throw new IllegalStateException("Invalid access vector: wrong size");
        }

        try {
            PersistentArrayMap credentialsMap = (PersistentArrayMap) dataSourceAccess.valAt(0);
            username = PARSER.stringFromMap(credentialsMap, "user-name");
            password = PARSER.stringFromMap(credentialsMap, "password");
            schema = PARSER.stringFromMap(credentialsMap, "database-name");
        } catch (ClassCastException e) {
            throw new IllegalStateException("Invalid access vector: at least one of the passed credentials is not a String");
        }

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.output.uml.sql.parser", extentFilePath);

        PARSER.findAllItemsByType(parsedConfiguration, "bind").forEach(bindMap -> {
            String variableName = PARSER.keywordNameFromMap(bindMap, "to");

            Object fromObject = PARSER.objectFromMap(bindMap, "from");

            if(fromObject instanceof IPersistentMap){

            } else if(fromObject instanceof KeywordValueProvider){

            } else {
                variables.put(variableName,new SingletonValueProvider(fromObject));
            }

        });



        PARSER.findAllItemsByType(parsedConfiguration, "foreach").forEach(forEachMap -> {

            String umlClassname = PARSER.keywordNameFromMap(forEachMap, "name");

            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-entity").forEach(addEntityMap -> {
                String tableDefinition = PARSER.vectorFromMap(addEntityMap, "table").valAt(0).toString();
                Table table = dataModelModule.getTable(tableDefinition);
                OutputExtentTable extentTable = new OutputExtentTable(table, umlClassname);

                addEntityMappings(addEntityMap, table, extentTable);

                //TODO mapping-relation

                tables.add(extentTable);
                logger.info("Parsed for-each table: "+tableDefinition);
            });

            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "foreach-attribute").forEach(attributeMap -> {
                String mainAttributeName = PARSER.stringFromMap(attributeMap, "attribute").substring(1);

                PARSER.findAllItemsFromMapValueByType(attributeMap, "body", "add-entity").forEach(addEntityMap -> {
                    String tableDefinition = PARSER.vectorFromMap(addEntityMap, "table").valAt(0).toString();
                    Table table = dataModelModule.getTable(tableDefinition);
                    OutputExtentInnerTable extentTable = new OutputExtentInnerTable(table, umlClassname, mainAttributeName);

                    addEntityMappings(addEntityMap, table, extentTable);

                        innerTable.add(extentTable);
                    logger.info("Parsed for-each-attribute table: "+tableDefinition);
                });

            });
        });

        PARSER.findAllItemsByType(parsedConfiguration, "association").forEach(forEachMap -> {
            String associationDefinition = PARSER.stringFromMap(forEachMap, "name");
        });
    }

    private void addEntityMappings(Object addEntityMap, Table table, OutputExtentTable extentTable){
        PARSER.findAllItemsFromMapValueByType(addEntityMap, "mappings", "mapping").forEach(mappingMap -> {
            IPersistentVector vector = PARSER.vectorFromMap(mappingMap, "column");
            String columnDefinition = vector.valAt(0).toString();
            String prefix = (String)(vector.length()>1?vector.valAt(1):null);
            String columnName = table.getColumn(columnDefinition).getName();
            String attributeName = PARSER.stringFromMap(mappingMap, "name");
            Mapping mapping = new Mapping(columnName, attributeName.substring(1), prefix);

            extentTable.addMapping(mapping);
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

    public IntermediateUMLModelManagerModule getIntermediateModelManagerModule() {
        return intermediateModelManagerModule;
    }

    public Set<OutputExtentTable> getTables() {
        return tables;
    }

    public Set<OutputExtentInnerTable> getInnerTable() {
        return innerTable;
    }

    public Map<String, ValueProvider> getVariables() {
        return variables;
    }

    public ValueProvider getValueProvider(Object fromObject) {
        ValueProvider provider = new ListValueProvider();

        if (fromObject instanceof IPersistentMap) {
            switch (PARSER.keywordNameFromMap(fromObject, "from")) {
                case "call":
                    return new CallProvider(functions.get(PARSER.keywordNameFromMap(fromObject, "fn-name")), PARSER.seqableToList(PARSER.seqableFromMap(fromObject, "args")));
                case "aggregate":
                    return new AggregateInstance(fromObject);
                default:
                    return new EmptyValueProvider();
            }
        }

        return provider;
    }

    public class EmptyValueProvider extends ListValueProvider {
    }

    public class KeywordValueProvider extends ListValueProvider {
    }

    public class CallProvider extends ListValueProvider {

        private final IFn function;
        private final List<OutputExtentUMLToSQLModule.ValueProvider> arguments = new ArrayList<>();

        private CallProvider(IFn function, List<Object> arguments) {
            this.function = function;
            arguments.stream().map(OutputExtentUMLToSQLModule.this::getValueProvider).forEach(this.arguments::add);
        }

        @Override
        public List<Object> getValues() {
            add(function.applyTo(PersistentVector.create(arguments.stream().map(ValueProvider::getValues).map(Iterable::iterator).map(Iterator::next).collect(Collectors.toList())).seq()));
            return this;
        }
    }


    public class AggregateInstance extends ListValueProvider {
        public AggregateInstance(Object fromObject) {
        }


    }

    public class SingletonValueProvider extends ListValueProvider {

        public SingletonValueProvider(Object value){
            this.add(value);
        }

        public Object getValue() {
            return this.get(0);
        }
    }

    public class ListValueProvider extends LinkedList<Object> implements ValueProvider {

        @Override
        public List<Object> getValues() {
            return this;
        }

    }

    public interface ValueProvider {
        List<Object> getValues();

        default public boolean isEmpty() {
            return false;
        }
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
