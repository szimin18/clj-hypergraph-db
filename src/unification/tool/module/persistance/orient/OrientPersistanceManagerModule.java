package unification.tool.module.persistance.orient;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import unification.tool.module.persistance.IPersistanceManagerModule;

import java.util.List;

public class OrientPersistanceManagerModule implements IPersistanceManagerModule {
    private OrientGraph database;

    private OrientPersistanceManagerModule(String databasePath) {
        database = new OrientGraph("plocal:/"+databasePath, "admin", "admin");
    }

    public static IPersistanceManagerModule newInstance(String databasePath) {
        return new OrientPersistanceManagerModule(databasePath);
    }

    @Override
    public OrientVertexType addClass(String className, String extendedClass) {
        return null;
    }

    @Override
    public OrientEdgeType addAssociation(String associationName, List<String> roles, String extendedAssociation) {
        return null;
    }

    @Override
    public OrientVertexType addClassAttribute(String className, String attributeName, OType attributeType) {
        return null;
    }

    @Override
    public OrientEdgeType addAssociationRole(Vertex associationInstance, Vertex targetInstance, String role) {
        return null;
    }

    @Override
    public void shutdownPersitanceManager() {
        database.drop();
    }
}
