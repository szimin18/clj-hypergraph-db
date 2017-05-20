package unification.tool.module.intermediate.uml;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import org.apache.log4j.Logger;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.persistence.IPersistenceInstanceManagerModule;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class IntermediateUMLModelManagerModule implements IIntermediateModelManagerModule {

    Logger logger = Logger.getLogger(IntermediateUMLModelManagerModule.class);

    private final IntermediateUMLModelModule modelModule;
    private final IPersistenceInstanceManagerModule persistenceInstanceManagerModule;

    private IntermediateUMLModelManagerModule(IntermediateUMLModelModule modelModule,
                                              IPersistenceInstanceManagerModule persistenceInstanceManagerModule) {
        this.modelModule = modelModule;
        this.persistenceInstanceManagerModule = persistenceInstanceManagerModule;
    }

    public static IntermediateUMLModelManagerModule newInstance(
            IntermediateUMLModelModule modelModule,
            IPersistenceInstanceManagerModule persistenceInstanceManagerModule) {
        return new IntermediateUMLModelManagerModule(modelModule, persistenceInstanceManagerModule);
    }

    public UMLClassInstance newClassInstance(String className, Map<String, Collection<Object>> attributesMap) {
        return new UMLClassInstance(className, attributesMap);
    }

    public UMLAssociationInstance newAssociationInstance(String associationName,
                                                         Map<String, Map<Vertex, Collection<OrientEdge>>> rolesMap) {
        return new UMLAssociationInstance(associationName, rolesMap);
    }

    public UMLClassInstance findInstanceByAttributes(String className, Map<String, Collection<Object>> attributesMap) {
        for (UMLClassInstance classInstance : getClassInstances(className)) {
            if (attributesMap.entrySet().stream().allMatch(
                    attributeMapEntry -> attributeMapEntry.getValue().stream().allMatch(
                            attributeValue -> classInstance.containsAttributeValue(
                                    attributeMapEntry.getKey(), attributeValue)))) {
                return classInstance;
            }
        }
        return null;
    }

    public Iterable<UMLClassInstance> getClassInstances(String className) {
        AtomicInteger c = new AtomicInteger(0);
        persistenceInstanceManagerModule.getClassInstances(className, Collections.emptyMap()).forEach(a -> c.incrementAndGet());
        logger.info("Number of instances of class " + className + ": " + c.get());
        return () -> new Iterator<UMLClassInstance>() {
            private Iterator<Vertex> vertexIterator = persistenceInstanceManagerModule.getClassInstances(className,
                    Collections.emptyMap()).iterator();

            @Override
            public boolean hasNext() {
                return vertexIterator.hasNext();
            }

            @Override
            public UMLClassInstance next() {
                return new UMLClassInstance(className, vertexIterator.next());
            }
        };
    }

    public boolean areAssociated(UMLClassInstance classInstance1, String role1Name, String associationName,
                                 String role2Name, UMLClassInstance classInstance2) {
        return persistenceInstanceManagerModule.areAssociated(
                classInstance1.vertex, role1Name, associationName, role2Name, classInstance2.vertex);
    }

    public IntermediateUMLModelModule getModelModule() {
        return modelModule;
    }

    public final class UMLClassInstance {
        private final Vertex vertex;
        private final String umlClassname;

        //TODO check if instance-part already exists?
        private UMLClassInstance(String className, Map<String, Collection<Object>> attributesMap) {
            logger.trace("New class instance for class: " + className);
            vertex = persistenceInstanceManagerModule.newClassInstance(className);
            umlClassname = className;

            attributesMap.forEach((attributeName, attributeValues) -> attributeValues.forEach(attributeValue ->
                    persistenceInstanceManagerModule.addAttribute(vertex, attributeName, attributeValue)));
        }

        private UMLClassInstance(String className, Vertex vertex) {
            this.vertex = vertex;

            umlClassname = className;

        }

        public <AttributeType> void addAttributeInstance(String attributeName, AttributeType attributeValue) {
            logger.trace("New attribute instance. Attribute name: " + attributeName + ", attribute value: " + attributeValue.toString());
            persistenceInstanceManagerModule.addAttribute(vertex, attributeName, attributeValue);
        }

        public <AttributeType> boolean containsAttributeValue(String attributeName, AttributeType attributeValue) {
            List<Object> attributeValues = persistenceInstanceManagerModule.getAttribute(vertex, attributeName);
            if (attributeValues != null) {

                return attributeValues.stream().filter(value -> attributeValue.getClass().isAssignableFrom(value.getClass()))
                        .map(attributeValue.getClass()::cast).anyMatch(attributeValue::equals);
            }
            return false;
        }

        public <ReturnedType> List<ReturnedType> getAttributeValues(String attributeName,
                                                                    Class<ReturnedType> clazz) {
            List<Object> attributes = persistenceInstanceManagerModule.getAttribute(vertex, attributeName);
            if (attributes != null) {
                return attributes.stream()
                        .filter(element -> clazz.isAssignableFrom(element.getClass())).map(clazz::cast)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }

    public final class UMLAssociationInstance {
        private final String associationName;
        private final Vertex vertex;
        private final Map<String, Map<Vertex, Collection<OrientEdge>>> rolesMap;

        public UMLAssociationInstance(String associationName,
                                      Map<String, Map<Vertex, Collection<OrientEdge>>> rolesMap) {
            logger.trace("New association instance for association: " + associationName);
            this.associationName = associationName;
            vertex = persistenceInstanceManagerModule.newAssociationInstance(associationName);
            this.rolesMap = rolesMap;
        }

        public UMLAssociationInstance(String associationName, Vertex vertex) {
            this.associationName = associationName;
            rolesMap = null;
            this.vertex = vertex;
        }

        public void addRoleInstance(String roleName, UMLClassInstance targetClass) {
            logger.trace("New role instance for association: " + associationName + ", role" + roleName);
            persistenceInstanceManagerModule.addAssociationRole(associationName, vertex, roleName, targetClass.vertex);
        }
    }
}
