package unification.tool.module.extent.input.xml.uml;

import clojure.lang.IPersistentVector;
import clojure.lang.Seqable;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.extent.input.IInputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule.XMLAttribute;
import unification.tool.module.model.xml.XMLDataModelModule.XMLToken;

import java.util.Map;
import java.util.stream.Collectors;

public class InputExtentXMLToUMLModule implements IInputExtentModelModule {
    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final XMLToUMLToken rootNode;

    private InputExtentXMLToUMLModule(XMLDataModelModule dataModelModule, String extentFilePath,
                                      IntermediateUMLModelManagerModule intermediateModelManagerModule) {
        this.intermediateModelManagerModule = intermediateModelManagerModule;
        rootNode = new XMLToUMLToken(dataModelModule.getRootNode(), null);

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.input.xml.uml.parser", extentFilePath);

        PARSER.findAllItemsByType(parsedConfiguration, "extent").forEach(forEachMap -> {
            IPersistentVector path = PARSER.vectorFromMap(forEachMap, "path");
            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-instance").forEach(addInstanceMap -> {
                String className = PARSER.keywordNameFromMap(addInstanceMap, "name");
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping").forEach(mappingMap -> {
                    IPersistentVector mappingPath = PARSER.vectorFromMap(mappingMap, "path");
                    String attributeName = PARSER.keywordNameFromMap(mappingMap, "name");
                    boolean isPkMapping = PARSER.booleanFromMap(mappingMap, "pk-mapping");
                    if (!isPkMapping) {

                    }
                });
            });
        });

        PARSER.findAllItemsByType(parsedConfiguration, "extent").forEach(forEachMap -> {
            Seqable path = PARSER.seqableFromMap(forEachMap, "path");
            PARSER.findAllItemsFromMapValueByType(forEachMap, "body", "add-association").forEach(addAssociationMap -> {
                String associationName = PARSER.keywordNameFromMap(addAssociationMap, "name");
                PARSER.findAllItemsFromMapValueByType(addAssociationMap, "mappings", "mapping").forEach(mappingMap -> {
                    IPersistentVector mappingPath = PARSER.vectorFromMap(mappingMap, "path");
                    String attributeName = PARSER.keywordNameFromMap(mappingMap, "name");
                    boolean isPkMapping = PARSER.booleanFromMap(mappingMap, "pk-mapping");
                    if (isPkMapping) {

                    } else {

                    }
                });
            });
        });
    }

    public static InputExtentXMLToUMLModule newInstance(
            IDataModelModule dataModelModule, String extentFilePath,
            IIntermediateModelManagerModule intermediateModelManagerModule) {
        if (dataModelModule instanceof XMLDataModelModule) {
            if (intermediateModelManagerModule instanceof IntermediateUMLModelManagerModule) {
                return new InputExtentXMLToUMLModule((XMLDataModelModule) dataModelModule, extentFilePath,
                        (IntermediateUMLModelManagerModule) intermediateModelManagerModule);
            } else {
                throw new IllegalArgumentException("An instance of IntermediateUMLModelManagerModule should be passed");
            }
        } else {
            throw new IllegalArgumentException("An instance of XMLDataModelModule should be passed");
        }
    }

    public static final class XMLToUMLToken {
        private final Map<String, XMLToUMLToken> children;
        private final Map<String, XMLToUMLAttribute> attributes;
        private final String name;
        private final String tokenStringName;
        private final String textName;
        private final XMLToUMLToken parentToken;

        private XMLToUMLToken(XMLToken original, XMLToUMLToken parent) {
            children = original.getChildren().values().stream().map(token -> new XMLToUMLToken(token, this))
                    .collect(Collectors.toMap(t -> t.name, t -> t));
            attributes = original.getAttributes().values().stream().map(XMLToUMLAttribute::new)
                    .collect(Collectors.toMap(t -> t.name, t -> t));
            name = original.getName();
            tokenStringName = original.getTokenStringName();
            textName = original.getTextName();
            parentToken = parent;
        }
    }

    public static final class XMLToUMLAttribute {
        private final String name;
        private final String attributeName;

        public XMLToUMLAttribute(XMLAttribute original) {
            name = original.getName();
            attributeName = original.getAttributeName();
        }
    }
}
