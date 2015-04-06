package unification.tool.module.model;

import unification.tool.module.model.xml.XMLDataModelModule;

public class DataModelModuleProvider {
    private DataModelModuleProvider() {
        throw new AssertionError();
    }

    public static IDataModelModule getDataModelModule(String keywordString, String modelFilePath) {
        IDataModelModule dataModelModule = null;
        switch (keywordString) {
            case "xml":
                dataModelModule = XMLDataModelModule.getInstance(modelFilePath);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized data model type");
        }
        return dataModelModule;
    }
}
