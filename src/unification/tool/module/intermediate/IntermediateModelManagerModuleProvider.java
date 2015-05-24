package unification.tool.module.intermediate;

import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.persistence.IPersistenceInstanceManagerModule;

public class IntermediateModelManagerModuleProvider {
    private IntermediateModelManagerModuleProvider() {
        throw new AssertionError();
    }

    public static IIntermediateModelManagerModule getIntermediateModelManagerModule(
            IIntermediateModelModule modelModule, IPersistenceInstanceManagerModule persistenceInstanceManagerModule) {
        IIntermediateModelManagerModule intermediateModelManagerModule;
        if (modelModule instanceof IntermediateUMLModelModule) {
            intermediateModelManagerModule = IntermediateUMLModelManagerModule.newInstance(
                    (IntermediateUMLModelModule) modelModule, persistenceInstanceManagerModule);
        } else {
            throw new IllegalArgumentException("Unrecognized intermediate model type");
        }
        return intermediateModelManagerModule;
    }
}
