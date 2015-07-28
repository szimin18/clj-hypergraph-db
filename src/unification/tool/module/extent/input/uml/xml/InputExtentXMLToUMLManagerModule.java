package unification.tool.module.extent.input.uml.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import unification.tool.module.extent.input.IInputExtentModelManagerModule;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule.XMLToUMLAssociationInstanceManager;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule.XMLToUMLAttribute;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule.XMLToUMLClassInstanceManager;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule.XMLToUMLToken;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule.UMLAttribute;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InputExtentXMLToUMLManagerModule extends DefaultHandler implements IInputExtentModelManagerModule {
    private final String systemIDOfFile;
    private final IntermediateUMLModelModule intermediateModelModule;
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

        intermediateModelModule = modelModule.getIntermediateModelModule();
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
        textBuilder.append(ch, start, length);
    }

    private void finalizeText() {
        String textValue = textBuilder.toString();
        if (textBuilder.length() > 0) {
            textBuilder.delete(0, textBuilder.length() - 1);
        }
        String trimmedValue = textValue.trim();
        if (!trimmedValue.isEmpty()) {
            currentNode.handleTextOccurrence(trimmedValue);
        }
    }

    private final class XMLToUMLManagerToken {
        private final XMLToUMLToken originalToken;
        private final Map<String, XMLToUMLManagerToken> children;
        private final Map<String, XMLToUMLAttribute> attributes;
        private final XMLToUMLManagerToken parentToken;

        private XMLToUMLManagerToken(XMLToUMLToken originalToken, XMLToUMLManagerToken parent) {
            this.originalToken = originalToken;
            parentToken = parent;
            children = originalToken.getChildrenValues().stream().collect(Collectors
                    .toMap(XMLToUMLToken::getTokenStringName, token -> new XMLToUMLManagerToken(token, this)));
            attributes = originalToken.getAttributesValues().stream().collect(Collectors.toMap(
                    XMLToUMLAttribute::getAttributeName, attribute -> attribute));
        }

        XMLToUMLManagerToken getChildAt(String name) {
            return children.get(name);
        }

        XMLToUMLManagerToken getParentToken() {
            return parentToken;
        }

        void handleTokenStart() {
            originalToken.getAddClassInstanceList().forEach(XMLToUMLClassInstanceManager::newInstance);
            originalToken.getAddAssociationInstanceList().forEach(XMLToUMLAssociationInstanceManager::newInstance);
            originalToken.getAddRoleInstanceList().forEach((associationInstanceManager, classInstanceManagerMap) -> {
                classInstanceManagerMap.forEach((classInstanceManager, roleNameList) -> {
                    roleNameList.forEach(roleName -> {
                        associationInstanceManager.addRoleInstance(roleName, classInstanceManager.getClassInstance());
                    });
                });
            });
        }

        void handleTokenEnd() {

        }

        void handleAttributeOccurrence(String xmlAttributeName, String attributeValue) {
            XMLToUMLAttribute xmlAttribute = attributes.get(xmlAttributeName);
            xmlAttribute.getAddAttributeInstanceList().forEach((classInstanceManager, umlAttributeNames) ->
                    umlAttributeNames.forEach(umlAttributeName ->
                            classInstanceManager.addAttributeInstance(umlAttributeName, attributeValue)));
            xmlAttribute.getAddRoleInstanceList().forEach((associationInstanceManager, umlRoleNames) ->
                    umlRoleNames.forEach(umlRoleName -> {
                        String targetClassName = intermediateModelModule.getAssociationByName(
                                associationInstanceManager.getAssociationName()).getRoleByName(umlRoleName)
                                .getTargetClass();
                        Set<UMLAttribute> targetClassPKSet =
                                intermediateModelModule.getClassByName(targetClassName).getPkSet();
                        if (targetClassPKSet.size() != 1) {
                            throw new AssertionError();
                        }
                        Map<String, Collection<Object>> attributesMap =
                                Collections.singletonMap(targetClassPKSet.iterator().next().getName(),
                                        Collections.singletonList(attributeValue));
                        IntermediateUMLModelManagerModule.UMLClassInstance classInstance =
                                intermediateModelManagerModule.findInstanceByAttributes(
                                        targetClassName, attributesMap);
                        if (classInstance == null) {
                            classInstance =
                                    intermediateModelManagerModule.newClassInstance(targetClassName, attributesMap);
                        }
                        associationInstanceManager.addRoleInstance(umlRoleName, classInstance);
                    }));
        }

        void handleTextOccurrence(String attributeValue) {
            originalToken.getAddAttributeInstanceFromTextList().forEach(
                    (classInstanceManager, umlAttributeNames) -> umlAttributeNames.forEach(umlAttributeName ->
                            classInstanceManager.addAttributeInstance(umlAttributeName, attributeValue)));
            originalToken.getAddRoleInstanceFromTextList().forEach((associationInstanceManager, umlRoleNames) ->
                    umlRoleNames.forEach(umlRoleName -> {
                        String targetClassName = intermediateModelModule.getAssociationByName(
                                associationInstanceManager.getAssociationName()).getRoleByName(umlRoleName)
                                .getTargetClass();
                        Set<UMLAttribute> targetClassPKSet =
                                intermediateModelModule.getClassByName(targetClassName).getPkSet();
                        if (targetClassPKSet.size() != 1) {
                            throw new AssertionError();
                        }
                        Map<String, Collection<Object>> attributesMap =
                                Collections.<String, Collection<Object>>singletonMap(
                                        targetClassPKSet.iterator().next().getName(),
                                        Collections.<Object>singletonList(attributeValue));
                        IntermediateUMLModelManagerModule.UMLClassInstance classInstance =
                                intermediateModelManagerModule.findInstanceByAttributes(
                                        targetClassName, attributesMap);
                        if (classInstance == null) {
                            classInstance =
                                    intermediateModelManagerModule.newClassInstance(targetClassName, attributesMap);
                        }
                        associationInstanceManager.addRoleInstance(umlRoleName, classInstance);
                    }));
        }
    }
}
