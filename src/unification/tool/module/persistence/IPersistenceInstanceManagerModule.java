package unification.tool.module.persistence;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;

import java.util.Map;

public interface IPersistenceInstanceManagerModule {

    public Vertex newClassInstance(String className);

    public Vertex newAssociationInstance(String associationName);

    public void addAssociationRole(String associationName, Vertex associationInstance, Vertex targetInstance, String role);

    public void addAttribute(Vertex vertex, String attributeName, Object attributeValue);

    public Iterable<Vertex> getAssociationInstances(String associationName, Map<String, Object> parameters);

    public Iterable<Vertex> getClassInstances(String className, Map<String, Object> parameters);

    public Iterable<Edge> getInstancesOfRole(String associationName, String role);

    public boolean areAssociated(Vertex associationVertex, String role, Vertex targetVertex);

}
