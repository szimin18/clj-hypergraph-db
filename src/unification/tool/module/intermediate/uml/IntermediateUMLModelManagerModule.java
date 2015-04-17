package unification.tool.module.intermediate.uml;

import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.persistance.IPersistanceManagerModule;

public class IntermediateUMLModelManagerModule implements IIntermediateModelManagerModule {
    private final IntermediateUMLModelModule modelModule;
    private final IPersistanceManagerModule persistanceManagerModule;

    private IntermediateUMLModelManagerModule(IntermediateUMLModelModule modelModule,
                                              IPersistanceManagerModule persistanceManagerModule) {
        this.modelModule = modelModule;
        this.persistanceManagerModule = persistanceManagerModule;
    }

    public static IntermediateUMLModelManagerModule newInstance(IntermediateUMLModelModule modelModule,
                                                                IPersistanceManagerModule persistanceManagerModule) {
        return new IntermediateUMLModelManagerModule(modelModule, persistanceManagerModule);
    }
}
