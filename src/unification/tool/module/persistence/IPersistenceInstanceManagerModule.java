package unification.tool.module.persistence;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.Map;

public interface IPersistenceInstanceManagerModule {

    public Vertex newClassInstance(String className);

    public Vertex newAssociationInstance(String associationName);

    public void addAssociationRole(String associationName, Vertex associationInstance, String roleName,
                                   Vertex targetInstance);

    public void addAttribute(Vertex vertex, String attributeName, Object attributeValue);

    public Iterable<Vertex> getAssociationInstances(String associationName, Map<String, Object> parameters);

    public Iterable<Vertex> getClassInstances(String className, Map<String, Object> parameters);

    public Iterable<Edge> getInstancesOfRole(String associationName, String role);

    public boolean servesRole(Vertex associationVertex, String role, Vertex targetVertex);

    public boolean areAssociated(Vertex vertex1, String role1, String associationName, String role2, Vertex vertex2);

}
