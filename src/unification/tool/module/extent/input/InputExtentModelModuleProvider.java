package unification.tool.module.extent.input;

import unification.tool.module.extent.input.xml.uml.InputExtentXMLToUMLModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.model.IDataModelModule;

public class InputExtentModelModuleProvider {
    private InputExtentModelModuleProvider() {
        throw new AssertionError();
    }

    public static IInputExtentModelModule getExtentModelModule(
            String modelType, String intermediateModelType, String extentFilePath, IDataModelModule dataModelModule,
            IIntermediateModelManagerModule intermediateModelManagerModule) {
        switch (modelType) {
            case "xml":
                switch (intermediateModelType) {
                    case "uml":
                        return InputExtentXMLToUMLModule.newInstance(
                                dataModelModule, extentFilePath, intermediateModelManagerModule);
                    default:
                        throw new IllegalArgumentException("Unrecognized intermediate model type");
                }
            default:
                throw new IllegalArgumentException("Unrecognized input model type");
        }
    }
}
