package unification.tool.module.extent.input.uml.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import unification.tool.module.extent.input.IInputExtentModelManagerModule;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule.XMLToUMLAttribute;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule.XMLToUMLClassInstanceManager;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule.XMLToUMLToken;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class InputExtentXMLToUMLManagerModule extends DefaultHandler implements IInputExtentModelManagerModule {
    private final String systemIDOfFile;
    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final XMLToUMLManagerToken rootNode;
    private final StringBuilder textBuilder = new StringBuilder();

    private XMLToUMLManagerToken currentNode;

    private InputExtentXMLToUMLManagerModule(InputExtentXMLToUMLModule modelModule) {
        String filePath = new File(modelModule.getFilePath()).getAbsolutePath().replace(File.separatorChar, '/');
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }
        systemIDOfFile = "file:" + filePath;

        intermediateModelManagerModule = modelModule.getIntermediateModelManagerModule();
        rootNode = new XMLToUMLManagerToken(modelModule.getRootNode(), null);
    }

    public static InputExtentXMLToUMLManagerModule newInstance(InputExtentXMLToUMLModule modelModule) {
        return new InputExtentXMLToUMLManagerModule(modelModule);
    }

    @Override public void readInput() {
        try {
            currentNode = rootNode;
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.parse(systemIDOfFile);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException("SAX exception");
        }
    }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        finalizeText();
        currentNode = currentNode.getChildAt(qName);
        currentNode.handleTokenStart();
        for (int i = 0; i < attributes.getLength(); i++) {
            currentNode.handleAttributeOccurrence(attributes.getQName(i), attributes.getValue(i));
        }
    }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException {
        finalizeText();
        currentNode.handleTokenEnd();
        currentNode = currentNode.getParentToken();
    }

    @Override public void characters(char[] ch, int start, int length) throws SAXException {
    }

    private void finalizeText() {
        String textValue = textBuilder.toString();
        if (textBuilder.length() > 0) {
            textBuilder.delete(0, textBuilder.length() - 1);
        }
        currentNode.handleTextOccurrence(textValue);

    }

    private static final class XMLToUMLManagerToken {
        private final XMLToUMLToken originalToken;
        private final Map<String, XMLToUMLManagerToken> children;
        private final Map<String, XMLToUMLAttribute> attributes;
        private final XMLToUMLManagerToken parentToken;

        private XMLToUMLManagerToken(XMLToUMLToken originalToken,
                                     XMLToUMLManagerToken parent) {
            this.originalToken = originalToken;
            children = originalToken.getChildrenValues().stream().collect(Collectors
                    .toMap(XMLToUMLToken::getTokenStringName, token -> new XMLToUMLManagerToken(token, this)));
            attributes = originalToken.getAttributesValues().stream().collect(Collectors.toMap(
                    XMLToUMLAttribute::getAttributeName, attribute -> attribute));
            parentToken = parent;
        }

        XMLToUMLManagerToken getChildAt(String name) {
            return children.get(name);
        }

        XMLToUMLManagerToken getParentToken() {
            return parentToken;
        }

        void handleTokenStart() {
            originalToken.getAddClassInstanceList().forEach(XMLToUMLClassInstanceManager::newInstance);
        }

        void handleTokenEnd() {

        }

        void handleAttributeOccurrence(String xmlAttributeName, String attributeValue) {
            attributes.get(xmlAttributeName).getAddAttributeInstanceList().forEach(
                    (classInstanceManager, umlAttributeNames) -> umlAttributeNames.forEach(umlAttributeName ->
                            classInstanceManager.addAttributeInstance(umlAttributeName, attributeValue)));
        }

        void handleTextOccurrence(String attributeValue) {
            originalToken.getAddAttributeInstanceFromTextList().forEach(
                    (classInstanceManager, umlAttributeNames) -> umlAttributeNames.forEach(umlAttributeName ->
                            classInstanceManager.addAttributeInstance(umlAttributeName, attributeValue)));
        }
    }
}
