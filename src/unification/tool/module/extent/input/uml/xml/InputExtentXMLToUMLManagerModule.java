package unification.tool.module.extent.input.uml.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import unification.tool.module.extent.input.IInputExtentModelManagerModule;
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

    private StringBuilder textBuilder;
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
            textBuilder = new StringBuilder();
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
    }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException {
        finalizeText();

        currentNode = currentNode.getParentToken();
    }

    @Override public void characters(char[] ch, int start, int length) throws SAXException {
    }

    private void finalizeText() {
        String textValue = textBuilder.toString();
        textBuilder = new StringBuilder();


    }

    private static final class XMLToUMLManagerToken {
        private final Map<String, XMLToUMLManagerToken> children;
        private final Map<String, XMLToUMLManagerAttribute> attributes;
        private final String textName;
        private final XMLToUMLManagerToken parentToken;

        private XMLToUMLManagerToken(InputExtentXMLToUMLModule.XMLToUMLToken original, XMLToUMLManagerToken parent) {
            children = original.getChildrenValues().stream().collect(Collectors.toMap(
                    InputExtentXMLToUMLModule.XMLToUMLToken::getTokenStringName,
                    t -> new XMLToUMLManagerToken(t, this)));
            attributes = original.getAttributesValues().stream().collect(Collectors.toMap(
                    InputExtentXMLToUMLModule.XMLToUMLAttribute::getAttributeName, XMLToUMLManagerAttribute::new));
            textName = original.getTextName();
            parentToken = parent;
        }

        private XMLToUMLManagerToken getChildAt(String name) {
            return children.get(name);
        }

        public XMLToUMLManagerToken getParentToken() {
            return parentToken;
        }
    }

    private static final class XMLToUMLManagerAttribute {
        public XMLToUMLManagerAttribute(InputExtentXMLToUMLModule.XMLToUMLAttribute original) {
        }
    }
}
