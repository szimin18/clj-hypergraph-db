package unification.tool.module.extent.input.uml.sql;

import clojure.lang.IPersistentVector;
import unification.tool.module.extent.input.IInputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.IIntermediateModelModule;
import unification.tool.module.model.IDataModelModule;

public class InputExtentSQLToUMLModule implements IInputExtentModelModule {
    public static IInputExtentModelModule newInstance(IDataModelModule dataModelModule, String extentFilePath,
                                                      IIntermediateModelModule intermediateModelModule,
                                                      IIntermediateModelManagerModule intermediateModelManagerModule,
                                                      IPersistentVector dataSourceAccess) {
        return null;
    }
}
