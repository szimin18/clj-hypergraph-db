package unification.tool.module.model.xml;

import clojure.core.Vec;
import clojure.lang.IPersistentVector;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Seqable;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.model.IDataModelModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XMLDataModelModule implements IDataModelModule {
    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private static final Map<String, XMLDataModelModule> parsedModels = new HashMap<>();
    private final XMLToken rootNode;
    private final IPersistentVector accessVector;

    private XMLDataModelModule(String dataModelPath) {
        Seqable parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.model.xml.parser", dataModelPath);

        String defaultPath = null;

        List<Object> databaseMaps = PARSER.findAllItemsByType(parsedConfiguration, "database");
        if (!databaseMaps.isEmpty()) {
            List<Object> defaultPathItems = PARSER.findAllItemsFromMapValueByType(databaseMaps.get(0), "metadata",
                    "default-path");
            if (!defaultPathItems.isEmpty()) {
                defaultPath = PARSER.stringFromMap(defaultPathItems.get(0), "path");
            }
        }

        if (defaultPath != null) {
            accessVector = RT.vector(defaultPath);
        } else {
            accessVector = null;
        }

        rootNode = new XMLToken(PARSER.findAllItemsByType(parsedConfiguration, "token"));
    }

    public static XMLDataModelModule getInstance(String dataModelPath) {
        XMLDataModelModule parsedModel = parsedModels.get(dataModelPath);
        if (parsedModel == null) {
            parsedModel = new XMLDataModelModule(dataModelPath);
            parsedModels.put(dataModelPath, parsedModel);
        }
        return parsedModel;
    }

    public XMLToken getRootNode() {
        return rootNode;
    }

    @Override public IPersistentVector getAccessVector() {
        return accessVector;
    }

    public static final class XMLToken {
        private final Map<String, XMLToken> children;
        private final Map<String, XMLAttribute> attributes;
        private final String name;
        private final String tokenStringName;
        private final String textName;

        private XMLToken(List<Object> childrenMaps) {
            this(null, null, null, Collections.emptyList(), childrenMaps);
        }

        private XMLToken(String name, String tokenStringName, Object textMap, List<Object> attributesMaps,
                         List<Object> childrenMaps) {
            this.name = name;
            this.tokenStringName = tokenStringName;
            this.textName = textMap != null ? PARSER.keywordNameFromMap(textMap, "name") : null;

            attributes = Collections.unmodifiableMap(attributesMaps.stream().collect(Collectors
                    .toMap(attributeMap -> PARSER.keywordNameFromMap(attributeMap, "name"), XMLAttribute::new)));

            Map<String, XMLToken> newChildren = new HashMap<>();

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

                newChildren.put(childName, new XMLToken(
                        childName, childTokenName, childTextMap, childAttributesMaps, childChildrenMaps));
            });

            children = Collections.unmodifiableMap(newChildren);
        }

        public Map<String, XMLToken> getChildren() {
            return children;
        }

        public String getName() {
            return name;
        }

        public String getTokenStringName() {
            return tokenStringName;
        }

        public String getTextName() {
            return textName;
        }

        public Map<String, XMLAttribute> getAttributes() {
            return attributes;
        }
    }

    public static final class XMLAttribute {
        private final String name;
        private final String attributeName;

        private XMLAttribute(Object attributeMap) {
            name = PARSER.keywordNameFromMap(attributeMap, "name");
            attributeName = PARSER.stringFromMap(attributeMap, "attribute-name");
        }

        public String getName() {
            return name;
        }

        public String getAttributeName() {
            return attributeName;
        }
    }
}
