package unification.tool.module.extent.input;

import unification.tool.module.extent.input.uml.sql.InputExtentSQLToUMLManagerModule;
import unification.tool.module.extent.input.uml.sql.InputExtentSQLToUMLModule;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLManagerModule;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule;

public class InputExtentModelManagerModuleProvider {
    private InputExtentModelManagerModuleProvider() {
        throw new AssertionError();
    }

    public static IInputExtentModelManagerModule getExtentManagerModule(IInputExtentModelModule modelModule) {
        IInputExtentModelManagerModule iInputExtentModelManagerModule;
        if (modelModule instanceof InputExtentXMLToUMLModule) {
            iInputExtentModelManagerModule = InputExtentXMLToUMLManagerModule.newInstance(
                    (InputExtentXMLToUMLModule) modelModule);
        } else if (modelModule instanceof InputExtentSQLToUMLModule) {
            iInputExtentModelManagerModule = InputExtentSQLToUMLManagerModule.newInstance(
                    (InputExtentSQLToUMLModule) modelModule);
        } else {
            throw new IllegalArgumentException("Unrecognized extent model type");
        }
        return iInputExtentModelManagerModule;
    }
}
