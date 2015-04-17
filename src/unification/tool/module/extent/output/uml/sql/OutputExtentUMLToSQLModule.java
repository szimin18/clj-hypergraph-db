package unification.tool.module.extent.output.uml.sql;

import clojure.lang.IPersistentVector;
import unification.tool.module.extent.output.IOutputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.model.IDataModelModule;

public class OutputExtentUMLToSQLModule {
    public static IOutputExtentModelModule newInstance(IDataModelModule dataModelModule, String extentFilePath,
                                                       IIntermediateModelManagerModule intermediateModelManagerModule,
                                                       IPersistentVector dataSourceAccess) {
        return null;
    }
}
