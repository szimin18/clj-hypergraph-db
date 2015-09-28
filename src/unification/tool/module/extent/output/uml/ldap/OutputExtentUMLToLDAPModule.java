package unification.tool.module.extent.output.uml.ldap;

import clojure.lang.IPersistentVector;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.extent.output.IOutputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule.UMLClass;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.ldap.LDAPDataModelModule;
import unification.tool.module.model.ldap.LDAPDataModelModule.LDAPClass;

import javax.naming.Context;
import java.util.*;
import java.util.function.Consumer;

public class OutputExtentUMLToLDAPModule implements IOutputExtentModelModule {
    private final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final Map<String, UMLToLDAPClass> classes = new HashMap<>();
    private final String host;
    private final String port;
    private final String dn;
    private final String password;

    private OutputExtentUMLToLDAPModule(
            LDAPDataModelModule dataModelModule, String extentFilePath, IntermediateUMLModelManagerModule intermediateModelManagerModule,
            IPersistentVector dataSourceAccess) {
        this.intermediateModelManagerModule = intermediateModelManagerModule;
        IntermediateUMLModelModule modelModule = intermediateModelManagerModule.getModelModule();

        if (dataSourceAccess.length() != 4) {
            throw new IllegalStateException("Invalid access vector: wrong size");
        }

        host = String.class.cast(dataSourceAccess.valAt(0));
        port = String.class.cast(dataSourceAccess.valAt(1));
        dn = String.class.cast(dataSourceAccess.valAt(2));
        password = String.class.cast(dataSourceAccess.valAt(3));

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.output.uml.ldap.parser", extentFilePath);

        PARSER.findAllItemsByType(parsedConfiguration, "for-each").forEach(forEachMap -> {
            String umlClassName = PARSER.keywordNameFromMap(forEachMap, "name");
            PARSER.findAllItemsFromMapValueByType(forEachMap, "other", "add-instance").forEach(addInstanceMap -> {
                if (!classes.containsKey(umlClassName)) {
                    classes.put(umlClassName, new UMLToLDAPClass(modelModule.getClassByName(umlClassName)));
                }
                LDAPAddClassInstance addClassInstance = classes.get(umlClassName).newClassMapping(
                        dataModelModule.getClassByName(PARSER.keywordNameFromMap(addInstanceMap, "name")));
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping").forEach(mappingMap -> {
                    addClassInstance.addMapping(PARSER.keywordNameFromMap(mappingMap, "name"),
                            PARSER.keywordNameFromMap(mappingMap, "path"));
                });
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping-fk").forEach(mappingMap -> {
                    addClassInstance.addFKMapping(PARSER.keywordNameFromMap(mappingMap, "primary-role"),
                            PARSER.keywordNameFromMap(mappingMap, "association-name"),
                            PARSER.keywordNameFromMap(mappingMap, "foreign-role"),
                            PARSER.keywordNameFromMap(mappingMap, "foreign-class-name"),
                            PARSER.keywordNameFromMap(mappingMap, "foreign-pk-name"),
                            PARSER.keywordNameFromMap(mappingMap, "attribute-name"));
                });
            });
        });
    }

    public static OutputExtentUMLToLDAPModule newInstance(
            IDataModelModule dataModelModule, String extentFilePath, IIntermediateModelManagerModule intermediateModelManagerModule,
            IPersistentVector dataSourceAccess) {
        if (dataModelModule instanceof LDAPDataModelModule) {
            if (intermediateModelManagerModule instanceof IntermediateUMLModelManagerModule) {
                return new OutputExtentUMLToLDAPModule((LDAPDataModelModule) dataModelModule, extentFilePath,
                        (IntermediateUMLModelManagerModule) intermediateModelManagerModule, dataSourceAccess);
            } else {
                throw new IllegalArgumentException("An instance of IntermediateUMLModelManagerModule should be passed");
            }
        } else {
            throw new IllegalArgumentException("An instance of LDAPDataModelModule should be passed");
        }
    }

    void forEachMappingClass(Consumer<UMLToLDAPClass> consumer) {
        classes.values().forEach(consumer);
    }

    IntermediateUMLModelManagerModule getIntermediateModelManagerModule() {
        return intermediateModelManagerModule;
    }

    String getContextName() {
        return "Mds-Vo-name=local,o=grid";
    }

    Hashtable<String, String> createEnvironment() {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, String.format("ldap://%s:%s", host, port));
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, dn);
        environment.put(Context.SECURITY_CREDENTIALS, password);
        environment.put(Context.BATCHSIZE, "100");
        return environment;
    }

    static final class UMLToLDAPClass {
        private final UMLClass originalClass;
        private final List<LDAPAddClassInstance> addClassInstances = new ArrayList<>();

        private UMLToLDAPClass(UMLClass originalClass) {
            this.originalClass = originalClass;
        }

        private LDAPAddClassInstance newClassMapping(LDAPClass ldapClass) {
            LDAPAddClassInstance ldapAddClassInstance = new LDAPAddClassInstance(ldapClass);
            addClassInstances.add(ldapAddClassInstance);
            return ldapAddClassInstance;
        }

        public String getName() {
            return originalClass.getName();
        }

        public void forEachAddClassInstance(Consumer<LDAPAddClassInstance> consumer) {
            addClassInstances.forEach(consumer);
        }
    }

    static final class LDAPAddClassInstance {
        private final LDAPClass ldapClass;
        private final List<LDAPMapping> attributeMappings = new ArrayList<>();
        private final List<LDAPForeignKeyMapping> fkMappings = new ArrayList<>();

        private LDAPAddClassInstance(LDAPClass ldapClass) {
            this.ldapClass = ldapClass;
        }

        private void addMapping(String ldapSourceAttributeName, String umlTargetAttributeName) {
            attributeMappings.add(new LDAPMapping(ldapSourceAttributeName, umlTargetAttributeName));
        }

        private void addFKMapping(String primaryRoleName, String associationName, String foreignRoleName, String foreignClassName,
                                  String foreignPKName, String ldapAttributeName) {
            fkMappings.add(new LDAPForeignKeyMapping(primaryRoleName, associationName, foreignRoleName, foreignClassName, foreignPKName,
                    ldapAttributeName));
        }

        public String getAttributeStringName(String attributeName) {
            return ldapClass.getAttributeByName(attributeName).getStringName();
        }

        public String getClassStringName() {
            return ldapClass.getStringName();
        }

        public String getClassName() {
            return ldapClass.getName();
        }

        public void forEachMapping(Consumer<LDAPMapping> consumer) {
            attributeMappings.forEach(consumer);
        }

        public void forEachFKMapping(Consumer<LDAPForeignKeyMapping> consumer) {
            fkMappings.forEach(consumer);
        }
    }

    static final class LDAPMapping {
        private final String umlSourceAttributeName;
        private final String ldapTargetAttributeName;

        public LDAPMapping(String umlSourceAttributeName, String ldapTargetAttributeName) {
            this.umlSourceAttributeName = umlSourceAttributeName;
            this.ldapTargetAttributeName = ldapTargetAttributeName;
        }

        public String getUmlSourceAttributeName() {
            return umlSourceAttributeName;
        }

        public String getLdapTargetAttributeName() {
            return ldapTargetAttributeName;
        }
    }

    static final class LDAPForeignKeyMapping {
        private final String primaryRoleName;
        private final String associationName;
        private final String foreignRoleName;
        private final String foreignClassName;
        private final String foreignPKName;
        private final String ldapAttributeName;

        LDAPForeignKeyMapping(String primaryRoleName, String associationName, String foreignRoleName, String foreignClassName,
                              String foreignPKName, String ldapAttributeName) {
            this.primaryRoleName = primaryRoleName;
            this.associationName = associationName;
            this.foreignRoleName = foreignRoleName;
            this.foreignClassName = foreignClassName;
            this.foreignPKName = foreignPKName;
            this.ldapAttributeName = ldapAttributeName;
        }

        public String getPrimaryRoleName() {
            return primaryRoleName;
        }

        public String getAssociationName() {
            return associationName;
        }

        public String getForeignRoleName() {
            return foreignRoleName;
        }

        public String getForeignClassName() {
            return foreignClassName;
        }

        public String getForeignPKName() {
            return foreignPKName;
        }

        public String getLdapAttributeName() {
            return ldapAttributeName;
        }
    }
}
