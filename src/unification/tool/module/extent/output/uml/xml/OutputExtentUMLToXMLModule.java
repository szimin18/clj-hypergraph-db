package unification.tool.module.extent.output.uml.xml;

import clojure.lang.IPersistentVector;
import clojure.lang.Seqable;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.extent.output.IOutputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule.XMLAttribute;
import unification.tool.module.model.xml.XMLDataModelModule.XMLToken;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class OutputExtentUMLToXMLModule implements IOutputExtentModelModule {
    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final UMLToXMLToken rootNode;
    private final String filePath;

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

        rootNode = new UMLToXMLToken(dataModelModule.getRootNode());

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.output.uml.xml.parser", extentFilePath);

//        PARSER.findAllItemsByType(parsedConfiguration, "extent").forEach(forEachMap -> {
//            IPersistentVector path = PARSER.vectorFromMap(forEachMap, "path");
//            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-instance").forEach(addInstanceMap -> {
//                String className = PARSER.keywordNameFromMap(addInstanceMap, "name");
//                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping").forEach(mappingMap -> {
//                    IPersistentVector mappingPath = PARSER.vectorFromMap(mappingMap, "path");
//                    String attributeName = PARSER.keywordNameFromMap(mappingMap, "name");
//                    boolean isPkMapping = PARSER.booleanFromMap(mappingMap, "pk-mapping");
//                    if (!isPkMapping) {
//
//                    }
//                });
//            });
//        });
//
//        PARSER.findAllItemsByType(parsedConfiguration, "extent").forEach(forEachMap -> {
//            IPersistentVector path = PARSER.vectorFromMap(forEachMap, "path");
//            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-association").forEach(addAssociationMap -> {
//                String associationName = PARSER.keywordNameFromMap(addAssociationMap, "name");
//                PARSER.findAllItemsFromMapValueByType(addAssociationMap, "mappings", "mapping").forEach(mappingMap -> {
//                    IPersistentVector mappingPath = PARSER.vectorFromMap(mappingMap, "path");
//                    String attributeName = PARSER.keywordNameFromMap(mappingMap, "name");
//                    boolean isPkMapping = PARSER.booleanFromMap(mappingMap, "pk-mapping");
//                    if (isPkMapping) {
//
//                    } else {
//
//                    }
//                });
//            });
//        });
    }

    public IntermediateUMLModelManagerModule getIntermediateModelManagerModule() {
        return intermediateModelManagerModule;
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

    public UMLToXMLToken getRootNode() {
        return rootNode;
    }

    public String getFilePath() {
        return filePath;
    }

    static final class UMLToXMLToken {
        private final Map<String, UMLToXMLToken> children;
        private final Map<String, UMLToXMLAttribute> attributes;
        private final String name;
        private final String tokenStringName;
        private final String textName;

        private UMLToXMLToken(XMLToken original) {
            children = original.getChildren().values().stream().map(UMLToXMLToken::new)
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
}
