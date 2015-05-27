package unification.tool.module.extent.input.uml.xml;

import clojure.lang.IPersistentVector;
import clojure.lang.Keyword;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.extent.input.IInputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.IIntermediateModelModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule.UMLAssociationInstance;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule.UMLClassInstance;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule.XMLAttribute;
import unification.tool.module.model.xml.XMLDataModelModule.XMLToken;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InputExtentXMLToUMLModule implements IInputExtentModelModule {
    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelModule intermediateModelModule;
    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final XMLToUMLToken rootNode;
    private final String filePath;

    private InputExtentXMLToUMLModule(XMLDataModelModule dataModelModule, String extentFilePath,
                                      IntermediateUMLModelModule intermediateModelModule,
                                      IntermediateUMLModelManagerModule intermediateModelManagerModule,
                                      IPersistentVector dataSourceAccess) {
        this.intermediateModelModule = intermediateModelModule;
        this.intermediateModelManagerModule = intermediateModelManagerModule;

        if (dataSourceAccess.length() != 1) {
            throw new IllegalStateException("Invalid access vector: wrong size");
        }
        Object firstElement = dataSourceAccess.valAt(0);
        if (!(firstElement instanceof String)) {
            throw new IllegalStateException("Invalid access vector: first element not a String");
        }
        filePath = (String) firstElement;

        rootNode = new XMLToUMLToken(dataModelModule.getRootNode());

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.input.uml.xml.parser", extentFilePath);

        PARSER.findAllItemsByType(parsedConfiguration, "foreach").forEach(forEachMap -> {
            IPersistentVector path = PARSER.vectorFromMap(forEachMap, "path");
            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-instance").forEach(addInstanceMap -> {
                String className = PARSER.keywordNameFromMap(addInstanceMap, "name");
                XMLToUMLClassInstanceManager classInstanceManager = new XMLToUMLClassInstanceManager(className);
                getNodeInPath(rootNode, calculatePath(path)).attachAddClassInstanceInformation(classInstanceManager);
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping").forEach(mappingMap -> {
                    IPersistentVector mappingPath = PARSER.vectorFromMap(mappingMap, "path");
                    String attributeName = PARSER.keywordNameFromMap(mappingMap, "name");
                    if (!PARSER.booleanFromMap(mappingMap, "pk-mapping")) {
                        List<String> calculatedPath = calculatePath(path, mappingPath);
                        if (calculatedPath.isEmpty()) {
                            throw new AssertionError();
                        }
                        String lastOfPath = calculatedPath.get(calculatedPath.size() - 1);
                        calculatedPath.remove(calculatedPath.size() - 1);
                        XMLToUMLToken node = getNodeInPath(rootNode, calculatedPath);
                        XMLToUMLAttribute attribute = node.getAttributeByName(lastOfPath);
                        if (attribute == null) {
                            node.attachAddAttributeInstanceFromTextInformation(classInstanceManager, attributeName);
                        } else {
                            attribute.attachAddAttributeInstanceInformation(classInstanceManager, attributeName);
                        }
                    }
                });
            });
        });

        PARSER.findAllItemsByType(parsedConfiguration, "foreach").forEach(forEachMap -> {
            IPersistentVector path = PARSER.vectorFromMap(forEachMap, "path");
            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-association").forEach(addAssociationMap -> {
                String associationName = PARSER.keywordNameFromMap(addAssociationMap, "name");
                XMLToUMLAssociationInstanceManager associationInstanceManager =
                        new XMLToUMLAssociationInstanceManager(associationName);
                getNodeInPath(rootNode, calculatePath(path))
                        .attachAddAssociationInstanceInformation(associationInstanceManager);
                PARSER.findAllItemsFromMapValueByType(addAssociationMap, "mappings", "mapping").forEach(mappingMap -> {
                    IPersistentVector mappingPath = PARSER.vectorFromMap(mappingMap, "path");
                    String roleName = PARSER.keywordNameFromMap(mappingMap, "name");
                    List<String> calculatedPath = calculatePath(path, mappingPath);
                    if (calculatedPath.isEmpty()) {
                        throw new AssertionError();
                    }
                    if (PARSER.booleanFromMap(mappingMap, "pk-mapping")) {
                        String lastOfPath = calculatedPath.get(calculatedPath.size() - 1);
                        calculatedPath.remove(calculatedPath.size() - 1);
                        XMLToUMLToken node = getNodeInPath(rootNode, calculatedPath);
                        XMLToUMLAttribute attribute = node.getAttributeByName(lastOfPath);
                        if (attribute == null) {
                            node.attachAddRoleInstanceFromTextInformation(associationInstanceManager, roleName);
                        } else {
                            attribute.attachAddRoleInstanceInformation(associationInstanceManager, roleName);
                        }
                    } else {
                        XMLToUMLToken node = getNodeInPath(rootNode, calculatedPath);
                        String targetClassOfRole = intermediateModelModule.getAssociationByName(associationName)
                                .getRoleByName(roleName).getTargetClass();
                        Optional<XMLToUMLClassInstanceManager> classInstanceManager =
                                node.getAddClassInstanceList().stream().filter(
                                        addClassInstance -> intermediateModelModule.getClassByName(
                                                addClassInstance.getClassName()).instanceOf(
                                                intermediateModelModule.getClassByName(targetClassOfRole))
                                ).findFirst();
                        if (!classInstanceManager.isPresent()) {
                            throw new AssertionError("No matching instance manager for role path");
                        }
                        getNodeInPath(rootNode, calculatePathIgnoreUps(path, mappingPath))
                                .attachAddRoleInstanceInformation(
                                        associationInstanceManager, classInstanceManager.get(), roleName);
                    }
                });
            });
        });
    }

    private XMLToUMLToken getNodeInPath(XMLToUMLToken parentNode, List<String> path) {
        return path.stream().reduce(
                parentNode,
                XMLToUMLToken::getChildByName,
                (e1, e2) -> {
                    throw new AssertionError();
                });
    }

    private List<String> calculatePath(IPersistentVector... paths) {
        return calculatePath(currentList -> {
                    if (currentList.isEmpty()) {
                        throw new AssertionError();
                    } else {
                        currentList.remove(currentList.size() - 1);
                    }
                },
                paths);
    }

    private List<String> calculatePathIgnoreUps(IPersistentVector... paths) {
        return calculatePath(null, paths);
    }

    private List<String> calculatePath(Consumer<List<String>> upDirectoryHandler, IPersistentVector[] paths) {
        List<String> result = new ArrayList<>();
        for (IPersistentVector path : paths) {
            for (int i = 0; i < path.length(); i++) {
                Object pathElement = path.valAt(i);
                if (pathElement instanceof Keyword) {
                    String pathElementName = ((Keyword) pathElement).getName();
                    if (pathElementName.equals("..")) {
                        if (upDirectoryHandler != null) {
                            upDirectoryHandler.accept(result);
                        }
                    } else {
                        result.add(pathElementName);
                    }
                } else {
                    throw new AssertionError();
                }
            }
        }
        return result;
    }

    public static InputExtentXMLToUMLModule newInstance(
            IDataModelModule dataModelModule, String extentFilePath, IIntermediateModelModule intermediateModelModule,
            IIntermediateModelManagerModule intermediateModelManagerModule, IPersistentVector dataSourceAccess) {
        if (dataModelModule instanceof XMLDataModelModule) {
            if (intermediateModelManagerModule instanceof IntermediateUMLModelManagerModule) {
                if (intermediateModelModule instanceof IntermediateUMLModelModule) {
                    return new InputExtentXMLToUMLModule((XMLDataModelModule) dataModelModule, extentFilePath,
                            (IntermediateUMLModelModule) intermediateModelModule,
                            (IntermediateUMLModelManagerModule) intermediateModelManagerModule, dataSourceAccess);
                } else {
                    throw new IllegalArgumentException("An instance of IntermediateUMLModelModule should be passed");
                }
            } else {
                throw new IllegalArgumentException("An instance of IntermediateUMLModelManagerModule should be passed");
            }
        } else {
            throw new IllegalArgumentException("An instance of XMLDataModelModule should be passed");
        }
    }

    private boolean isSuperclassOf(String superclassName, String subclassName) {
        return intermediateModelModule.getClassByName(subclassName)
                .instanceOf(intermediateModelModule.getClassByName(superclassName));
    }

    public IntermediateUMLModelManagerModule getIntermediateModelManagerModule() {
        return intermediateModelManagerModule;
    }

    public XMLToUMLToken getRootNode() {
        return rootNode;
    }

    public String getFilePath() {
        return filePath;
    }

    static final class XMLToUMLToken {
        private final Map<String, XMLToUMLToken> children;
        private final Map<String, XMLToUMLAttribute> attributes;
        private final String name;
        private final String tokenStringName;
        private final String textName;
        private final List<XMLToUMLClassInstanceManager> addClassInstanceList = new ArrayList<>();
        private final Map<XMLToUMLClassInstanceManager, Collection<String>> addAttributeInstanceFromTextList =
                new HashMap<>();
        private final List<XMLToUMLAssociationInstanceManager> addAssociationInstanceList = new ArrayList<>();
        private final Map<XMLToUMLAssociationInstanceManager, Collection<String>> addRoleInstanceFromTextList =
                new HashMap<>();
        private final Map<XMLToUMLAssociationInstanceManager, Map<XMLToUMLClassInstanceManager, Collection<String>>>
                addRoleInstanceList = new HashMap<>();

        private XMLToUMLToken(XMLToken original) {
            children = original.getChildren().values().stream().map(XMLToUMLToken::new)
                    .collect(Collectors.toMap(t -> t.name, t -> t));
            attributes = original.getAttributes().values().stream().map(XMLToUMLAttribute::new)
                    .collect(Collectors.toMap(t -> t.name, t -> t));
            name = original.getName();
            tokenStringName = original.getTokenStringName();
            textName = original.getTextName();
        }

        Collection<XMLToUMLToken> getChildrenValues() {
            return children.values();
        }

        XMLToUMLToken getChildByName(String childName) {
            return children.get(childName);
        }

        Collection<XMLToUMLAttribute> getAttributesValues() {
            return attributes.values();
        }

        XMLToUMLAttribute getAttributeByName(String attributeName) {
            return attributes.get(attributeName);
        }

        String getTokenStringName() {
            return tokenStringName;
        }

        String getTextName() {
            return textName;
        }

        private void attachAddClassInstanceInformation(XMLToUMLClassInstanceManager manager) {
            addClassInstanceList.add(manager);
        }

        List<XMLToUMLClassInstanceManager> getAddClassInstanceList() {
            return addClassInstanceList;
        }

        private void attachAddAttributeInstanceFromTextInformation(XMLToUMLClassInstanceManager manager,
                                                                   String attributeName) {
            if (!addAttributeInstanceFromTextList.containsKey(manager)) {
                addAttributeInstanceFromTextList.put(manager, new ArrayList<>());
            }
            addAttributeInstanceFromTextList.get(manager).add(attributeName);
        }

        Map<XMLToUMLClassInstanceManager, Collection<String>> getAddAttributeInstanceFromTextList() {
            return addAttributeInstanceFromTextList;
        }

        private void attachAddAssociationInstanceInformation(XMLToUMLAssociationInstanceManager manager) {
            addAssociationInstanceList.add(manager);
        }

        List<XMLToUMLAssociationInstanceManager> getAddAssociationInstanceList() {
            return addAssociationInstanceList;
        }

        private void attachAddRoleInstanceFromTextInformation(XMLToUMLAssociationInstanceManager manager,
                                                              String roleName) {
            if (!addRoleInstanceFromTextList.containsKey(manager)) {
                addRoleInstanceFromTextList.put(manager, new ArrayList<>());
            }
            addRoleInstanceFromTextList.get(manager).add(roleName);
        }

        Map<XMLToUMLAssociationInstanceManager, Collection<String>> getAddRoleInstanceFromTextList() {
            return addRoleInstanceFromTextList;
        }

        private void attachAddRoleInstanceInformation(XMLToUMLAssociationInstanceManager manager,
                                                      XMLToUMLClassInstanceManager targetClassManager,
                                                      String roleName) {
            if (!addRoleInstanceList.containsKey(manager)) {
                addRoleInstanceList.put(manager, new HashMap<>());
            }
            if (!addRoleInstanceList.get(manager).containsKey(targetClassManager)) {
                addRoleInstanceList.get(manager).put(targetClassManager, new ArrayList<>());
            }
            addRoleInstanceList.get(manager).get(targetClassManager).add(roleName);
        }

        public Map<XMLToUMLAssociationInstanceManager,
                Map<XMLToUMLClassInstanceManager, Collection<String>>> getAddRoleInstanceList() {
            return addRoleInstanceList;
        }
    }

    static final class XMLToUMLAttribute {
        private final String name;
        private final String attributeName;
        private final Map<XMLToUMLClassInstanceManager, Collection<String>> addAttributeInstanceList = new HashMap<>();
        private final Map<XMLToUMLAssociationInstanceManager, Collection<String>> addRoleInstanceList = new HashMap<>();

        private XMLToUMLAttribute(XMLAttribute original) {
            name = original.getName();
            attributeName = original.getAttributeName();
        }

        public String getAttributeName() {
            return attributeName;
        }

        private void attachAddAttributeInstanceInformation(XMLToUMLClassInstanceManager manager, String attributeName) {
            if (!addAttributeInstanceList.containsKey(manager)) {
                addAttributeInstanceList.put(manager, new ArrayList<>());
            }
            addAttributeInstanceList.get(manager).add(attributeName);
        }

        Map<XMLToUMLClassInstanceManager, Collection<String>> getAddAttributeInstanceList() {
            return addAttributeInstanceList;
        }

        private void attachAddRoleInstanceInformation(XMLToUMLAssociationInstanceManager manager, String roleName) {
            if (!addRoleInstanceList.containsKey(manager)) {
                addRoleInstanceList.put(manager, new ArrayList<>());
            }
            addRoleInstanceList.get(manager).add(roleName);
        }
    }

    final class XMLToUMLClassInstanceManager {
        private final String className;
        private UMLClassInstance classInstance = null;

        private XMLToUMLClassInstanceManager(String className) {
            this.className = className;
        }

        public void newInstance() {
            classInstance = intermediateModelManagerModule.newClassInstance(className, Collections.emptyMap());
        }

        public <AttributeType> void addAttributeInstance(String attributeName, AttributeType attributeValue) {
            classInstance.addAttributeInstance(attributeName, attributeValue);
        }

        public String getClassName() {
            return className;
        }

        public UMLClassInstance getClassInstance() {
            return classInstance;
        }
    }

    final class XMLToUMLAssociationInstanceManager {
        private final String associationName;
        private UMLAssociationInstance associationInstance = null;

        XMLToUMLAssociationInstanceManager(String associationName) {
            this.associationName = associationName;
        }

        public void newInstance() {
            associationInstance = intermediateModelManagerModule.newAssociationInstance(
                    associationName, Collections.emptyMap());
        }

        public void addRoleInstance(String roleName, UMLClassInstance classInstance) {
            associationInstance.addRoleInstance(roleName, classInstance);
        }
    }
}
