package unification.tool.module.extent.input;

import clojure.lang.IPersistentVector;
import unification.tool.module.extent.input.uml.sql.InputExtentSQLToUMLModule;
import unification.tool.module.extent.input.uml.xml.InputExtentXMLToUMLModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.model.IDataModelModule;

public class InputExtentModelModuleProvider {
    private InputExtentModelModuleProvider() {
        throw new AssertionError();
    }

    public static IInputExtentModelModule getExtentModelModule(
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
                        return InputExtentXMLToUMLModule.newInstance(
                                dataModelModule, extentFilePath, intermediateModelManagerModule, dataSourceAccess);
                    case "sql":
                        return InputExtentSQLToUMLModule.newInstance(
                                dataModelModule, extentFilePath, intermediateModelManagerModule, dataSourceAccess);
                    default:
                        throw new IllegalArgumentException("Unrecognized input model type");
                }
            default:
                throw new IllegalArgumentException("Unrecognized intermediate model type");
        }
    }
}
