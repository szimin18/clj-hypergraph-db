package unification.tool.module.extent.output;

import clojure.lang.IPersistentVector;
import unification.tool.module.extent.output.uml.sql.OutputExtentUMLToSQLModule;
import unification.tool.module.extent.output.uml.xml.OutputExtentUMLToXMLModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.model.IDataModelModule;

public interface IOutputExtentModelModule {
    static IOutputExtentModelModule getExtentModelModule(
            String modelType, String intermediateModelType, String extentFilePath, IDataModelModule dataModelModule,
            IIntermediateModelManagerModule intermediateModelManagerModule, IPersistentVector dataSourceAccess) {
        if (dataSourceAccess == null) {
            dataSourceAccess = dataModelModule.getAccessVector();
        }
        if (dataSourceAccess == null) {
            throw new IllegalStateException("No data source access provided");
        }

        switch (intermediateModelType) {
            case "uml":
                switch (modelType) {
                    case "xml":
                        return OutputExtentUMLToXMLModule.newInstance(
                                dataModelModule, extentFilePath, intermediateModelManagerModule, dataSourceAccess);
                    case "sql":
                        return OutputExtentUMLToSQLModule.newInstance(
                                dataModelModule, extentFilePath, intermediateModelManagerModule, dataSourceAccess);
                    default:
                        throw new IllegalArgumentException("Unrecognized output model type");
                }
            default:
                throw new IllegalArgumentException("Unrecognized intermediate model type");
        }
    }
}
