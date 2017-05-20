package unification.tool.module.extent.output;

import unification.tool.module.extent.output.uml.ldap.OutputExtentUMLToLDAPManagerModule;
import unification.tool.module.extent.output.uml.ldap.OutputExtentUMLToLDAPModule;
import unification.tool.module.extent.output.uml.sql.OutputExtentUMLToSQLManagerModule;
import unification.tool.module.extent.output.uml.sql.OutputExtentUMLToSQLModule;
import unification.tool.module.extent.output.uml.xml.OutputExtentUMLToXMLManagerModule;
import unification.tool.module.extent.output.uml.xml.OutputExtentUMLToXMLModule;

public interface IOutputExtentModelManagerModule {
    void writeOutput() throws Exception;

    static IOutputExtentModelManagerModule getExtentManagerModule(IOutputExtentModelModule modelModule) {
        IOutputExtentModelManagerModule iInputExtentModelManagerModule;
        if (modelModule instanceof OutputExtentUMLToXMLModule) {
            iInputExtentModelManagerModule = OutputExtentUMLToXMLManagerModule.newInstance(
                    (OutputExtentUMLToXMLModule) modelModule);
        } else if (modelModule instanceof OutputExtentUMLToSQLModule) {
            iInputExtentModelManagerModule = OutputExtentUMLToSQLManagerModule.newInstance(
                    (OutputExtentUMLToSQLModule) modelModule);
        } else if (modelModule instanceof OutputExtentUMLToLDAPModule) {
            iInputExtentModelManagerModule = OutputExtentUMLToLDAPManagerModule.newInstance(
                    (OutputExtentUMLToLDAPModule) modelModule);
        } else {
            throw new IllegalArgumentException("Unrecognized extent model type");
        }
        return iInputExtentModelManagerModule;
    }
}
