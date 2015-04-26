package unification.tool.module.persistence;

import unification.tool.module.persistence.orient.OrientPersistenceManagerModule;

public class PersistenceManagerModuleProvider {
    private PersistenceManagerModuleProvider() {
        throw new AssertionError();
    }

    public static IPersistenceManagerModule getPersistanceManagerModule(String databasePath) {
        return OrientPersistenceManagerModule.newInstance(databasePath);
    }
}