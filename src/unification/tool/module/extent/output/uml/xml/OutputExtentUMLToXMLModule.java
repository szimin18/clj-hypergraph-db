package unification.tool.module.extent.output.uml.xml;

import clojure.lang.*;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.extent.output.IOutputExtentModelModule;
import unification.tool.module.extent.output.uml.xml.OutputExtentUMLToXMLManagerModule.IOutputPrinter;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule.UMLClassInstance;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule.XMLAttribute;
import unification.tool.module.model.xml.XMLDataModelModule.XMLToken;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OutputExtentUMLToXMLModule implements IOutputExtentModelModule {
    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final UMLToXMLToken rootToken;
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

        rootToken = new UMLToXMLToken(dataModelModule.getRootNode(), null);

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.output.uml.xml.parser", extentFilePath);

        functionsMap = new HashMap<>();
        rootModelItems = new ArrayList<>();

        Set<Object> functionMaps = PARSER.findAllItemsByType(parsedConfiguration, "function").stream().collect(Collectors.toSet());
        functionMaps.forEach(functionMap -> {
            String functionName = PARSER.keywordNameFromMap(functionMap, "name");
            IFn functionBody = PARSER.iFnFromMap(functionMap, "body");
            functionsMap.put(functionName, functionBody);
        });
        PARSER.seqableToList(parsedConfiguration).forEach(itemMap -> {
            if (!functionMaps.contains(itemMap)) {
                rootModelItems.add(createModelItem(itemMap, rootToken));
            }
        });
        rootModelItems.forEach(OutputExtentUMLToXMLModule::linkInHierarchyRecursively);
    }

    public static OutputExtentUMLToXMLModule newInstance(IDataModelModule dataModelModule, String extentFilePath,
                                                         IIntermediateModelManagerModule intermediateModelManagerModule,
                                                         IPersistentVector dataSourceAccess) {
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

    private ModelItem createModelItem(Object itemMap, UMLToXMLToken currentToken) {
        String tokenType = PARSER.keywordNameFromMap(itemMap, "type");
        switch (tokenType) {
            case "for-each":
                final ForEach forEachToken = new ForEach(PARSER.keywordNameFromMap(itemMap, "name"));
                PARSER.seqableToList(PARSER.seqableFromMap(itemMap, "body"))
                        .forEach(bodyItemMap -> forEachToken.addChild(createModelItem(bodyItemMap, currentToken)));
                return forEachToken;
            //            case "associated-with":
            //                final AssociatedWith associatedWith = new AssociatedWith(
            //                        PARSER.vectorFromMap(itemMap, "with"),
            //                        PARSER.keywordNameFromMap(itemMap, "with-role"),
            //                        PARSER.keywordNameFromMap(itemMap, "association-name"),
            //                        PARSER.keywordNameFromMap(itemMap, "target-role"));
            //                PARSER.seqableToList(PARSER.seqableFromMap(itemMap, "body"))
            //                        .forEach(bodyItemMap -> associatedWith.addChild(createModelItem(bodyItemMap, currentToken)));
            //                return associatedWith;
            case "associated-with-for":
                final AssociatedWithFor associatedWithFor = new AssociatedWithFor(
                        PARSER.vectorFromMap(itemMap, "with"),
                        PARSER.keywordNameFromMap(itemMap, "with-role"),
                        PARSER.keywordNameFromMap(itemMap, "association-name"),
                        PARSER.keywordNameFromMap(itemMap, "target-role"));
                PARSER.seqableToList(PARSER.seqableFromMap(itemMap, "body"))
                        .forEach(bodyItemMap -> associatedWithFor
                                .addChild(createModelItem(bodyItemMap, currentToken)));
                return associatedWithFor;
            case "add-token":
                List<String> path = PARSER.seqableToKeywordNamesList(PARSER.vectorFromMap(itemMap, "path"));
                UMLToXMLToken newCurrentToken = getRelativeNodeInHierarchy(currentToken, path);
                AddToken addToken = new AddToken(path.remove(path.size() - 1));
                PARSER.seqableToList(PARSER.seqableFromMap(itemMap, "body"))
                        .forEach(bodyItemMap -> addToken.addChild(createModelItem(bodyItemMap, newCurrentToken)));
                return createTokenWriters(path, addToken);
            case "mapping":
            case "mapping-each":
                Object mappingSource = PARSER.objectFromMap(itemMap, "from");
                List<String> mappingPath = PARSER.seqableToKeywordNamesList(PARSER.seqableFromMap(itemMap, "to"));
                switch (mappingPath.size()) {
                    case 0:
                        throw new AssertionError();
                    case 1:
                        return tokenType.equals("mapping") ? new Mapping(mappingSource, mappingPath.get(0)) : new MappingEach(mappingSource,
                                mappingPath.get(0));
                    default:
                        String lastOfMappingPath = mappingPath.remove(mappingPath.size() - 1);
                        boolean isTextMapping =
                                getRelativeNodeInHierarchy(currentToken, mappingPath).getAttributeByName(lastOfMappingPath) == null;
                        if (isTextMapping) {
                            String butLastOfMappingPath = mappingPath.remove(mappingPath.size() - 1);
                            if (tokenType.equals("mapping")) {
                                return createTokenWriters(mappingPath, new TextMapping(mappingSource, butLastOfMappingPath));
                            } else {
                                return createTokenWriters(mappingPath, new TextMappingEach(mappingSource, butLastOfMappingPath));
                            }
                        } else {
                            if (tokenType.equals("mapping")) {
                                return createTokenWriters(mappingPath, new Mapping(mappingSource, lastOfMappingPath));
                            } else {
                                return createTokenWriters(mappingPath, new MappingEach(mappingSource, lastOfMappingPath));
                            }
                        }
                }
            case "bind":
                return new Bind(PARSER.objectFromMap(itemMap, "from"), PARSER.keywordNameFromMap(itemMap, "to"));
            default:
                throw new AssertionError("Unrecognized token type");
        }
    }

    void executeRootModelItems(IOutputPrinter outputPrinter) {
        VariablesStore variablesStore = new VariablesStore();
        rootModelItems.forEach(rootModelItem -> rootModelItem.execute(outputPrinter, variablesStore));
    }

    private static final class VariablesStore {
        private final Map<String, Object> variablesMap = new HashMap<>();

        private void setVariableValue(String variableName, Object variableValue) {
            variablesMap.put(variableName, variableValue);
        }

        private Object getVariableValue(String variableName) {
            return variablesMap.get(variableName);
        }
    }

    String getFilePath() {
        return filePath;
    }

    UMLToXMLToken getRootToken() {
        return rootToken;
    }

    private static void linkInHierarchyRecursively(ModelItem modelItem) {
        modelItem.linkInHierarchy();
        modelItem.forEachChild(OutputExtentUMLToXMLModule::linkInHierarchyRecursively);
    }

    private static void notifyParentItemsDuringCollapsing(ModelItem modelItem, IOutputPrinter outputPrinter) {
        ModelItem parentItem = modelItem.getParent();
        if (parentItem != null) {
            notifyParentItemsDuringCollapsing(parentItem, outputPrinter);
            parentItem.notifyChildExecuted(outputPrinter);
        }
    }

    private UMLToXMLToken getRelativeNodeInHierarchy(UMLToXMLToken token, List<String> keys) {
        //        List<String> keysCopy = new ArrayList<>(keys);
        //        if (keysCopy.isEmpty()) {
        //            return token;
        //        } else {
        //            String firstKey = keysCopy.remove(0);
        //            return getRelativeNodeInHierarchy(token.getChildByName(firstKey), keysCopy);
        //        }
        return keys.stream().reduce(token, UMLToXMLToken::getChildByName, (e1, e2) -> e1);
    }

    private ModelItem createTokenWriters(List<String> tokenNames, ModelItem childForLastTokenWriter) {
        if (tokenNames.isEmpty()) {
            return childForLastTokenWriter;
        } else {
            TokenWriter tokenWriter = new TokenWriter(tokenNames.get(tokenNames.size() - 1));
            tokenWriter.addChild(childForLastTokenWriter);
            return tokenWriter;
        }
    }

    final class UMLToXMLToken {
        private final Map<String, UMLToXMLToken> children;
        private final Map<String, UMLToXMLAttribute> attributes;
        private final List<AddToken> addTokenItems = new ArrayList<>();
        private final String name;
        private final String tokenStringName;
        private final String textName;
        private final UMLToXMLToken parent;

        private UMLToXMLToken(XMLToken original, UMLToXMLToken parent) {
            this.parent = parent;
            children = original.getChildren().values().stream()
                    .map(dataToken -> new UMLToXMLToken(dataToken, UMLToXMLToken.this))
                    .collect(Collectors.toMap(t -> t.name, t -> t));
            attributes = original.getAttributes().values().stream().map(UMLToXMLAttribute::new)
                    .collect(Collectors.toMap(t -> t.name, t -> t));
            name = original.getName();
            tokenStringName = original.getTokenStringName();
            textName = original.getTextName();
        }

        public UMLToXMLToken getParent() {
            return parent;
        }

        public UMLToXMLToken getChildByName(String childName) {
            return children.get(childName);
        }

        public UMLToXMLAttribute getAttributeByName(String attributeName) {
            return attributes.get(attributeName);
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

    interface IClassInstanceProvider {
        UMLClassInstance getClassInstance();

        String getClassName();
    }

    private abstract class ModelItem {
        private final List<ModelItem> children = new ArrayList<>();
        private ModelItem parent = null;

        protected abstract void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore);

        protected void linkInHierarchy() {
        }

        protected final void addChild(ModelItem item) {
            item.parent = this;
            children.add(item);
        }

        protected void notifyChildExecuted(IOutputPrinter outputPrinter) {
        }

        protected void forEachChild(Consumer<? super ModelItem> consumer) {
            children.forEach(consumer);
        }

        protected void executeEachChild(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            children.forEach(childItem -> childItem.execute(outputPrinter, variablesStore));
        }

        final ModelItem getParent() {
            return parent;
        }
    }

    private final class TokenWriter extends ModelItem implements IInstancesEndedListener {
        private String tokenName;
        private boolean executed = false;

        private TokenWriter(String tokenName) {
            this.tokenName = tokenName;
        }

        @Override protected void linkInHierarchy() {
            for (ModelItem item = getParent(); item != null; item = item.getParent()) {
                if (item instanceof ForEach) {
                    ((ForEach) item).addInstancesEndedListener(this);
                }
            }
        }

        @Override protected void notifyChildExecuted(IOutputPrinter outputPrinter) {
            if (!executed) {
                outputPrinter.startToken(tokenName);
                executed = true;
            }
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            executeEachChild(outputPrinter, variablesStore);
        }

        @Override public void instancesEnded(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            if (executed) {
                outputPrinter.endToken();
                executed = false;
            }
        }
    }

    private final class ForEach extends ModelItem implements IClassInstanceProvider {
        private final String className;
        private UMLClassInstance currentClassInstance = null;
        private final List<IInstancesEndedListener> instancesEndedListeners = new ArrayList<>();

        private ForEach(String className) {
            this.className = className;
        }

        @Override public String getClassName() {
            return className;
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            intermediateModelManagerModule.getClassInstances(className).forEach(classInstance -> {
                System.out.printf("Class occurrence: %s\n", className);
                currentClassInstance = classInstance;
                executeEachChild(outputPrinter, variablesStore);
            });
            instancesEndedListeners.forEach(listener -> listener.instancesEnded(outputPrinter, variablesStore));
        }

        @Override protected void linkInHierarchy() {
        }

        @Override public UMLClassInstance getClassInstance() {
            return currentClassInstance;
        }

        public void addInstancesEndedListener(IInstancesEndedListener listener) {
            instancesEndedListeners.add(0, listener);
        }
    }

    interface IInstancesEndedListener {
        void instancesEnded(IOutputPrinter outputPrinter, VariablesStore variablesStore);
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

    //    private final class AssociatedWith extends Associated implements IClassInstanceProvider {
    //        private IClassInstanceProvider associatedInstanceProvider = null;
    //
    //        private IClassInstanceProvider instanceProvider = null;
    //
    //        private AssociatedWith(IPersistentVector with, String withRole, String associationName, String targetRole) {
    //            super(with, withRole, associationName, targetRole);
    //        }
    //
    //        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
    //            if (intermediateModelManagerModule.areAssociated(associatedInstanceProvider.getClassInstance(), getWithRole(),
    //                    getAssociationName(), getTargetRole(), instanceProvider.getClassInstance())) {
    //                forEachChild(child -> child.execute(outputPrinter, variablesStore));
    //            }
    //        }
    //
    //        @Override protected void linkInHierarchy() {
    //            UMLToXMLToken node = getRelativeNodeInHierarchy(this);
    //            associatedInstanceProvider = node.findAddTokenItem(intermediateModelManagerModule.getModelModule().getAssociationByName(
    //                    getAssociationName()).getRoleByName(getWithRole()).getTargetClass());
    //
    //            for (ModelItem item = getParent(); item != null; item = item.getParent()) {
    //                if (instanceProvider == null && item instanceof IClassInstanceProvider) {
    //                    instanceProvider = (IClassInstanceProvider) item;
    //                }
    //            }
    //        }
    //
    //        @Override public UMLClassInstance getClassInstance() {
    //            return instanceProvider.getClassInstance();
    //        }
    //
    //        @Override public String getClassName() {
    //            return instanceProvider.getClassName();
    //        }
    //    }

    private final class AssociatedWithFor extends Associated implements IClassInstanceProvider {
        private IClassInstanceProvider associatedInstanceProvider = null;

        private IClassInstanceProvider instanceProvider = null;

        private AssociatedWithFor(IPersistentVector with, String withRole, String associationName, String targetRole) {
            super(with, withRole, associationName, targetRole);
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            if (intermediateModelManagerModule.areAssociated(associatedInstanceProvider.getClassInstance(), getWithRole(),
                    getAssociationName(), getTargetRole(), instanceProvider.getClassInstance())) {
                executeEachChild(outputPrinter, variablesStore);
            }
        }

        @Override protected void linkInHierarchy() {
            int associatedForEachIndex = PARSER.seqableToList(getWith()).size();
            if (associatedForEachIndex == 0) {
                throw new AssertionError();
            }

            for (ModelItem item = getParent(); item != null; item = item.getParent()) {
                if (instanceProvider == null && item instanceof IClassInstanceProvider) {
                    instanceProvider = (IClassInstanceProvider) item;
                }
                if (item instanceof ForEach) {
                    if (associatedForEachIndex == 1) {
                        associatedInstanceProvider = (ForEach) item;
                        break;
                    } else {
                        associatedForEachIndex--;
                    }
                }
            }

            if (associatedInstanceProvider == null) {
                throw new AssertionError();
            }
        }

        @Override public UMLClassInstance getClassInstance() {
            return instanceProvider.getClassInstance();
        }

        @Override public String getClassName() {
            return instanceProvider.getClassName();
        }
    }

    private final class AddToken extends ModelItem implements IClassInstanceProvider {
        private IClassInstanceProvider instanceProvider = null;

        private final String tokenName;

        private AddToken(String tokenName) {
            this.tokenName = tokenName;
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            notifyParentItemsDuringCollapsing(this, outputPrinter);
            outputPrinter.startToken(tokenName);
            executeEachChild(outputPrinter, variablesStore);
            outputPrinter.endToken();
        }

        @Override protected void linkInHierarchy() {
            for (ModelItem item = getParent(); item != null; item = item.getParent()) {
                if (instanceProvider == null && item instanceof IClassInstanceProvider) {
                    instanceProvider = (IClassInstanceProvider) item;
                }
            }
        }

        @Override public UMLClassInstance getClassInstance() {
            return instanceProvider.getClassInstance();
        }

        @Override public String getClassName() {
            return instanceProvider.getClassName();
        }
    }

    private abstract class AbstractMapping extends ModelItem {
        private final ValueProvider valueProvider;
        private final String relatedAttributeName;

        protected AbstractMapping(Object source, String relatedAttributeName) {
            valueProvider = getValueProvider(source);
            addChild(valueProvider);
            this.relatedAttributeName = relatedAttributeName;
        }

        protected List<Object> getSourceValues() {
            return valueProvider.getValues();
        }

        protected void addAttributeValue(IOutputPrinter outputPrinter, String attributeValue) {
            notifyParentItemsDuringCollapsing(this, outputPrinter);
            outputPrinter.addAttribute(relatedAttributeName, attributeValue);
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            executeEachChild(outputPrinter, variablesStore);
            addValues(outputPrinter, variablesStore);
        }

        protected abstract void addValues(IOutputPrinter outputPrinter, VariablesStore variablesStore);
    }

    private final class Mapping extends AbstractMapping {
        protected Mapping(Object source, String mappingTarget) {
            super(source, mappingTarget);
        }

        @Override protected void addValues(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            getSourceValues().forEach(attributeValue -> addAttributeValue(outputPrinter, attributeValue.toString()));
        }
    }

    private final class MappingEach extends AbstractMapping {
        protected MappingEach(Object source, String mappingTarget) {
            super(source, mappingTarget);
        }

        @Override protected void addValues(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            getSourceValues().forEach(attributeValuesSequable -> PARSER.seqableToList((Seqable) attributeValuesSequable).forEach(
                    attributeValue -> addAttributeValue(outputPrinter, attributeValue.toString())));
        }
    }

    private abstract class AbstractTextMapping extends ModelItem {
        private final ValueProvider valueProvider;
        private final String tokenName;

        protected AbstractTextMapping(Object source, String tokenName) {
            valueProvider = getValueProvider(source);
            addChild(valueProvider);
            this.tokenName = tokenName;
        }

        protected void addTextValue(IOutputPrinter outputPrinter, String attributeValue) {
            notifyParentItemsDuringCollapsing(this, outputPrinter);
            outputPrinter.addTextInToken(tokenName, attributeValue);
        }

        protected List<Object> getSourceValues() {
            return valueProvider.getValues();
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            executeEachChild(outputPrinter, variablesStore);
            addValues(outputPrinter, variablesStore);
        }

        protected abstract void addValues(IOutputPrinter outputPrinter, VariablesStore variablesStore);
    }

    private final class TextMapping extends AbstractTextMapping {
        protected TextMapping(Object source, String tokenName) {
            super(source, tokenName);
        }

        @Override protected void addValues(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            getSourceValues().forEach(attributeValue -> addTextValue(outputPrinter, attributeValue.toString()));
        }
    }

    private final class TextMappingEach extends AbstractTextMapping {
        protected TextMappingEach(Object source, String tokenName) {
            super(source, tokenName);
        }

        @Override protected void addValues(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            getSourceValues().forEach(attributeValuesSequable -> PARSER.seqableToList((Seqable) attributeValuesSequable).forEach(
                    attributeValue -> addTextValue(outputPrinter, attributeValue.toString())));
        }
    }

    private abstract class ValueProvider extends ModelItem {
        protected abstract List<Object> getValues();
    }

    ValueProvider getValueProvider(Object objectToDispatch) {
        if (objectToDispatch instanceof IPersistentMap) {
            switch (PARSER.keywordNameFromMap(objectToDispatch, "type")) {
                case "call":
                    return new Call(PARSER.keywordNameFromMap(objectToDispatch, "fn-name"),
                            PARSER.seqableToList(PARSER.seqableFromMap(objectToDispatch, "args")));
                case "aggregate":
                    return new Aggregate(PARSER.keywordNameFromMap(objectToDispatch, "arg"));
                default:
                    throw new AssertionError();
            }
        } else if (objectToDispatch instanceof Keyword) {
            return new ClassAttributeOrVariableValueProvider(((Keyword) objectToDispatch).getName());
        } else {
            return new ConstantProvider(objectToDispatch);
        }
    }

    private class ConstantProvider extends ValueProvider {
        private final List<Object> constant;

        private ConstantProvider(Object constant) {
            this.constant = Collections.singletonList(constant);
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
        }

        @Override public List<Object> getValues() {
            return constant;
        }
    }

    private class ClassAttributeOrVariableValueProvider extends ValueProvider {
        private List<Object> value = null;
        private IClassInstanceProvider instanceProvider = null;
        private final String attributeName;

        private ClassAttributeOrVariableValueProvider(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override protected void linkInHierarchy() {
            for (ModelItem currentItem = getParent(); currentItem != null; currentItem = currentItem.getParent()) {
                if (currentItem instanceof IClassInstanceProvider) {
                    instanceProvider = (IClassInstanceProvider) currentItem;
                    break;
                }
            }

            if (instanceProvider != null && intermediateModelManagerModule.getModelModule().getClassByName(instanceProvider.getClassName())
                    .getAttributeByName(attributeName) == null) {
                instanceProvider = null;
            }
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            if (instanceProvider != null) {
                value = instanceProvider.getClassInstance().getAttributeValues(attributeName, Object.class);
            } else {
                value = Collections.singletonList(variablesStore.getVariableValue(attributeName));
            }
        }

        @Override public List<Object> getValues() {
            return value;
        }
    }

    private final class Aggregate extends ValueProvider {
        List<Object> value = null;
        private final String attributeName;
        private IClassInstanceProvider instanceProvider = null;

        private Aggregate(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override protected void linkInHierarchy() {
            for (ModelItem currentItem = getParent(); currentItem != null; currentItem = currentItem.getParent()) {
                if (currentItem instanceof IClassInstanceProvider) {
                    instanceProvider = (IClassInstanceProvider) currentItem;
                    return;
                }
            }

            throw new AssertionError();
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            value = Collections.singletonList(PersistentVector.create(
                    instanceProvider.getClassInstance().getAttributeValues(attributeName, Object.class)));
        }

        @Override public List<Object> getValues() {
            return value;
        }
    }

    private final class Bind extends ModelItem {
        private final ValueProvider valueProvider;
        private final String variableName;

        protected Bind(Object source, String variableName) {
            valueProvider = getValueProvider(source);
            addChild(valueProvider);
            this.variableName = variableName;
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            valueProvider.execute(outputPrinter, variablesStore);
            variablesStore.setVariableValue(variableName, valueProvider.getValues().get(0));
        }
    }

    private final class Call extends ValueProvider {
        private final IFn function;
        private final List<ValueProvider> arguments = new ArrayList<>();
        private List<Object> value = null;

        protected Call(String functionName, List<Object> arguments) {
            function = functionsMap.get(functionName);
            arguments.stream().map(OutputExtentUMLToXMLModule.this::getValueProvider).forEach(this.arguments::add);
            this.arguments.forEach(this::addChild);
        }

        @Override protected void execute(IOutputPrinter outputPrinter, VariablesStore variablesStore) {
            executeEachChild(outputPrinter, variablesStore);
            value = Collections.singletonList(function.applyTo(PersistentVector.create(arguments.stream().map(ValueProvider::getValues)
                    .map(Iterable::iterator).map(Iterator::next).collect(Collectors.toList())).seq()));
        }

        @Override public List<Object> getValues() {
            return value;
        }
    }
}
