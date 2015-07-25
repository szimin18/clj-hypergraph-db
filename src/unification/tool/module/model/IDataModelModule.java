package unification.tool.module.model;

import clojure.lang.IPersistentVector;
import unification.tool.module.model.sql.SQLDataModelModule;
import unification.tool.module.model.xml.XMLDataModelModule;

public interface IDataModelModule {
    IPersistentVector getAccessVector();

    static IDataModelModule getInstance(String keywordString, String modelFilePath) {
        IDataModelModule dataModelModule = null;
        switch (keywordString) {
            case "xml":
                dataModelModule = XMLDataModelModule.getInstance(modelFilePath);
                break;
            case "sql":
                dataModelModule = SQLDataModelModule.getInstance(modelFilePath);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized data model type");
        }
        return dataModelModule;
    }
}
