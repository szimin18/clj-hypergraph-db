package unification.tool.module.extent.output.uml.xml;

import unification.tool.module.extent.output.IOutputExtentModelManagerModule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class OutputExtentUMLToXMLManagerModule implements IOutputExtentModelManagerModule {
    private final OutputExtentUMLToXMLModule modelModule;

    public OutputExtentUMLToXMLManagerModule(OutputExtentUMLToXMLModule modelModule) {
        this.modelModule = modelModule;
    }

    public static IOutputExtentModelManagerModule newInstance(OutputExtentUMLToXMLModule modelModule) {
        return new OutputExtentUMLToXMLManagerModule(modelModule);
    }

    @Override public void writeOutput() {
        try {
            File file = new File(modelModule.getFilePath());
            if (!file.exists()) {
                file.createNewFile();
            }
            modelModule.executeRootModelItems(new PrintWriter(file));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
