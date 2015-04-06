package unification.tool.module.model.xml;

import clojure.lang.IPersistentVector;
import clojure.lang.Seqable;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.model.IDataModelModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLDataModelModule implements IDataModelModule {
    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private static final Map<String, XMLDataModelModule> parsedModels = new HashMap<>();
    private final XMLToken rootNode;
    private String dataPath = null;

    private XMLDataModelModule(String dataModelPath) {
        Seqable parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.xml.model.parser", dataModelPath);

        List<Object> defaultPathItems = PARSER.findAllItemsByType(parsedConfiguration, "default-path");

        if (!defaultPathItems.isEmpty()) {
            dataPath = PARSER.stringFromMap(defaultPathItems.get(0), "path");
        }

        rootNode = new XMLToken(PARSER.findAllItemsByType(parsedConfiguration, "token"));
    }

    private XMLDataModelModule(XMLDataModelModule templateModule) {
        dataPath = templateModule.dataPath;
        rootNode = templateModule.rootNode;
    }

    public static XMLDataModelModule getInstance(String dataModelPath) {
        XMLDataModelModule parsedModel = parsedModels.get(dataModelPath);
        if (parsedModel == null) {
            parsedModel = new XMLDataModelModule(dataModelPath);
            parsedModels.put(dataModelPath, parsedModel);
        } else {
            parsedModel = new XMLDataModelModule(parsedModel);
        }

        return parsedModel;
    }

    @Override public void setAccessVector(IPersistentVector accessVector) {
        if (accessVector.length() != 0) {
            throw new IllegalArgumentException("Illegal length of access vector");
        }

        dataPath = accessVector.valAt(0).toString();
    }

    public static class XMLToken {
        private final Map<String, XMLToken> children = new HashMap<>();
        private final String name;
        private final String tokenStringName;
        private final String textName;
        private final XMLToken parentToken;
        private final Map<String, XMLAttribute> attributes = new HashMap<>();

        private XMLToken(List<Object> childrenMaps) {
            this(null, null, null, null, Collections.emptyList(), childrenMaps);
        }

        private XMLToken(String name, String tokenStringName, Object textMap, XMLToken parentToken,
                         List<Object> attributesMaps, List<Object> childrenMaps) {
            this.name = name;
            this.tokenStringName = tokenStringName;
            this.textName = textMap != null ? PARSER.keywordNameFromMap(textMap, "name") : null;
            this.parentToken = parentToken;

            attributesMaps.forEach(attributeMap ->
                    attributes.put(PARSER.keywordNameFromMap(attributeMap, "name"), new XMLAttribute(attributeMap)));

            childrenMaps.forEach(childMap -> {
                String childName = PARSER.keywordNameFromMap(childMap, "name");
                String childTokenName = PARSER.stringFromMap(childMap, "token-name");

                List<Object> childChildrenMaps =
                        PARSER.findAllItemsFromMapValueByType(childMap, "other", "token");

                List<Object> textMaps =
                        PARSER.findAllItemsFromMapValueByType(childMap, "other", "text");
                Object childTextMap = textMaps.isEmpty() ? null : textMaps.get(0);

                List<Object> childAttributesMaps =
                        PARSER.findAllItemsFromMapValueByType(childMap, "other", "attribute");

                children.put(childName, new XMLToken(
                        childName, childTokenName, childTextMap, this, childAttributesMaps, childChildrenMaps));
            });
        }
    }

    public static class XMLAttribute {
        private final String name;
        private final String attributeName;

        private XMLAttribute(Object attributeMap) {
            name = PARSER.keywordNameFromMap(attributeMap, "name");
            attributeName = PARSER.stringFromMap(attributeMap, "attribute-name");
        }
    }
}
