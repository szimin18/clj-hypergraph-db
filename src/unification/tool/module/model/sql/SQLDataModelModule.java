package unification.tool.module.model.sql;

import clojure.lang.IPersistentVector;
import unification.tool.module.model.IDataModelModule;

public class SQLDataModelModule implements IDataModelModule {
    @Override public IPersistentVector getAccessVector() {
        return null;
    }

    public static IDataModelModule getInstance(String modelFilePath) {
        return null;
    }
}
