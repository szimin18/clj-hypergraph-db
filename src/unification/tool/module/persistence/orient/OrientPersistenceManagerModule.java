package unification.tool.module.persistence.orient;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import unification.tool.module.persistence.IPersistenceManagerModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrientPersistenceManagerModule implements IPersistenceManagerModule {
    private OrientGraph database;
    //Logger logger = Logger.getLogger(OrientPersistanceManagerModule.class.getName());
    private Map<String, Object> classes = new HashMap<>();
    private Map<String, Object> associations = new HashMap<>();

    private OrientPersistenceManagerModule(String databasePath) {
        database = new OrientGraph("plocal:/" + databasePath, "admin", "admin");
    }

    public static OrientPersistenceManagerModule newInstance(String databasePath) {
        return new OrientPersistenceManagerModule(databasePath);
    }

    @Override
    public void addClass(String className, String extendedClass) {
        OrientVertexType newClass = (OrientVertexType) classes.get(className);
        if (newClass != null) {
            return;
        }
        newClass = database.createVertexType(className, extendedClass);
        classes.put(className, newClass);
        database.commit();
    }

    //TODO attributes as collections
    @Override
    public void addClassAttribute(String className, String attributeName, Class attributeType) {
        OrientVertexType type = database.getVertexType(className);
        type.createProperty(attributeName, OType.getTypeByClass(attributeType));
        database.commit();
    }

    @Override
    public void addAssociation(String associationName, List<String> roles, String extendedAssociation) {
        OrientVertexType newAssociation = (OrientVertexType) associations.get(associationName);
        if (newAssociation != null) {
            return;
        }
        newAssociation = database.createVertexType(associationName);
        for (String role : roles) {
            database.createEdgeType(role);
            newAssociation.createProperty(role, OType.getTypeByClass(OrientEdgeType.class));
        }
        associations.put(associationName, newAssociation);
        database.commit();
    }

    @Override
    public void shutdownPersitanceManager() {
        database.drop();
    }

    @Override
    public Vertex newClassInstance(String className) {
        Vertex newClassInstance = database.addVertex(classes.get(className));
        database.commit();
        return newClassInstance;
    }

    @Override
    public Vertex newAssociationInstance(String associationName) {
        Vertex returned = database.addVertex(associations.get(associationName));
        database.commit();
        return returned;
    }

    @Override
    public Edge addAssociationRole(Vertex associationInstance, Vertex targetInstance, String role) {
        OrientEdgeType roleType = database.getEdgeType(role);
        Edge associationEdge = database.addEdge(roleType, associationInstance, targetInstance, role);
        associationInstance.setProperty(role, roleType);
        database.commit();
        return associationEdge;
    }

    @Override
    public void addAttribute(Element element, String attributeName, Object attributeValue) {
        element.setProperty(attributeName, attributeValue);
    }

    @Override
    public Iterable<Vertex> getAssociationInstances(String associationName, Map<String, Object> parameters) {
        String[] keys = (String[]) parameters.keySet().toArray();
        Object[] values = parameters.values().toArray();
        return database.getVertices(associationName, keys, values);
    }

    @Override
    public Iterable<Vertex> getClassInstances(String className, Map<String, Object> parameters) {
        String[] keys = (String[]) parameters.keySet().toArray();
        Object[] values = parameters.values().toArray();
        return database.getVertices(className, keys, values);
    }

    @Override
    public Iterable<Edge> getInstancesOfRole(String role) {
        return database.getEdgesOfClass(role);
    }

    //TODO searches for complex neighbourhood
    @Override
    public boolean areAssociated(Vertex associationVertex, String role, Vertex targetVertex) {
        VertexQuery query = associationVertex.query();
        for (Edge e : associationVertex.getEdges(Direction.OUT, role)) {
            if (targetVertex == e.getVertex(Direction.IN)) {
                return true;
            }
        }
        return false;
    }

}
