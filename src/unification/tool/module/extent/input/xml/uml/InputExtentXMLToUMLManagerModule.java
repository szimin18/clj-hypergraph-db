package unification.tool.module.extent.input.xml.uml;

import unification.tool.module.extent.input.IInputExtentModelManagerModule;

public class InputExtentXMLToUMLManagerModule implements IInputExtentModelManagerModule {
    private InputExtentXMLToUMLManagerModule(InputExtentXMLToUMLModule modelModule) {

    }

    public static final InputExtentXMLToUMLManagerModule newInstance(InputExtentXMLToUMLModule modelModule) {
        return new InputExtentXMLToUMLManagerModule(modelModule);
    }

    @Override public void readInputConfiguration() {

    }
}
