package unification.tool;

import clojure.lang.IPersistentVector;
import clojure.lang.Keyword;
import org.postgresql.util.PSQLException;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.extent.input.IInputExtentModelManagerModule;
import unification.tool.module.extent.input.IInputExtentModelModule;
import unification.tool.module.extent.output.IOutputExtentModelManagerModule;
import unification.tool.module.extent.output.IOutputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.IIntermediateModelModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.persistence.IPersistenceManagerModule;
import unification.tool.module.persistence.PersistenceManagerModuleProvider;
import unification.tool.module.run.RunModelModule;

public class UnificationTool {
    private static final ClojureParser PARSER = ClojureParser.getInstance();

    public static void main(String[] args) {
        UnificationTool unificationTool = new UnificationTool();
        unificationTool.run("configuration/run.clj");
        //unificationTool.run("configuration/run-cities.clj");
    }

    private void run(String runFilePath) {
        IPersistenceManagerModule persistanceManagerModule =
                PersistenceManagerModuleProvider.getPersistanceManagerModule("/databases/unification");

        try {
            RunModelModule runModelModule = RunModelModule.newInstance(runFilePath);

            RunModelModule.IntermediateModelConfiguration intermediateModelConfiguration =
                    runModelModule.getIntermediateModelConfiguration();

            String intermediateModelPath = intermediateModelConfiguration.getIntermediateModelPath();
            String intermediateModelType = PARSER.getTypeFromFile(intermediateModelPath);
            IIntermediateModelModule intermediateModelModule = IIntermediateModelModule.getInstance(intermediateModelType,
                    intermediateModelPath, persistanceManagerModule);

            IIntermediateModelManagerModule intermediateModelManagerModule = IIntermediateModelManagerModule.getInstance(
                    intermediateModelModule, persistanceManagerModule);

            runModelModule.getInputExtentConfigurations().forEach(extentConfigurations -> {
                String modelFilePath = extentConfigurations.getModelFilePath();
                String modelFileType = PARSER.getTypeFromFile(modelFilePath);
                IDataModelModule dataModelModule = IDataModelModule.getInstance(modelFileType, modelFilePath);

                Object dataSourceAccess = extentConfigurations.getDataSourceAccess();

                if (dataSourceAccess instanceof Keyword && ((Keyword) dataSourceAccess).getName().equals("default")) {
                    dataSourceAccess = null;
                } else if (!(dataSourceAccess instanceof IPersistentVector)) {
                    throw new IllegalStateException(
                            "Access vector should be an instance of IPersistentVector or :default keyword");
                }

                String extentFilePath = extentConfigurations.getExtentFilePath();

                IInputExtentModelModule extentModelModule = IInputExtentModelModule.getInstance(modelFileType, intermediateModelType,
                        extentFilePath, dataModelModule, intermediateModelManagerModule, (IPersistentVector) dataSourceAccess);

                IInputExtentModelManagerModule extentModelManagerModule = IInputExtentModelManagerModule.getInstance(extentModelModule);

                extentModelManagerModule.readInput();
            });

            runModelModule.getOutputExtentConfigurations().forEach(extentConfigurations -> {
                String modelFilePath = extentConfigurations.getModelFilePath();
                String modelFileType = PARSER.getTypeFromFile(modelFilePath);
                IDataModelModule dataModelModule = IDataModelModule.getInstance(modelFileType, modelFilePath);

                Object dataSourceAccess = extentConfigurations.getDataSourceAccess();

                if (dataSourceAccess instanceof Keyword && ((Keyword) dataSourceAccess).getName().equals("default")) {
                    dataSourceAccess = null;
                } else if (!(dataSourceAccess instanceof IPersistentVector)) {
                    throw new IllegalStateException(
                            "Access vector should be an instance of IPersistentVector or :default keyword");
                }

                String extentFilePath = extentConfigurations.getExtentFilePath();

                IOutputExtentModelModule extentModelModule = IOutputExtentModelModule.getExtentModelModule(modelFileType,
                        intermediateModelType, extentFilePath, dataModelModule, intermediateModelManagerModule,
                        (IPersistentVector) dataSourceAccess);

                IOutputExtentModelManagerModule extentModelManagerModule =
                        IOutputExtentModelManagerModule.getExtentManagerModule(extentModelModule);

                try{
                    extentModelManagerModule.writeOutput();
                } catch (Exception psqlException){
                    System.out.println(psqlException.getLocalizedMessage());
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            persistanceManagerModule.shutdownPersistenceManager();
        }
    }
}
