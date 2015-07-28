package unification.tool.module.intermediate.uml;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.persistence.IPersistenceInstanceManagerModule;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class IntermediateUMLModelManagerModule implements IIntermediateModelManagerModule {
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
        System.out.println("Number of instances of class " + className + ": " + c.get());
        return () -> new Iterator<UMLClassInstance>() {
            private Iterator<Vertex> vertexIterator = persistenceInstanceManagerModule.getClassInstances(className,
                    Collections.emptyMap()).iterator();

            @Override public boolean hasNext() {
                return vertexIterator.hasNext();
            }

            @Override public UMLClassInstance next() {
                return new UMLClassInstance(className, vertexIterator.next());
            }
        };
    }

    public boolean areAssociated(UMLClassInstance classInstance1, String role1Name, String associationName,
                                 String role2Name, UMLClassInstance classInstance2) {
        return false; //TODO implement
    }

    public IntermediateUMLModelModule getModelModule() {
        return modelModule;
    }

    public final class UMLClassInstance {
        private final Vertex vertex;
        private final Map<String, Collection<Object>> attributesMap;

        //TODO check if instance-part already exists?
        private UMLClassInstance(String className, Map<String, Collection<Object>> attributesMap) {
            System.out.format("New class instance for class: %s\n", className);
            vertex = persistenceInstanceManagerModule.newClassInstance(className);

            this.attributesMap = new HashMap<>(attributesMap);

            attributesMap.forEach((attributeName, attributeValues) -> attributeValues.forEach(attributeValue ->
                    persistenceInstanceManagerModule.addAttribute(vertex, attributeName, attributeValue)));
        }

        private UMLClassInstance(String className, Vertex vertex) {
            this.vertex = vertex;

            attributesMap = modelModule.getClassByName(className).getAttributesNames().stream().collect(
                    Collectors.<String, String, Collection<Object>>toMap(
                            attributeName -> attributeName,
                            attributeName -> {
                                Object propertyValue = vertex.getProperty(attributeName);
                                if (propertyValue != null && propertyValue instanceof Collection) {
                                    return (Collection) propertyValue;
                                } else {
                                    return Collections.emptyList();
                                }
                            }));
        }

        public <AttributeType> void addAttributeInstance(String attributeName, AttributeType attributeValue) {
            System.out.printf("New attribute instance. Attribute name: %s, attribute value: %s\n", attributeName,
                    attributeValue.toString());
            Collection<Object> attributeValuesList = attributesMap.get(attributeName);
            if (attributeValuesList == null) {
                attributeValuesList = new ArrayList<>();
                attributesMap.put(attributeName, attributeValuesList);
            }
            attributeValuesList.add(attributeValue);
            persistenceInstanceManagerModule.addAttribute(vertex, attributeName, attributeValuesList);
        }

        public <AttributeType> boolean containsAttributeValue(String attributeName, AttributeType attributeValue) {
            return attributesMap.containsKey(attributeName) &&
                    attributesMap.get(attributeName).contains(attributeValue);
        }

        public <ReturnedType> List<ReturnedType> getAttributeValues(String attributeName,
                                                                          Class<ReturnedType> clazz) {
            return attributesMap.get(attributeName).stream()
                    .filter(element -> clazz.isAssignableFrom(element.getClass())).map(clazz::cast)
                    .collect(Collectors.toList());
        }
    }

    public final class UMLAssociationInstance {
        private final String associationName;
        private final Vertex vertex;
        private final Map<String, Map<Vertex, Collection<OrientEdge>>> rolesMap;

        public UMLAssociationInstance(String associationName,
                                      Map<String, Map<Vertex, Collection<OrientEdge>>> rolesMap) {
            System.out.format("New association instance for association: %s\n", associationName);
            this.associationName = associationName;
            vertex = persistenceInstanceManagerModule.newAssociationInstance(associationName);
            this.rolesMap = rolesMap;
        }

        public UMLAssociationInstance(String associationName, Vertex vertex) {
            this.associationName = associationName;
            rolesMap = null;
            this.vertex = vertex;
        }

        public void addRoleInstance(String roleName, UMLClassInstance targetClassVertex) {
            System.out.format("New role instance for association: %s, role %s\n", associationName, roleName);
            persistenceInstanceManagerModule.addAssociationRole(
                    associationName, vertex, roleName, targetClassVertex.vertex);
        }
    }
}
