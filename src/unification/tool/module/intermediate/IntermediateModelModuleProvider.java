package unification.tool.module.intermediate;

import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;

public class IntermediateModelModuleProvider {
    private IntermediateModelModuleProvider() {
        throw new AssertionError();
    }

    public static IIntermediateModelModule getIntermediateModelModule(String keywordString,
                                                                      String intermediateModelPath) {
        IIntermediateModelModule intermediateModelModule = null;
        switch (keywordString) {
            case "uml":
                intermediateModelModule = IntermediateUMLModelModule.newInstance(intermediateModelPath);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized intermediate model type");
        }
        return intermediateModelModule;
    }
}
