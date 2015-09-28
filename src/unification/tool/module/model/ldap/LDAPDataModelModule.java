package unification.tool.module.model.ldap;

import clojure.lang.IPersistentVector;
import clojure.lang.RT;
import clojure.lang.Seqable;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.model.IDataModelModule;

import java.util.*;

public class LDAPDataModelModule implements IDataModelModule {
    private static final CommonModelParser PARSER = CommonModelParser.getInstance();
    private static final Map<String, LDAPDataModelModule> parsedModels = new HashMap<>();

    private final IPersistentVector accessVector;
    private final Map<String, LDAPClass> classes = new HashMap<>();

    private LDAPDataModelModule(String dataModelPath) {
        Seqable parsedConfiguration = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.model.ldap.parser", dataModelPath);

        String defaultHost = null;
        String defaultPort = null;
        String defaultDN = null;
        String defaultPassword = null;

        List<Object> databaseMaps = PARSER.findAllItemsByType(parsedConfiguration, "database");
        if (!databaseMaps.isEmpty()) {
            Object databaseMap = databaseMaps.get(0);
            List<Object> defaultHostItems = PARSER.findAllItemsFromMapValueByType(databaseMap, "metadata", "default-host");
            if (!defaultHostItems.isEmpty()) {
                defaultHost = PARSER.stringFromMap(defaultHostItems.get(0), "name");
            }
            List<Object> defaultPortItems = PARSER.findAllItemsFromMapValueByType(databaseMap, "metadata", "default-port");
            if (!defaultPortItems.isEmpty()) {
                defaultPort = PARSER.stringFromMap(defaultPortItems.get(0), "name");
            }
            List<Object> defaultDNItems = PARSER.findAllItemsFromMapValueByType(databaseMap, "metadata", "default-dn");
            if (!defaultDNItems.isEmpty()) {
                defaultDN = PARSER.stringFromMap(defaultDNItems.get(0), "name");
            }
            List<Object> defaultPasswordItems = PARSER.findAllItemsFromMapValueByType(databaseMap, "metadata", "default-password");
            if (!defaultPasswordItems.isEmpty()) {
                defaultPassword = PARSER.stringFromMap(defaultPasswordItems.get(0), "name");
            }
        }

        if (defaultHost != null && defaultPort != null && defaultDN != null && defaultPassword != null) {
            accessVector = RT.vector(defaultHost, defaultPort, defaultDN, defaultPassword);
        } else {
            accessVector = null;
        }

        PARSER.findAllItemsByType(parsedConfiguration, "object-class").stream().map(LDAPClass::new).forEach(ldapClass ->
                classes.put(ldapClass.name, ldapClass));
    }

    public static LDAPDataModelModule getInstance(String dataModelPath) {
        LDAPDataModelModule parsedModel = parsedModels.get(dataModelPath);
        if (parsedModel == null) {
            parsedModel = new LDAPDataModelModule(dataModelPath);
            parsedModels.put(dataModelPath, parsedModel);
        }
        return parsedModel;
    }

    @Override public IPersistentVector getAccessVector() {
        return accessVector;
    }

    public Collection<LDAPClass> getClasses() {
        return classes.values();
    }

    public LDAPClass getClassByName(String name) {
        return classes.get(name);
    }

    public final class LDAPClass {
        private final Map<String, LDAPAttribute> attributes = new HashMap<>();
        private final String name;
        private final String stringName;
        private final boolean structural;
        private final List<String> superclasses = new ArrayList<>();

        private LDAPClass(Object classMap) {
            name = PARSER.keywordNameFromMap(classMap, "name");
            stringName = PARSER.stringFromMap(classMap, "string-name");
            Seqable othersSeqable = PARSER.seqableFromMap(classMap, "other");
            structural = !PARSER.findAllItemsByType(othersSeqable, "structural").isEmpty();
            PARSER.findAllItemsByType(othersSeqable, "superclass").forEach(superclassMap ->
                    superclasses.add(PARSER.keywordNameFromMap(superclassMap, "name")));
            PARSER.findAllItemsByType(othersSeqable, "must").stream().map(mustMap -> new LDAPAttribute(mustMap, true))
                    .forEach(ldapAttribute -> attributes.put(ldapAttribute.name, ldapAttribute));
            PARSER.findAllItemsByType(othersSeqable, "may").stream().map(mustMap -> new LDAPAttribute(mustMap, false))
                    .forEach(ldapAttribute -> attributes.put(ldapAttribute.name, ldapAttribute));
        }

        public LDAPAttribute getAttributeByName(String name) {
            if (attributes.containsKey(name)) {
                return attributes.get(name);
            }

            for (String superclass : superclasses) {
                LDAPAttribute result = classes.get(superclass).getAttributeByName(name);
                if (result != null) {
                    return result;
                }
            }

            return null;
        }

        public String getName() {
            return name;
        }

        public String getStringName() {
            return stringName;
        }
    }

    public final class LDAPAttribute {
        private final String name;
        private final String stringName;
        private final boolean mandatory;

        private LDAPAttribute(Object attributeMap, boolean mandatory) {
            name = PARSER.keywordNameFromMap(attributeMap, "name");
            stringName = PARSER.stringFromMap(attributeMap, "string-name");
            this.mandatory = mandatory;
        }

        public String getStringName() {
            return stringName;
        }
    }
}
