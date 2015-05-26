package unification.tool.module.persistence.orient;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import unification.tool.module.persistence.IPersistenceManagerModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class OrientPersistenceManagerModule implements IPersistenceManagerModule {
    private OrientGraph database;
    //Logger logger = Logger.getLogger(OrientPersistanceManagerModule.class.getName());
    private Map<String, OrientVertexType> classes = new HashMap<>();
    private Map<String, OrientVertexType> associations = new HashMap<>();

    private OrientPersistenceManagerModule(String databasePath) {
        database = new OrientGraph("memory:/" + databasePath, "admin", "admin");
    }

    public static OrientPersistenceManagerModule newInstance(String databasePath) {
        return new OrientPersistenceManagerModule(databasePath);
    }

    @Override
    public void addClass(String className, String extendedClass) {
        String newClassName = getNameForClass(className);
        if (!classes.containsKey(newClassName)) {
            classes.put(newClassName, database.createVertexType(newClassName, extendedClass));
            database.commit();
        }
    }

    //TODO attributes as collections
    @Override
    public void addClassAttribute(String className, String attributeName, Class attributeType) {
        String newClassName = getNameForClass(className);
        String newAttributeName = getNameForAttribute(attributeName);
        if (!classes.containsKey(newClassName)) {
            throw new AssertionError();
        }
        OrientVertexType type = classes.get(newClassName);
        type.createProperty(newAttributeName, OType.getTypeByClass(attributeType));
        database.commit();
    }

    @Override
    public void addAssociation(String associationName, Collection<String> roles, String extendedAssociation) {
        String newAssociationName = getNameForAssociation(associationName);
        if (!associations.containsKey(newAssociationName)) {
            OrientVertexType newAssociation = database.createVertexType(associationName);
            roles.stream().map(roleName -> getNameForRole(associationName, roleName)).forEach(newRoleName -> {
                database.createEdgeType(newRoleName);
                //                TODO check why this does not work
                newAssociation.createProperty(newRoleName, OType.ANY);
            });
            associations.put(associationName, newAssociation);
            database.commit();
        }
    }

    private static String getNameForAssociation(String name) {
        return "association" + name;
    }

    private static String getNameForRole(String associationName, String name) {
        return "role" + name + "ofAssociation" + associationName;
    }

    private static String getNameForAttribute(String name) {
        return "attribute" + name;
    }

    private static String getNameForClass(String name) {
        return "class" + name;
    }

    @Override
    public void shutdownPersitenceManager() {
        database.shutdown();
    }

    @Override
    public Vertex newClassInstance(String className) {
        String newClassName = getNameForClass(className);
        if (!classes.containsKey(newClassName)) {
            throw new AssertionError();
        }
        Vertex newClassInstance = database.addVertex(classes.get(newClassName));
        database.commit();
        return newClassInstance;
    }

    @Override
    public Vertex newAssociationInstance(String associationName) {
        String newAssociationName = getNameForAssociation(associationName);
        if (!associations.containsKey(newAssociationName)) {
            throw new AssertionError();
        }
        Vertex returned = database.addVertex(associations.get(newAssociationName));
        database.commit();
        return returned;
    }

    @Override
    public void addAssociationRole(String associationName, Vertex associationInstance, Vertex targetInstance,
                                   String roleName) {
        String newRoleName = getNameForRole(associationName, roleName);
        OrientEdgeType roleType = database.getEdgeType(newRoleName);
        database.addEdge(roleType, associationInstance, targetInstance, newRoleName);
        associationInstance.setProperty(newRoleName, roleType);
        database.commit();
    }

    @Override
    public void addAttribute(Vertex vertex, String attributeName, Object attributeValue) {
        vertex.setProperty(attributeName, attributeValue);
    }

    @Override
    public Iterable<Vertex> getAssociationInstances(String associationName, Map<String, Object> parameters) {
        String[] keys = parameters.keySet().toArray(new String[parameters.size()]);
        Object[] values = new Object[keys.length];
        IntStream.range(0, keys.length).forEach(index -> values[index] = parameters.get(keys[index]));
        return database.getVertices(getNameForAssociation(associationName), keys, values);
    }

    @Override
    public Iterable<Vertex> getClassInstances(String className, Map<String, Object> parameters) {
        String[] keys = parameters.keySet().toArray(new String[parameters.size()]);
        Object[] values = new Object[keys.length];
        IntStream.range(0, keys.length).forEach(index -> values[index] = parameters.get(keys[index]));
        return database.getVertices(getNameForClass(className), keys, values);
    }

    @Override
    public Iterable<Edge> getInstancesOfRole(String associationName, String role) {
        return database.getEdgesOfClass(getNameForRole(associationName, role));
    }

    //TODO searches for complex neighbourhood
    @Override
    public boolean areAssociated(Vertex associationVertex, String role, Vertex targetVertex) {
        VertexQuery query = associationVertex.query(); //TODO what is this for?
        for (Edge edge : associationVertex.getEdges(Direction.OUT, role)) {
            if (targetVertex == edge.getVertex(Direction.IN)) { //TODO is == enough
                return true;
            }
        }
        return false;
    }
}
