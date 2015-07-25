package unification.tool.module.extent.input.uml.ldap;

import clojure.lang.IPersistentVector;
import unification.tool.module.extent.input.IInputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.IIntermediateModelModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.ldap.LDAPDataModelModule;

public class InputExtentLDAPToUMLModule implements IInputExtentModelModule {
    public InputExtentLDAPToUMLModule(
            LDAPDataModelModule dataModelModule, String extentFilePath, IntermediateUMLModelModule intermediateModelModule,
            IntermediateUMLModelManagerModule intermediateModelManagerModule, IPersistentVector dataSourceAccess) {

    }

    public static InputExtentLDAPToUMLModule newInstance(
            IDataModelModule dataModelModule, String extentFilePath, IIntermediateModelModule intermediateModelModule,
            IIntermediateModelManagerModule intermediateModelManagerModule, IPersistentVector dataSourceAccess) {
        if (dataModelModule instanceof LDAPDataModelModule) {
            if (intermediateModelManagerModule instanceof IntermediateUMLModelManagerModule) {
                if (intermediateModelModule instanceof IntermediateUMLModelModule) {
                    return new InputExtentLDAPToUMLModule((LDAPDataModelModule) dataModelModule, extentFilePath,
                            (IntermediateUMLModelModule) intermediateModelModule,
                            (IntermediateUMLModelManagerModule) intermediateModelManagerModule, dataSourceAccess);
                } else {
                    throw new IllegalArgumentException("An instance of IntermediateUMLModelModule should be passed");
                }
            } else {
                throw new IllegalArgumentException("An instance of IntermediateUMLModelManagerModule should be passed");
            }
        } else {
            throw new IllegalArgumentException("An instance of LDAPDataModelModule should be passed");
        }
    }
}
