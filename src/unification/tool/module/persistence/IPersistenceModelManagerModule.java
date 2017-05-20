package unification.tool.module.persistence;

import java.util.Collection;

public interface IPersistenceModelManagerModule {
    //Class instances
    public void addClass(String className, String extendedClass);

    public void addClassAttribute(String className, String attributeName, Class attributeType);

    //Associations
    void addAssociation(String associationName, Collection<String> roles, String extendedAssociation);
}
