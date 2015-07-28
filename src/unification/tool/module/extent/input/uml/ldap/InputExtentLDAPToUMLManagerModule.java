package unification.tool.module.extent.input.uml.ldap;

import unification.tool.module.extent.input.IInputExtentModelManagerModule;
import unification.tool.module.extent.input.uml.ldap.InputExtentLDAPToUMLModule.LDAPMapping;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule.UMLAssociationInstance;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule.UMLClassInstance;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Collections;
import java.util.Hashtable;
import java.util.function.Consumer;

public class InputExtentLDAPToUMLManagerModule implements IInputExtentModelManagerModule {
    private final InputExtentLDAPToUMLModule modelModule;
    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;

    public InputExtentLDAPToUMLManagerModule(InputExtentLDAPToUMLModule modelModule) {
        this.modelModule = modelModule;
        intermediateModelManagerModule = modelModule.getIntermediateModelManagerModule();
    }

    public static InputExtentLDAPToUMLManagerModule newInstance(InputExtentLDAPToUMLModule modelModule) {
        return new InputExtentLDAPToUMLManagerModule(modelModule);
    }


    private SearchControls getSimpleSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(120000);
        return searchControls;
    }

    @Override public void readInput() {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, "ldap://localhost:389");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, "cn=admin,Mds-Vo-name=local,o=grid");
        environment.put(Context.SECURITY_CREDENTIALS, "alamakota");
        environment.put(Context.BATCHSIZE, "100");

        modelModule.forEachNotEmptyExtentClass(extentClass -> {
            try {
                LdapContext context = new InitialLdapContext(environment, null);
                context.setRequestControls(null);
                NamingEnumeration<SearchResult> namingEnumeration = context.search("Mds-Vo-name=local,o=grid",
                        String.format("(objectclass=%s)", extentClass.getStringName()), getSimpleSearchControls());
                while (namingEnumeration.hasMore()) {
                    Attributes attributes = namingEnumeration.next().getAttributes();
                    extentClass.forEachAddClassInstance(addClassInstance -> {
                        UMLClassInstance newClassInstance = intermediateModelManagerModule.newClassInstance(
                                addClassInstance.getClassName(), Collections.emptyMap());
                        addClassInstance.forEachMapping(mapping -> {
                            try {
                                Attribute attribute = attributes.get(mapping.getLdapSourceAttributeName());
                                String umlTargetAttributeName = mapping.getUmlTargetAttributeName();
                                if (attribute != null) {
                                    NamingEnumeration<?> values = attribute.getAll();
                                    while (values.hasMore()) {
                                        newClassInstance.addAttributeInstance(umlTargetAttributeName, values.next());
                                    }
                                    values.close();
                                }
                            } catch (NamingException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                }
                namingEnumeration.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        });

        modelModule.forEachNotEmptyExtentClass(extentClass -> {
            try {
                LdapContext context = new InitialLdapContext(environment, null);
                context.setRequestControls(null);
                NamingEnumeration<SearchResult> namingEnumeration = context.search("Mds-Vo-name=local,o=grid",
                        String.format("(objectclass=%s)", extentClass.getStringName()), getSimpleSearchControls());
                while (namingEnumeration.hasMore()) {
                    Attributes attributes = namingEnumeration.next().getAttributes();
                    extentClass.forEachAddAssociationInstance(addAssociationInstance -> {
                        String associationName = addAssociationInstance.getAssociationName();
                        UMLAssociationInstance umlAssociationInstance = intermediateModelManagerModule
                                .newAssociationInstance(associationName, Collections.emptyMap());
                        Consumer<LDAPMapping> ldapMappingConsumer = mapping -> {
                            try {
                                Attribute attribute = attributes.get(mapping.getLdapSourceAttributeName());
                                if (attribute != null) {
                                    String targetRoleName = mapping.getUmlTargetAttributeName();
                                    String targetClassName = intermediateModelManagerModule.getModelModule()
                                            .getAssociationByName(associationName).getRoleByName(targetRoleName).getTargetClass();
                                    String umlKeyName = intermediateModelManagerModule.getModelModule().getClassByName(targetClassName)
                                            .getPkSet().iterator().next().getName();
                                    Object umlKeyValue = attribute.get();
                                    UMLClassInstance targetClassInstance = intermediateModelManagerModule.findInstanceByAttributes(
                                            targetClassName, Collections.singletonMap(umlKeyName, Collections.singletonList(umlKeyValue)));
                                    if (targetClassInstance == null) {
                                        targetClassInstance = intermediateModelManagerModule.newClassInstance(targetClassName,
                                                Collections.singletonMap(umlKeyName, Collections.singletonList(umlKeyValue)));
                                    }
                                    umlAssociationInstance.addRoleInstance(targetRoleName, targetClassInstance);
                                }
                            } catch (NamingException e) {
                                e.printStackTrace();
                            }
                        };
                        addAssociationInstance.forEachPKMapping(ldapMappingConsumer);
                        addAssociationInstance.forEachFKMapping(ldapMappingConsumer);
                    });
                }
                namingEnumeration.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        });
    }
}