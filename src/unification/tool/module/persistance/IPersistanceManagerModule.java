package unification.tool.module.persistance;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

import java.util.List;

public interface IPersistanceManagerModule {

    public OrientVertexType addClass(String className,String extendedClass);

    public OrientEdgeType addAssociation(String associationName,List<String> roles, String extendedAssociation);

    public OrientVertexType addClassAttribute(String className,String attributeName,OType attributeType);

    public OrientEdgeType addAssociationRole(Vertex associationInstance,Vertex targetInstance, String role);

    public void shutdownPersitanceManager();

}
