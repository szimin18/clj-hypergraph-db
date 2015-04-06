package unification.tool.module.intermediate;

import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;

public class IntermediateModelManagerModuleProvider {
    private IntermediateModelManagerModuleProvider() {
        throw new AssertionError();
    }

    public static IIntermediateModelManagerModule getIntermediateModelManagerModule(
            IIntermediateModelModule modelModule) {
        IIntermediateModelManagerModule intermediateModelManagerModule = null;
        if (modelModule instanceof IntermediateUMLModelModule) {
            intermediateModelManagerModule = IntermediateUMLModelManagerModule.newInstance(
                    (IntermediateUMLModelModule) modelModule);
        } else {
            throw new IllegalArgumentException("Unrecognized intermediate model type");
        }
        return intermediateModelManagerModule;
    }
}
