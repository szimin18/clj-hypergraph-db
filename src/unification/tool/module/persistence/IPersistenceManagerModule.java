package unification.tool.module.persistence;

public interface IPersistenceManagerModule extends IPersistenceInstanceManagerModule, IPersistenceModelManagerModule {
    public void shutdownPersitanceManager();
}
