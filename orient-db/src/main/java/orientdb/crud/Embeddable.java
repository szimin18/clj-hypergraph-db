package orientdb.crud;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.config.OServerConfiguration;

/**
 * Created by Puszek_SE on 2015-04-12.
 */
public class Embeddable {
    public static void main(String[] args){

        try {
            OServerConfiguration config = new OServerConfiguration();
            OServer server = OServerMain.create();
            server.startup(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
