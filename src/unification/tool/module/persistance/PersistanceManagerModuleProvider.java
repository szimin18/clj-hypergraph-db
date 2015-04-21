package unification.tool.module.persistance;

import unification.tool.module.persistance.orient.OrientPersistanceManagerModule;

public class PersistanceManagerModuleProvider {
    private PersistanceManagerModuleProvider() {
        throw new AssertionError();
    }

    public static IPersistanceManagerModule getPersistanceManagerModule(String databasePath) {
        return OrientPersistanceManagerModule.newInstance(databasePath);
    }
}