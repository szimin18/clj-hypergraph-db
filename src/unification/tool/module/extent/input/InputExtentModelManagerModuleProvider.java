package unification.tool.module.extent.input;

import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLManagerModule;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule;

public class InputExtentModelManagerModuleProvider {
    private InputExtentModelManagerModuleProvider() {
        throw new AssertionError();
    }

    public static IInputExtentModelManagerModule getExtentManagerModule(IInputExtentModelModule modelModule) {
        IInputExtentModelManagerModule iInputExtentModelManagerModule = null;
        if (modelModule instanceof InputExtentXMLToUMLModule) {
            iInputExtentModelManagerModule = InputExtentXMLToUMLManagerModule.newInstance(
                    (InputExtentXMLToUMLModule) modelModule);
        } else {
            throw new IllegalArgumentException("Unrecognized extent model type");
        }
        return iInputExtentModelManagerModule;
    }
}
