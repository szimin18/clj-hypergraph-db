package unification.tool.module.extent.input.uml.sql;

import unification.tool.module.extent.input.IInputExtentModelManagerModule;
import unification.tool.module.model.sql.Table;

import java.sql.*;

public class InputExtentSQLToUMLManagerModule implements IInputExtentModelManagerModule {

    InputExtentSQLToUMLModule modelModule;

    private static String host = "jdbc:mysql://localhost:3306/";
    private static int fetchSize = 50;

    private static final String selectQuery = "SELECT * FROM ";


    private final String schema;
    private final String username;
    private final String password;


    private InputExtentSQLToUMLManagerModule(InputExtentSQLToUMLModule modelModule){
        this.modelModule = modelModule;
        schema = modelModule.getSchema();
        username = modelModule.getUsername();
        password = modelModule.getPassword();
    }

    private static boolean checkCredentials(InputExtentSQLToUMLModule modelModule){
        boolean validConnection = false;
        try{
            String url = host+modelModule.getSchema();
            String username = modelModule.getUsername();
            String password = modelModule.getPassword();
            java.sql.Connection connection = DriverManager.getConnection(url,username,password);
            connection.close();
            validConnection = true;
        }catch (SQLException e){
            System.err.println("Connection failed! - " + host + modelModule.getSchema());
            e.printStackTrace();
        }

        return validConnection;
    }

    public static IInputExtentModelManagerModule newInstance(InputExtentSQLToUMLModule modelModule) {
        InputExtentSQLToUMLManagerModule managerModule = null;
        if(checkCredentials(modelModule)){
            managerModule = new InputExtentSQLToUMLManagerModule(modelModule);
        }
        return managerModule;
    }

    private void iterateOverTables(Connection connection) throws SQLException {
        for(InputExtentTable extentTable : modelModule.getTables()){
            Table table = extentTable.getTable();
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery+table.getName()+";");
            selectStatement.setFetchSize(fetchSize);

            ResultSet rs = selectStatement.executeQuery();


            while (rs.next()){
                for(Instance instance : extentTable.getInstances()){
                    modelModule.addInstance(instance,rs);
                }
            }
        }
    }

    @Override public void readInput() {
        String url = host+schema;
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,username,password);
            iterateOverTables(connection);

            connection.close();
        }catch (SQLException e){
            System.err.println("Connection failed! - " + host + modelModule.getSchema());
            e.printStackTrace();
        }
    }
}
