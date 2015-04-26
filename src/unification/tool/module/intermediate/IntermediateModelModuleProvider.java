package unification.tool.module.intermediate;

import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.persistence.IPersistenceModelManagerModule;

public class IntermediateModelModuleProvider {
    private IntermediateModelModuleProvider() {
        throw new AssertionError();
    }

    public static IIntermediateModelModule getIntermediateModelModule(
            String keywordString, String intermediateModelPath,
            IPersistenceModelManagerModule persistanceModelManagerModule) {
        IIntermediateModelModule intermediateModelModule;
        switch (keywordString) {
            case "uml":
                intermediateModelModule = IntermediateUMLModelModule.newInstance(
                        intermediateModelPath, persistanceModelManagerModule);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized intermediate model type");
        }
        return intermediateModelModule;
    }
}
