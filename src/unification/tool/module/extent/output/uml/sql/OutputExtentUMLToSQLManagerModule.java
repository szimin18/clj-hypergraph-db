package unification.tool.module.extent.output.uml.sql;

import org.apache.log4j.Logger;
import unification.tool.common.sql.SQLCommonHelper;
import unification.tool.module.extent.output.IOutputExtentModelManagerModule;
import unification.tool.module.extent.output.uml.sql.OutputExtentUMLToSQLModule.ValueProvider;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.model.sql.Mapping;
import unification.tool.module.model.sql.Table;

import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OutputExtentUMLToSQLManagerModule implements IOutputExtentModelManagerModule {

    static Logger logger = Logger.getLogger(OutputExtentUMLToSQLManagerModule.class);

    OutputExtentUMLToSQLModule modelModule;
    IntermediateUMLModelManagerModule intermediateModelManager;

    private static String host = "jdbc:postgresql://localhost:5432/";

    private static final String insertQuery = "INSERT INTO #table ( #columns ) VALUES ( #values );";


    private final String schema;
    private final String username;
    private final String password;


    private OutputExtentUMLToSQLManagerModule(OutputExtentUMLToSQLModule modelModule) {
        this.modelModule = modelModule;
        intermediateModelManager = modelModule.getIntermediateModelManagerModule();
        schema = modelModule.getSchema();
        username = modelModule.getUsername();
        password = modelModule.getPassword();
    }

    public static IOutputExtentModelManagerModule newInstance(OutputExtentUMLToSQLModule modelModule) {
        OutputExtentUMLToSQLManagerModule managerModule = null;
        if (SQLCommonHelper.checkCredentials(modelModule, host)) {
            managerModule = new OutputExtentUMLToSQLManagerModule(modelModule);
        }
        return managerModule;
    }

    private void iterateOverInstances(Connection connection) throws SQLException {
        Set<OutputExtentTable> extentTables = modelModule.getTables();
        Set<OutputExtentInnerTable> extentInnerTables = modelModule.getInnerTable();
        Map<String, ValueProvider> variables = modelModule.getVariables();

        for (OutputExtentTable outputExtentTable : extentTables) {
            Table table = outputExtentTable.getTable();
            String umlClassname = outputExtentTable.getUmlClassname();

            Iterable<IntermediateUMLModelManagerModule.UMLClassInstance> instances = intermediateModelManager.getClassInstances(umlClassname);
            Iterator<IntermediateUMLModelManagerModule.UMLClassInstance> instanceIterator = instances.iterator();

            while (instanceIterator.hasNext()) {
                IntermediateUMLModelManagerModule.UMLClassInstance instance = instanceIterator.next();

                StringBuilder columnsBuilder = new StringBuilder();
                StringBuilder valuesBuilder = new StringBuilder();

                List<Mapping> mappings = outputExtentTable.getMappings();

                for (int i = 0; i < mappings.size(); i++) {
                    Mapping mapping = mappings.get(i);
                    String attributeName = mapping.getAttributeName();

                    if (intermediateModelManager.getModelModule().getClassByName(umlClassname).getAttributesNames().contains(attributeName)) {
                        List<Object> attributeValues = instance.getAttributeValues(mapping.getAttributeName(), Object.class);
                        if (null != attributeValues && attributeValues.size() > 0) {
                            columnsBuilder.append(mapping.getColumnName()).append(",");
                            String attributeValue = String.valueOf(attributeValues.get(attributeValues.size() - 1));
                            attributeValue = attributeValue.replace("\'", "\'\'");
                            valuesBuilder.append("'").append(attributeValue).append("',");
                        }
                    } else if (variables.containsKey(attributeName)) {
                        columnsBuilder.append(mapping.getColumnName()).append(",");
                        List<Object> attributeValues = variables.get(attributeName).getValues();
                        String attributeValue = String.valueOf(attributeValues.get(attributeValues.size() - 1));
                        valuesBuilder.append("'").append(attributeValue).append("',");
                    }
                }


                String columns = columnsBuilder.toString();
                String values = valuesBuilder.toString();

                writeQuery(columns, values, table, connection);
            }
        }

        for (OutputExtentInnerTable innerTable : extentInnerTables) {
            Table table = innerTable.getTable();
            String umlClassname = innerTable.getUmlClassname();
            String mainAttribute = innerTable.getMainAttribute();

            if (intermediateModelManager.getModelModule().getClassByName(umlClassname).getAttributesNames().contains(mainAttribute)) {
                Iterable<IntermediateUMLModelManagerModule.UMLClassInstance> instances = intermediateModelManager.getClassInstances(umlClassname);
                Iterator<IntermediateUMLModelManagerModule.UMLClassInstance> instanceIterator = instances.iterator();
                while (instanceIterator.hasNext()) {
                    IntermediateUMLModelManagerModule.UMLClassInstance instance = instanceIterator.next();

                    if (!instance.getAttributeValues(mainAttribute, String.class).isEmpty()) {

                        StringBuilder columnsBuilder = new StringBuilder();
                        StringBuilder valuesBuilder = new StringBuilder();

                        List<Mapping> mappings = innerTable.getMappings().stream().filter(mapping -> !mapping.getAttributeName().equals(mainAttribute)).collect(Collectors.toList());


                        for (int i = 0; i < mappings.size(); i++) {
                            Mapping mapping = mappings.get(i);
                            String attributeName = mapping.getAttributeName();

                            if (intermediateModelManager.getModelModule().getClassByName(umlClassname).getAttributesNames().contains(attributeName)) {
                                List<String> attributeValues = instance.getAttributeValues(mapping.getAttributeName(), String.class);
                                if (null != attributeValues && attributeValues.size() > 0) {
                                    columnsBuilder.append(mapping.getColumnName()).append(",");
                                    String attributeValue = String.valueOf(attributeValues.get(attributeValues.size() - 1));
                                    attributeValue = attributeValue.replace("\'", "\'\'");
                                    valuesBuilder.append("'").append(attributeValue).append("',");
                                }
                            } else if (variables.containsKey(attributeName)) {
                                columnsBuilder.append(mapping.getColumnName()).append(",");
                                List<Object> attributeValues = variables.get(attributeName).getValues();
                                String attributeValue = String.valueOf(attributeValues.get(attributeValues.size() - 1));
                                valuesBuilder.append("'").append(attributeValue).append("',");
                            }
                        }

                        List<Mapping> mainMappings = getMainMappings(innerTable, mainAttribute);
                        for (Mapping mapping : mainMappings) {
                            columnsBuilder.append(mapping.getColumnName()).append(",");
                        }

                        String columns = columnsBuilder.toString();

                        for (Object attribute : instance.getAttributeValues(mainAttribute, Object.class)) {
                            StringBuilder attributeValuesBuilder = new StringBuilder().append(valuesBuilder.toString());

                            String attributeValue = String.valueOf(attribute);
                            attributeValue = attributeValue.replace("\'", "\'\'");

                            for (Mapping mapping : mainMappings) {
                                attributeValuesBuilder.append("'").append(attributeValue).append("',");
                            }

                            writeQuery(columns, attributeValuesBuilder.toString(), table, connection);
                        }
                    }
                }
            }
        }
    }

    private List<Mapping> getMainMappings(OutputExtentInnerTable innerTable, String mainAttribute) {
        return innerTable.getMappings().stream().filter(mapping -> mainAttribute.equals(mapping.getAttributeName())).collect(Collectors.toList());
    }

    private void writeQuery(String columns, String values, Table table, Connection connection) throws SQLException {
        columns = columns.endsWith(",") ? columns.substring(0, columns.length() - 1) : columns;
        values = values.endsWith(",") ? values.substring(0, values.length() - 1) : values;

        if (columns.length() == 0 || values.length() == 0) {
            logger.warn("Empty query has been passed!");
            return;
        }

        String query = insertQuery;
        query = query.replace("#table", table.getName());
        query = query.replace("#columns", columns);
        query = query.replace("#values", values);

        logger.debug(query);
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            logger.warn(query);
            logger.warn(e.getMessage());
        }
    }

    @Override
    public void writeOutput() {
        String url = host + schema;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);

            logger.info("SQL output-write start");
            iterateOverInstances(connection);
            logger.info("SQL output-write end");

            connection.close();
        } catch (SQLException e) {
            System.err.println("Connection failed! - " + host + modelModule.getSchema());
            e.printStackTrace();
        }
    }
}
