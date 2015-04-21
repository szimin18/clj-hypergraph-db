package unification.tool.module.persistance;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Puszek_SE on 2015-04-21.
 */
public interface IPersistanceInstanceModule {

    public Vertex newClassInstance(String className);

    public Vertex newAssociationInstance(String associationName);

    public Edge addAssociationRole(Vertex associationInstance, Vertex targetInstance, String role);

    public void addAttribute(Element element, String attributeName, Object attribute);

    public Iterable<Vertex> getAssociationInstances(String associationName,Map<String,Object> parameters);

    public Iterable<Vertex> getClassInstances(String className,Map<String,Object> parameters);

    public Iterable<Edge> getInstancesOfRole(String role);

    public boolean areAssociated(Vertex associationVertex, String role, Vertex targetVertex);

}
