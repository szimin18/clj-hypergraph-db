package unification.tool.module.persistance;

import java.util.List;

/**
 * Created by Puszek_SE on 2015-04-21.
 */
public interface IPersistanceModelModule {

    //Class instances
    public void addClass(String className, String extendedClass);

    public void addClassAttribute(String className, String attributeName, Class attributeType);

    //Associations
    public void addAssociation(String associationName, List<String> roles, String extendedAssociation);

    //Miscellaneous
    public void shutdownPersitanceManager();

}