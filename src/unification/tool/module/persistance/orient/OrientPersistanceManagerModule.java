package unification.tool.module.persistance.orient;

import unification.tool.module.persistance.IPersistanceManagerModule;

public class OrientPersistanceManagerModule implements IPersistanceManagerModule {
    private OrientPersistanceManagerModule(String databasePath) {

    }

    public static IPersistanceManagerModule newInstance(String databasePath) {
        return new OrientPersistanceManagerModule(databasePath);
    }
}
