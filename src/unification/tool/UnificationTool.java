package unification.tool;

import clojure.lang.IPersistentVector;
import clojure.lang.Keyword;
import clojure.lang.RT;
import com.sun.media.sound.InvalidDataException;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.IIntermediateModelModule;
import unification.tool.module.intermediate.IntermediateModelManagerModuleProvider;
import unification.tool.module.intermediate.IntermediateModelModuleProvider;
import unification.tool.module.model.DataModelModuleProvider;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.run.RunModelModule;

public class UnificationTool {
    private static final String RUN_PARSER_NAMESPACE =
            "unification.tool.common.clojure.parser.clj.config.run.model.parser";

    public static void main(String[] args) throws InvalidDataException {
        UnificationTool unificationTool = new UnificationTool();
        unificationTool.run("configuration/run.clj");
    }

    private void run(String runFilePath) throws InvalidDataException {
        System.out.println("Running unification tool...");

        ClojureParser.getInstance().parse(RUN_PARSER_NAMESPACE, runFilePath);

        RunModelModule runModelModule = RunModelModule.newInstance(runFilePath);

        RunModelModule.IntermediateModelConfiguration intermediateModelConfiguration =
                runModelModule.getIntermediateModelConfiguration();

        String intermediateModelPath = intermediateModelConfiguration.getIntermediateModelPath();
        String intermediateModuleType = ((Keyword) RT.second(RT.readString(RT.var("clojure.core", "slurp")
                .invoke(intermediateModelPath).toString()))).getName();
        IIntermediateModelModule intermediateModelModule =
                IntermediateModelModuleProvider.getIntermediateModelModule(intermediateModuleType,
                        intermediateModelPath);

        IIntermediateModelManagerModule intermediateModelManagerModule =
                IntermediateModelManagerModuleProvider.getIntermediateModelManagerModule(intermediateModelModule);

        runModelModule.getInputExtentConfigurations().forEach(inputExtentConfigurations -> {
            String modelFilePath = inputExtentConfigurations.getModelFilePath();

            String modelModuleType = ((Keyword) RT.second(RT.readString(RT.var("clojure.core", "slurp")
                    .invoke(modelFilePath).toString()))).getName();

            IDataModelModule dataModelModule =
                    DataModelModuleProvider.getDataModelModule(modelModuleType, modelFilePath);

            Object dataSourceAccess = inputExtentConfigurations.getDataSourceAccess();

            if (!(dataSourceAccess instanceof Keyword && ((Keyword) dataSourceAccess).getName().equals("default"))) {
                if (dataSourceAccess instanceof IPersistentVector) {
                    dataModelModule.setAccessVector((IPersistentVector)dataSourceAccess);
                } else {
                    throw new IllegalStateException(
                            "Access vector should be an instance of IPersistentVector or :default keyword");
                }
            }
        });
    }
}
