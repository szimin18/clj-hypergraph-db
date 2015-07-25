package unification.tool.module.extent.output.uml.xml;

import org.apache.commons.lang.StringUtils;
import unification.tool.module.extent.output.IOutputExtentModelManagerModule;
import unification.tool.module.extent.output.uml.xml.OutputExtentUMLToXMLModule.UMLToXMLToken;

import java.io.File;
import java.io.PrintWriter;

public class OutputExtentUMLToXMLManagerModule implements IOutputExtentModelManagerModule {
    private final static int SPACES_PER_INDENT = 4;
    private final static String INDENT = StringUtils.repeat(" ", SPACES_PER_INDENT);

    private final OutputExtentUMLToXMLModule modelModule;

    public OutputExtentUMLToXMLManagerModule(OutputExtentUMLToXMLModule modelModule) {
        this.modelModule = modelModule;
    }

    public static IOutputExtentModelManagerModule newInstance(OutputExtentUMLToXMLModule modelModule) {
        return new OutputExtentUMLToXMLManagerModule(modelModule);
    }

    @Override public void writeOutput() {
        try {
            final File file = new File(modelModule.getFilePath());
            file.createNewFile();
            modelModule.executeRootModelItems(new IOutputPrinter() {
                private PrintWriter printWriter = new PrintWriter(file);
                private int currentIndent = 0;
                private UMLToXMLToken currentToken = modelModule.getRootToken();
                private boolean notEndedTokenBeginning = false;

                private void manageNotEndedTokenBeginning() {
                    if (notEndedTokenBeginning) {
                        printWriter.print(">\n");
                        notEndedTokenBeginning = false;
                    }
                }

                @Override public void startToken(String tokenName) {
                    manageNotEndedTokenBeginning();
                    currentToken = currentToken.getChildByName(tokenName);
                    printWriter.printf("%s<%s", StringUtils.repeat(INDENT, currentIndent++), currentToken.getTokenStringName());
                    printWriter.flush();
                    notEndedTokenBeginning = true;
                }

                @Override public void addAttribute(String attributeName, String value) {
                    printWriter.printf(" %s=\"%s\"", currentToken.getAttributeByName(attributeName).getAttributeName(), value);
                    printWriter.flush();
                }

                @Override public void addText(String value) {
                    manageNotEndedTokenBeginning();
                    printWriter.printf("%s%s\n", StringUtils.repeat(INDENT, currentIndent), value);
                    printWriter.flush();
                }

                @Override public void endToken() {
                    manageNotEndedTokenBeginning();
                    printWriter.printf("%s</%s>\n", StringUtils.repeat(INDENT, --currentIndent), currentToken.getTokenStringName());
                    printWriter.flush();
                    currentToken = currentToken.getParent();
                }
            });
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    interface IOutputPrinter {
        void startToken(String tokenName);
        void addAttribute(String attributeName, String value);
        void addText(String value);
        void endToken();
    }
}
