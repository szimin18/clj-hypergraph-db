package unification.tool.common.sql;

import unification.tool.common.sql.extent.ExtentSQLModel;
import unification.tool.module.extent.input.uml.sql.InputExtentSQLToUMLModule;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Puszek_SE on 2015-07-28.
 */
public class SQLCommonHelper {

    public static boolean checkCredentials(ExtentSQLModel modelModule,String host){
        boolean validConnection = false;
        try{
            String url = host+modelModule.getSchema();
            String username = modelModule.getUsername();
            String password = modelModule.getPassword();
            java.sql.Connection connection = DriverManager.getConnection(url, username, password);
            connection.close();
            validConnection = true;
        }catch (SQLException e){
            System.err.println("Connection failed! - " + host + modelModule.getSchema());
            e.printStackTrace();
        }

        return validConnection;
    }

}
