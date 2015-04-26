package unification.tool.module.persistence;

import java.util.List;

public interface IPersistenceModelManagerModule {
    //Class instances
    public void addClass(String className, String extendedClass);

    public void addClassAttribute(String className, String attributeName, Class attributeType);

    //Associations
    public void addAssociation(String associationName, List<String> roles, String extendedAssociation);

    //Miscellaneous
    public void shutdownPersitanceManager();
}
