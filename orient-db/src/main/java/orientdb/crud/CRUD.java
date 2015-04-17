package orientdb.crud;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Puszek_SE on 2015-04-12.
 */
public class CRUD {

    Map<String, OrientVertexType> classes = new HashMap<>();

    OrientGraphFactory factory;
    OrientGraph database;
    ODatabaseDocumentTx documentTx;
    String address = "plocal:/databases/test";


    public CRUD() {
        /*factory = new OrientGraphFactory(address,"admin","admin").setupPool(1,10);
        database = factory.getTx();
        documentTx = factory.getDatabase(false, true);*/
        database = new OrientGraph(address, "admin", "admin");
    }

    public OrientVertexType addVertexType(String className) {
        OrientVertexType returned = classes.get(className);
        if (returned != null) {
            return returned;
        }
        return database.createVertexType(className);
    }

    public OrientVertexType addExtendingType(String className, String extendedClass) {
        OrientVertexType returned = classes.get(className);
        if (returned != null && returned.getSuperClass() == database.getVertexType(extendedClass)) {
            return returned;
        }
        return database.createVertexType(className, extendedClass);
    }

    public void addClassProperty(String className, String propName, OType type) {
        OrientVertexType modifiedClass = classes.get(className);
        modifiedClass.createProperty(propName, type);
    }

    public Iterable<Vertex> getVertexesForClass(String className) {
        return database.getVerticesOfClass(className, true);
    }

    public Iterable<Vertex> getVertexesForSpecificClass(String className) {
        return database.getVerticesOfClass(className, false);
    }

    public void destroy() {
        System.out.println("DROPPING");
        database.drop();
    }
}
