package unification.tool.module.extent.input.uml.ldap;

import clojure.lang.IPersistentVector;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.extent.input.IInputExtentModelModule;
import unification.tool.module.intermediate.IIntermediateModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.model.IDataModelModule;
import unification.tool.module.model.ldap.LDAPDataModelModule;
import unification.tool.module.model.ldap.LDAPDataModelModule.LDAPClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class InputExtentLDAPToUMLModule implements IInputExtentModelModule {
    private final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final Map<String, LDAPToUMLClass> classes;
    private final String host;
    private final String port;
    private final String dn;
    private final String password;

    public InputExtentLDAPToUMLModule(
            LDAPDataModelModule dataModelModule, String extentFilePath, IntermediateUMLModelManagerModule intermediateModelManagerModule,
            IPersistentVector dataSourceAccess) {
        this.intermediateModelManagerModule = intermediateModelManagerModule;

        if (dataSourceAccess.length() != 4) {
            throw new IllegalStateException("Invalid access vector: wrong size");
        }
        host = String.class.cast(dataSourceAccess.valAt(0));
        port = String.class.cast(dataSourceAccess.valAt(1));
        dn = String.class.cast(dataSourceAccess.valAt(2));
        password = String.class.cast(dataSourceAccess.valAt(3));

        classes = dataModelModule.getClasses().stream().map(LDAPToUMLClass::new)
                .collect(Collectors.toMap(LDAPToUMLClass::getName, e -> e));

        IPersistentVector parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.extent.input.uml.ldap.parser", extentFilePath);

        PARSER.findAllItemsByType(parsedConfiguration, "for-each").forEach(forEachMap -> {
            String ldapClassName = PARSER.keywordNameFromMap(forEachMap, "name");
            PARSER.findAllItemsFromMapValueByType(forEachMap, "other", "add-instance").forEach(addInstanceMap -> {
                LDAPAddClassInstance addClassInstance = classes.get(ldapClassName).newClassMapping(
                        PARSER.keywordNameFromMap(addInstanceMap, "name"));
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping").forEach(mappingMap -> {
                    addClassInstance.addMapping(PARSER.keywordNameFromMap(mappingMap, "path"),
                            PARSER.keywordNameFromMap(mappingMap, "name"));
                });
            });
            PARSER.findAllItemsFromMapValueByType(forEachMap, "other", "add-association").forEach(addInstanceMap -> {
                LDAPAddAssociationInstance addAssociationInstance = classes.get(ldapClassName).newAssociationMapping(
                        PARSER.keywordNameFromMap(addInstanceMap, "name"));
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping-pk").forEach(pkMappingMap -> {
                    addAssociationInstance.addPKMapping(PARSER.keywordNameFromMap(pkMappingMap, "path"),
                            PARSER.keywordNameFromMap(pkMappingMap, "name"));
                });
                PARSER.findAllItemsFromMapValueByType(addInstanceMap, "mappings", "mapping-fk").forEach(fkMappingMap -> {
                    addAssociationInstance.addFKMapping(PARSER.keywordNameFromMap(fkMappingMap, "path"),
                            PARSER.keywordNameFromMap(fkMappingMap, "name"));
                });
            });
        });
    }

    public static InputExtentLDAPToUMLModule newInstance(
            IDataModelModule dataModelModule, String extentFilePath, IIntermediateModelManagerModule intermediateModelManagerModule,
            IPersistentVector dataSourceAccess) {
        if (dataModelModule instanceof LDAPDataModelModule) {
            if (intermediateModelManagerModule instanceof IntermediateUMLModelManagerModule) {
                return new InputExtentLDAPToUMLModule((LDAPDataModelModule) dataModelModule, extentFilePath,
                        (IntermediateUMLModelManagerModule) intermediateModelManagerModule, dataSourceAccess);
            } else {
                throw new IllegalArgumentException("An instance of IntermediateUMLModelManagerModule should be passed");
            }
        } else {
            throw new IllegalArgumentException("An instance of LDAPDataModelModule should be passed");
        }
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDN() {
        return dn;
    }

    public String getPassword() {
        return password;
    }

    public void forEachNotEmptyExtentClass(Consumer<LDAPToUMLClass> consumer) {
        classes.values().stream().filter(LDAPToUMLClass::hasAnyMappings).forEach(consumer);
    }

    public IntermediateUMLModelManagerModule getIntermediateModelManagerModule() {
        return intermediateModelManagerModule;
    }

    static final class LDAPToUMLClass {
        private final LDAPClass originalClass;
        private final List<LDAPAddClassInstance> addClassInstances = new ArrayList<>();
        private final List<LDAPAddAssociationInstance> addAssociationInstances = new ArrayList<>();

        private LDAPToUMLClass(LDAPClass originalClass) {
            this.originalClass = originalClass;
        }

        private LDAPAddClassInstance newClassMapping(String className) {
            LDAPAddClassInstance ldapClassMappingSource = new LDAPAddClassInstance(className);
            addClassInstances.add(ldapClassMappingSource);
            return ldapClassMappingSource;
        }

        private LDAPAddAssociationInstance newAssociationMapping(String associationName) {
            LDAPAddAssociationInstance ldapAssociationMappingSource = new LDAPAddAssociationInstance(associationName);
            addAssociationInstances.add(ldapAssociationMappingSource);
            return ldapAssociationMappingSource;
        }

        public void forEachAddClassInstance(Consumer<LDAPAddClassInstance> consumer) {
            addClassInstances.forEach(consumer);
        }

        public void forEachAddAssociationInstance(Consumer<LDAPAddAssociationInstance> consumer) {
            addAssociationInstances.forEach(consumer);
        }

        private boolean hasAnyMappings() {
            return !addClassInstances.isEmpty() || !addAssociationInstances.isEmpty();
        }

        public String getName() {
            return originalClass.getName();
        }

        public String getStringName() {
            return originalClass.getStringName();
        }
    }

    static final class LDAPAddClassInstance {
        private final String className;
        private final List<LDAPMapping> attributeMappings = new ArrayList<>();

        private LDAPAddClassInstance(String className) {
            this.className = className;
        }

        private void addMapping(String ldapSourceAttributeName, String umlTargetAttributeName) {
            attributeMappings.add(new LDAPMapping(ldapSourceAttributeName, umlTargetAttributeName));
        }

        public String getClassName() {
            return className;
        }

        public void forEachMapping(Consumer<LDAPMapping> consumer) {
            attributeMappings.forEach(consumer);
        }
    }

    static final class LDAPAddAssociationInstance {
        private final String associationName;
        private final List<LDAPMapping> pkMappings = new ArrayList<>();
        private final List<LDAPMapping> fkMappings = new ArrayList<>();

        private LDAPAddAssociationInstance(String associationName) {
            this.associationName = associationName;
        }

        private void addPKMapping(String ldapSourceAttributeName, String umlTargetAttributeName) {
            pkMappings.add(new LDAPMapping(ldapSourceAttributeName, umlTargetAttributeName));
        }

        private void addFKMapping(String ldapSourceAttributeName, String umlTargetAttributeName) {
            fkMappings.add(new LDAPMapping(ldapSourceAttributeName, umlTargetAttributeName));
        }

        public String getAssociationName() {
            return associationName;
        }

        public void forEachPKMapping(Consumer<LDAPMapping> consumer) {
            pkMappings.forEach(consumer);
        }

        public void forEachFKMapping(Consumer<LDAPMapping> consumer) {
            fkMappings.forEach(consumer);
        }
    }

    static final class LDAPMapping {
        private final String ldapSourceAttributeName;
        private final String umlTargetAttributeName;

        public LDAPMapping(String ldapSourceAttributeName, String umlTargetAttributeName) {
            this.ldapSourceAttributeName = ldapSourceAttributeName;
            this.umlTargetAttributeName = umlTargetAttributeName;
        }

        public String getLdapSourceAttributeName() {
            return ldapSourceAttributeName;
        }

        public String getUmlTargetAttributeName() {
            return umlTargetAttributeName;
        }
    }
}
