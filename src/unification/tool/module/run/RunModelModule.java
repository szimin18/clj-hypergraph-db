package unification.tool.module.run;

import clojure.lang.Seqable;
import com.sun.media.sound.InvalidDataException;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;

import java.util.ArrayList;
import java.util.List;

public class RunModelModule {
    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private boolean isParsed = false;

    private IntermediateModelConfiguration intermediateModelConfiguration;
    private List<ExtentConfiguration> inputExtentConfigurations;
    private List<ExtentConfiguration> outputExtentConfigurations;

    private RunModelModule(String runFilePath) throws InvalidDataException {
        inputExtentConfigurations = new ArrayList<>();
        outputExtentConfigurations = new ArrayList<>();

        Seqable parsedFile = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.run.parser", runFilePath);

        List<Object> intermediateItemes = PARSER.findAllItemsByType(parsedFile, "hdm");

        if (intermediateItemes.isEmpty()) {
            throw new InvalidDataException("No intermediate model provided in configuration file");
        }

        intermediateModelConfiguration = new IntermediateModelConfiguration(
                PARSER.stringFromMap(intermediateItemes.get(0), "filename"));

        PARSER.findAllItemsByType(parsedFile, "input").forEach(modelItem -> {
            String modelFilename = PARSER.stringFromMap(modelItem, "filename");
            Object accessVector = PARSER.objectFromMap(modelItem, "access");

            PARSER.findAllItemsByType(PARSER.seqableFromMap(modelItem, "extents"), "extent").forEach(
                    extentItem -> inputExtentConfigurations.add(new ExtentConfiguration(modelFilename,
                            PARSER.stringFromMap(extentItem, "filename"), accessVector)));
        });

        PARSER.findAllItemsByType(parsedFile, "output").forEach(modelItem -> {
            String modelFilename = PARSER.stringFromMap(modelItem, "filename");
            Object accessVector = PARSER.objectFromMap(modelItem, "access");

            PARSER.findAllItemsByType(PARSER.seqableFromMap(modelItem, "extents"), "extent").forEach(
                    extentItem -> outputExtentConfigurations.add(new ExtentConfiguration(modelFilename,
                            PARSER.stringFromMap(extentItem, "filename"), accessVector)));
        });
    }

    public static RunModelModule newInstance(String runFilePath) throws InvalidDataException {
        return new RunModelModule(runFilePath);
    }

    public IntermediateModelConfiguration getIntermediateModelConfiguration() {
        return intermediateModelConfiguration;
    }

    public List<ExtentConfiguration> getInputExtentConfigurations() {
        return inputExtentConfigurations;
    }

    public List<ExtentConfiguration> getOutputExtentConfigurations() {
        return outputExtentConfigurations;
    }

    public static class IntermediateModelConfiguration {
        private final String intermediateModelPath;

        public IntermediateModelConfiguration(String intermediateModelPath) {
            this.intermediateModelPath = intermediateModelPath;
        }

        public String getIntermediateModelPath() {
            return intermediateModelPath;
        }
    }

    public static class ExtentConfiguration {
        private final String modelFilePath;
        private final String extentFilePath;
        private final Object dataSourceAccess;

        public ExtentConfiguration(String modelFilePath, String extentFilePath, Object dataSourceAccess) {
            this.modelFilePath = modelFilePath;
            this.extentFilePath = extentFilePath;
            this.dataSourceAccess = dataSourceAccess;
        }

        public String getModelFilePath() {
            return modelFilePath;
        }

        public String getExtentFilePath() {
            return extentFilePath;
        }

        public Object getDataSourceAccess() {
            return dataSourceAccess;
        }
    }
}
