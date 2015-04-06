package unification.tool.module.intermediate.uml;

import unification.tool.module.intermediate.IIntermediateModelManagerModule;

public class IntermediateUMLModelManagerModule implements IIntermediateModelManagerModule {
    private final IntermediateUMLModelModule modelModule;

    private IntermediateUMLModelManagerModule(IntermediateUMLModelModule modelModule) {
        this.modelModule = modelModule;
    }

    public static IntermediateUMLModelManagerModule newInstance(IntermediateUMLModelModule modelModule) {
        return new IntermediateUMLModelManagerModule(modelModule);
    }
}
