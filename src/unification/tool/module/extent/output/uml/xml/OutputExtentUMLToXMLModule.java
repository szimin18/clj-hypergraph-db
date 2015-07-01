package unification.tool.module.extent.output.uml.xml;

import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.Keyword;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.extent.output.IOutputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule.UMLClassInstance;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule.XMLAttribute;
import unification.tool.module.model.xml.XMLDataModelModule.XMLToken;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OutputExtentUMLToXMLModule implements IOutputExtentModelModule {
    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final UMLToXMLToken rootNode;
    private final List<ModelItem> rootModelItems;
    private final String filePath;
    private final Map<String, IFn> functionsMap;

    private OutputExtentUMLToXMLModule(XMLDataModelModule dataModelModule, String extentFilePath,
                                       IntermediateUMLModelManagerModule intermediateModelManagerModule,
                                       IPersistentVector dataSourceAccess) {
        this.intermediateModelManagerModule = intermediateModelManagerModule;

        if (dataSourceAccess.length() != 1) {
            throw new IllegalStateException("Invalid access vector: wrong size");
        }
        Object firstElement = dataSourceAccess.valAt(0);
        if (!(firstElement instanceof String)) {
            throw new IllegalStateException("Invalid access vector: first element not a String");
        }
        filePath = (String) firstElement;

        rootNode = new UMLToXMLToken(dataModelModule.getRootNode(), null);

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.output.uml.xml.parser", extentFilePath);

        functionsMap = new HashMap<>();
        rootModelItems = new ArrayList<>();

        List<Object> functionMaps = PARSER.findAllItemsByType(parsedConfiguration, "function");
        functionMaps.forEach(functionMap -> {
            String functionName = PARSER.keywordNameFromMap(functionMap, "name");
            IFn functionBody = PARSER.iFnFromMap(functionMap, "body");
            functionsMap.put(functionName, functionBody);
        });
        PARSER.seqableToList(parsedConfiguration).forEach(itemMap -> {
            if (!functionMaps.contains(itemMap)) {
                rootModelItems.add(createModelItem(itemMap));
            }
        });
    }

    public static OutputExtentUMLToXMLModule newInstance(
            IDataModelModule dataModelModule, String extentFilePath,
            IIntermediateModelManagerModule intermediateModelManagerModule, IPersistentVector dataSourceAccess) {
        if (dataModelModule instanceof XMLDataModelModule) {
            if (intermediateModelManagerModule instanceof IntermediateUMLModelManagerModule) {
                return new OutputExtentUMLToXMLModule((XMLDataModelModule) dataModelModule, extentFilePath,
                        (IntermediateUMLModelManagerModule) intermediateModelManagerModule, dataSourceAccess);
            } else {
                throw new IllegalArgumentException("An instance of IntermediateUMLModelManagerModule should be passed");
            }
        } else {
            throw new IllegalArgumentException("An instance of XMLDataModelModule should be passed");
        }
    }

    private static UMLToXMLToken getNodeInPath(UMLToXMLToken parentNode, List<String> path) {
        return path.stream().reduce(
                parentNode,
                UMLToXMLToken::getChildByName,
                (e1, e2) -> {
                    throw new AssertionError();
                });
    }

    private static List<String> calculatePath(IPersistentVector... paths) {
        List<String> result = new ArrayList<>();
        for (IPersistentVector path : paths) {
            for (int i = 0; i < path.length(); i++) {
                Object pathElement = path.valAt(i);
                if (pathElement instanceof Keyword) {
                    String pathElementName = ((Keyword) pathElement).getName();
                    result.add(pathElementName);
                } else {
                    throw new AssertionError();
                }
            }
        }
        return result;
    }

    private ModelItem createModelItem(Object itemMap) {
        String tokenType = PARSER.keywordNameFromMap(itemMap, "type");
        switch (tokenType) {
            case "for-each":
                final ForEach forEachToken = new ForEach(PARSER.keywordNameFromMap(itemMap, "name"));
                PARSER.seqableToList(PARSER.seqableFromMap(itemMap, "body"))
                        .forEach(bodyItemMap -> forEachToken.addChild(createModelItem(bodyItemMap)));
                return forEachToken;
            case "associated-with":
                final AssociatedWith associatedWith = new AssociatedWith(
                        PARSER.vectorFromMap(itemMap, "with"),
                        PARSER.keywordNameFromMap(itemMap, "with-role"),
                        PARSER.keywordNameFromMap(itemMap, "association-name"),
                        PARSER.keywordNameFromMap(itemMap, "target-role"));
                PARSER.seqableToList(PARSER.seqableFromMap(itemMap, "body"))
                        .forEach(bodyItemMap -> associatedWith.addChild(createModelItem(bodyItemMap)));
                return associatedWith;
            case "associated-with-for":
                final AssociatedWithFor associatedWithFor = new AssociatedWithFor(
                        PARSER.vectorFromMap(itemMap, "with"),
                        PARSER.keywordNameFromMap(itemMap, "with-role"),
                        PARSER.keywordNameFromMap(itemMap, "association-name"),
                        PARSER.keywordNameFromMap(itemMap, "target-role"));
                PARSER.seqableToList(PARSER.seqableFromMap(itemMap, "body"))
                        .forEach(bodyItemMap -> associatedWithFor
                                .addChild(createModelItem(bodyItemMap)));
                return associatedWithFor;
            case "add-token":
                AddToken addToken = new AddToken(PARSER.vectorFromMap(itemMap, "path"));
                PARSER.seqableToList(PARSER.seqableFromMap(itemMap, "body"))
                        .forEach(bodyItemMap -> addToken.addChild(createModelItem(bodyItemMap)));
                return addToken;
            case "mapping":
                return new Mapping(PARSER.objectFromMap(itemMap, "from"), PARSER.objectFromMap(itemMap, "to"));
            case "mapping-each":
                return new MappingEach(PARSER.objectFromMap(itemMap, "from"), PARSER.objectFromMap(itemMap, "to"));
            case "bind":
                return new Bind(PARSER.objectFromMap(itemMap, "from"), PARSER.objectFromMap(itemMap, "to"));
            case "call":
                return new Call(PARSER.keywordNameFromMap(itemMap, "fn-name"), PARSER.vectorFromMap(itemMap, "args"));
            default:
                throw new AssertionError("Unrecognized token type");
        }
    }

    void executeRootModelItems(PrintWriter printWriter) {
        rootModelItems.forEach(rootModelItem -> rootModelItem.execute(new ArrayList<>(), rootNode, printWriter));
    }

    String getFilePath() {
        return filePath;
    }

    static final class UMLToXMLToken {
        private final Map<String, UMLToXMLToken> children;
        private final Map<String, UMLToXMLAttribute> attributes;
        private final String name;
        private final String tokenStringName;
        private final String textName;

        private UMLToXMLToken(XMLToken original, UMLToXMLToken parent) {
            children = original.getChildren().values().stream()
                    .map(dataToken -> new UMLToXMLToken(dataToken, UMLToXMLToken.this))
                    .collect(Collectors.toMap(t -> t.name, t -> t));
            attributes = original.getAttributes().values().stream().map(UMLToXMLAttribute::new)
                    .collect(Collectors.toMap(t -> t.name, t -> t));
            name = original.getName();
            tokenStringName = original.getTokenStringName();
            textName = original.getTextName();
        }

        public Collection<UMLToXMLToken> getChildrenValues() {
            return children.values();
        }

        public UMLToXMLToken getChildByName(String childName) {
            return children.get(childName);
        }

        public Collection<UMLToXMLAttribute> getAttributesValues() {
            return attributes.values();
        }

        public String getTokenStringName() {
            return tokenStringName;
        }

        public String getTextName() {
            return textName;
        }
    }

    static final class UMLToXMLAttribute {
        private final String name;
        private final String attributeName;

        public UMLToXMLAttribute(XMLAttribute original) {
            name = original.getName();
            attributeName = original.getAttributeName();
        }

        public String getAttributeName() {
            return attributeName;
        }
    }

    abstract class ModelItem {
        private final List<ModelItem> children = new ArrayList<>();
        private ModelItem parent = null;

        void execute(List<ModelItem> oldStack, UMLToXMLToken currentModelToken, PrintWriter printWriter) {
            oldStack.add(this);
            executeWithStackItemAdded(oldStack, currentModelToken, printWriter);
            oldStack.remove(this);
        }

        protected abstract void executeWithStackItemAdded(List<ModelItem> stack, UMLToXMLToken currentModelToken,
                                                          PrintWriter printWriter);

        protected void addChild(ModelItem item) {
            item.parent = this;
            children.add(item);
        }

        void forEachChild(Consumer<? super ModelItem> consumer) {
            children.forEach(consumer);
        }

        ModelItem getParent() {
            return parent;
        }
    }

    private final class ForEach extends ModelItem {
        private final String className;
        private UMLClassInstance currentClassInstance = null;

        private ForEach(String className) {
            this.className = className;
        }

        private String getClassName() {
            return className;
        }

        @Override protected void executeWithStackItemAdded(List<ModelItem> stack, UMLToXMLToken currentModelToken,
                                                           PrintWriter printWriter) {
            intermediateModelManagerModule.getClassInstances(className).forEach(classInstance -> {
                currentClassInstance = classInstance;
                forEachChild(child -> child.execute(stack, currentModelToken, printWriter));
            });
            currentClassInstance = null;
        }

        public UMLClassInstance getCurrentClassInstance() {
            return currentClassInstance;
        }
    }

    private abstract class Associated extends ModelItem {
        private final IPersistentVector with;
        private final String withRole;
        private final String associationName;
        private final String targetRole;

        private Associated(IPersistentVector with, String withRole, String associationName,
                           String targetRole) {
            this.with = with;
            this.withRole = withRole;
            this.associationName = associationName;
            this.targetRole = targetRole;
        }

        protected IPersistentVector getWith() {
            return with;
        }

        protected String getWithRole() {
            return withRole;
        }

        protected String getAssociationName() {
            return associationName;
        }

        protected String getTargetRole() {
            return targetRole;
        }
    }

    private final class AssociatedWith extends Associated {
        private AssociatedWith(IPersistentVector with, String withRole, String associationName, String targetRole) {
            super(with, withRole, associationName, targetRole);
        }

        @Override protected void executeWithStackItemAdded(List<ModelItem> stack, UMLToXMLToken currentModelToken,
                                                           PrintWriter printWriter) {
            for (String pathItem : calculatePath(getWith())) {
                currentModelToken = currentModelToken.getChildByName(pathItem);
            }

            //            if (intermediateModelManagerModule.) {
            //                forEachChild(child -> child.execute(stack, currentModelToken, printWriter));
            //            }
        }
    }

    private final class AssociatedWithFor extends Associated {
        private AssociatedWithFor(IPersistentVector with, String withRole, String associationName, String targetRole) {
            super(with, withRole, associationName, targetRole);
        }

        @Override protected void executeWithStackItemAdded(List<ModelItem> stack, UMLToXMLToken currentModelToken,
                                                           PrintWriter printWriter) {
            int indexOnStack = stack.size() - 1;

            int forEachIndex = PARSER.seqableToList(getWith()).size();
            if (forEachIndex < 2) {
                throw new AssertionError();
            }

            ForEach firstForEach = null;

            for (int i = 0; i < forEachIndex; i++) {
                do {
                    indexOnStack--;
                } while (!(stack.get(indexOnStack) instanceof ForEach));
                if (i == 0) {
                    firstForEach = (ForEach) stack.get(indexOnStack);
                }
            }

            ForEach forEach = (ForEach) stack.get(indexOnStack);

            if (intermediateModelManagerModule.areAssociated(forEach.getCurrentClassInstance(), getWithRole(),
                    getAssociationName(), getTargetRole(), firstForEach.getCurrentClassInstance())) {
                forEachChild(childItem -> childItem.execute(stack, currentModelToken, printWriter));
            }
        }
    }

    private final class AddToken extends ModelItem {
        private final IPersistentVector path;

        private AddToken(IPersistentVector path) {
            this.path = path;
        }

        private IPersistentVector getPath() {
            return path;
        }

        @Override protected void executeWithStackItemAdded(List<ModelItem> stack, UMLToXMLToken currentModelToken,
                                                           PrintWriter printWriter) {
            // TODO implement
        }
    }

    private abstract class AbstractMapping extends ModelItem {
        private final Object from;
        private final Object to;

        protected AbstractMapping(Object from, Object to) {
            this.from = from;
            this.to = to;
        }

        protected Object getFrom() {
            return from;
        }

        protected Object getTo() {
            return to;
        }
    }

    private final class Mapping extends AbstractMapping {
        protected Mapping(Object from, Object to) {
            super(from, to);
        }

        @Override protected void executeWithStackItemAdded(List<ModelItem> stack, UMLToXMLToken currentModelToken,
                                                           PrintWriter printWriter) {
            // TODO implement
        }
    }

    private final class MappingEach extends AbstractMapping {
        protected MappingEach(Object from, Object to) {
            super(from, to);
        }

        @Override protected void executeWithStackItemAdded(List<ModelItem> stack, UMLToXMLToken currentModelToken,
                                                           PrintWriter printWriter) {
            // TODO implement
        }
    }

    private final class Bind extends AbstractMapping {
        protected Bind(Object from, Object to) {
            super(from, to);
        }

        @Override protected void executeWithStackItemAdded(List<ModelItem> stack, UMLToXMLToken currentModelToken,
                                                           PrintWriter printWriter) {
            // TODO implement
        }
    }

    private final class Call extends ModelItem {
        private final String fnName;
        private final List<Object> args;

        protected Call(String fnName, IPersistentVector args) {
            this.fnName = fnName;
            this.args = PARSER.seqableToList(args);
        }

        @Override protected void executeWithStackItemAdded(List<ModelItem> stack, UMLToXMLToken currentModelToken,
                                                           PrintWriter printWriter) {
            // TODO implement
        }
    }
}
