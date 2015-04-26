package unification.tool.module.intermediate.uml;

import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.persistence.IPersistenceManagerModule;

public class IntermediateUMLModelManagerModule implements IIntermediateModelManagerModule {
    private final IntermediateUMLModelModule modelModule;
    private final IPersistenceManagerModule persistanceManagerModule;

    private IntermediateUMLModelManagerModule(IntermediateUMLModelModule modelModule,
                                              IPersistenceManagerModule persistanceManagerModule) {
        this.modelModule = modelModule;
        this.persistanceManagerModule = persistanceManagerModule;
    }

    public static IntermediateUMLModelManagerModule newInstance(IntermediateUMLModelModule modelModule,
                                                                IPersistenceManagerModule persistanceManagerModule) {
        return new IntermediateUMLModelManagerModule(modelModule, persistanceManagerModule);
    }
}
