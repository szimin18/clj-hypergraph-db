package unification.tool.module.extent.input.uml.sql;

import clojure.lang.IPersistentVector;
import unification.tool.module.extent.input.IInputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.model.IDataModelModule;

public class InputExtentSQLToUMLModule {
    public static IInputExtentModelModule newInstance(IDataModelModule dataModelModule, String extentFilePath,
                                                      IIntermediateModelManagerModule intermediateModelManagerModule,
                                                      IPersistentVector dataSourceAccess) {
        return null;
    }
}
