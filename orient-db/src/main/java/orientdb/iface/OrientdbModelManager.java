package orientdb.iface;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

import java.util.List;

/**
 * Created by Puszek_SE on 2015-04-21.
 */
public interface OrientdbModelManager {

    public OrientVertexType addClass(String className,String extendedClass);

    public OrientEdgeType addAssociation(String associationName,List<String> roles, String extendedAssociation);

    public OrientVertexType addClassAttribute(String className,String attributeName,OType attributeType);

    public OrientEdgeType addAssociationRole(Vertex associationInstance,Vertex targetInstance, String role);



}
