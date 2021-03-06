package unification.tool.module.intermediate.uml;

import clojure.lang.IPersistentVector;
import clojure.lang.Seqable;
import org.apache.log4j.Logger;
import unification.tool.common.CommonModelParser;
import unification.tool.common.clojure.parser.ClojureParser;
import unification.tool.module.intermediate.IIntermediateModelModule;
import unification.tool.module.persistence.IPersistenceModelManagerModule;

import java.util.*;

public class IntermediateUMLModelModule implements IIntermediateModelModule {

    private static final Logger logger = Logger.getLogger(IntermediateUMLModelModule.class);

    private static final CommonModelParser PARSER = CommonModelParser.getInstance();

    private final Map<String, UMLClass> classes = new HashMap<>();

    private final Map<String, UMLAssociation> associations = new HashMap<>();

    private final IPersistenceModelManagerModule persistanceModelManagerModule;

    private IntermediateUMLModelModule(String intermediateModelPath,
                                       IPersistenceModelManagerModule persistanceModelManagerModule) {
        this.persistanceModelManagerModule = persistanceModelManagerModule;

        IPersistentVector parsedFile = ClojureParser.getInstance().parse(
                "unification.tool.common.clojure.parser.clj.config.intermediate.uml.parser",
                intermediateModelPath);

        PARSER.findAllItemsByType(parsedFile, "class").forEach(classMap -> {
            String className = PARSER.keywordNameFromMap(classMap, "name");
            Seqable classOther = PARSER.seqableFromMap(classMap, "other");
            classes.put(className, new UMLClass(className, classOther));
            logger.info("New class: "+className);
        });

        classes.values().forEach(umlClass -> umlClass.extendsTemporarySet.forEach(extendsClassname -> {
            umlClass.extendsSet.add(classes.get(extendsClassname));
            classes.get(extendsClassname).extendedBySet.add(umlClass);
        }));

        PARSER.findAllItemsByType(parsedFile, "association").forEach(associationMap -> {
            String associationName = PARSER.keywordNameFromMap(associationMap, "name");
            String associationDescription = PARSER.stringFromMap(associationMap, "description");
            Seqable associationRoles = PARSER.seqableFromMap(associationMap, "roles");
            associations.put(associationName,
                    new UMLAssociation(associationName, associationDescription, associationRoles));
        });

        //        classes.values().forEach(umlClass -> {
        //            System.out.println("#############");
        //            System.out.println(umlClass.name);
        //            System.out.println(umlClass.attributes);
        //            System.out.println(umlClass.extendsTemporarySet);
        //            System.out.println(umlClass.extendsSet);
        //            System.out.println(umlClass.extendedBySet);
        //            System.out.println(umlClass.pkSet);
        //
        //            umlClass.attributes.values().forEach(umlAttribute -> {
        //                System.out.println("  ---------------");
        //                System.out.println("  " + umlAttribute.name);
        //                System.out.println("  " + umlAttribute.uniquenessType);
        //                System.out.println("  " + umlAttribute.isPartOfPk);
        //            });
        //        });
        //
        //        associations.values().forEach(umlAssociation -> {
        //            System.out.println("#############");
        //            System.out.println(umlAssociation.name);
        //            System.out.println(umlAssociation.description);
        //
        //            umlAssociation.roles.values().forEach(umlRole -> {
        //                System.out.println("  ---------------");
        //                System.out.println("  " + umlRole.name);
        //                System.out.println("  " + umlRole.description);
        //                System.out.println("  " + umlRole.targetClass);
        //                System.out.println("  " + umlRole.uniquenessType);
        //            });
        //        });
    }

    public static IIntermediateModelModule newInstance(String intermediateModelPath,
                                                       IPersistenceModelManagerModule persistanceModelManagerModule) {
        return new IntermediateUMLModelModule(intermediateModelPath, persistanceModelManagerModule);
    }

    private static UniquenessType getUniquenessTypeFromMap(Object map) {
        boolean mandatory = PARSER.keywordNameFromMap(map, "L").equals("1");
        boolean unique = PARSER.keywordNameFromMap(map, "U").equals("1");

        UniquenessType uniquenessType;

        if (mandatory) {
            if (unique) {
                uniquenessType = UniquenessType.EXACTLY_ONE;
            } else {
                uniquenessType = UniquenessType.ONE_OR_MORE;
            }
        } else {
            if (unique) {
                uniquenessType = UniquenessType.ZERO_OR_ONE;
            } else {
                uniquenessType = UniquenessType.ANY_NUMBER;
            }
        }

        return uniquenessType;
    }

    public UMLClass getClassByName(String name) {
        return classes.get(name);
    }

    public UMLAssociation getAssociationByName(String name) {
        return associations.get(name);
    }

    public Iterable<UMLClass> iterateClassAndSubclassesBreadthFirst(String className) {
        return () -> new Iterator<UMLClass>() {
            private Queue<UMLClass> buffer = new ArrayDeque<>();

            {
                buffer.add(getClassByName(className));
            }

            @Override public boolean hasNext() {
                return !buffer.isEmpty();
            }

            @Override public UMLClass next() {
                UMLClass nextClass = buffer.poll();
                buffer.addAll(nextClass.extendedBySet);
                return nextClass;
            }
        };
    }

    public static enum UniquenessType {
        ZERO_OR_ONE,
        EXACTLY_ONE,
        ONE_OR_MORE,
        ANY_NUMBER
    }

    public final class UMLClass {
        private final String name;
        private final Map<String, UMLAttribute> attributes = new HashMap<>();
        private final Set<String> extendsTemporarySet = new HashSet<>();
        private final Set<UMLClass> extendsSet = new HashSet<>();
        private final Set<UMLClass> extendedBySet = new HashSet<>();
        private final Set<UMLAttribute> pkSet = new HashSet<>();

        private UMLClass(String name, Seqable other) {
            this.name = name;

            PARSER.findAllItemsByType(other, "extends").forEach(extendsItem ->
                    extendsTemporarySet.add(PARSER.keywordNameFromMap(extendsItem, "superclass")));

            PARSER.findAllItemsByType(other, "attribute").forEach(attributeItem -> {
                String attributeName = PARSER.keywordNameFromMap(attributeItem, "name");
                attributes.put(attributeName, new UMLAttribute(attributeName, attributeItem));
            });

            attributes.values().stream().filter(UMLAttribute::isPartOfPk).forEach(pkSet::add);

            Optional<UMLClass> firstExtendedClassName = extendsSet.stream().findFirst();
            persistanceModelManagerModule.addClass(
                    name, firstExtendedClassName.isPresent() ? firstExtendedClassName.get().name : null);

            attributes.keySet().forEach(attributeName ->
                    persistanceModelManagerModule.addClassAttribute(name, attributeName, String.class));
        }

        public Set<String> getAttributesNames() {
            Set<String> allAttributes = new HashSet<>(attributes.keySet());
            extendsSet.stream().map(UMLClass::getAttributesNames).forEach(allAttributes::addAll);
            return allAttributes;
        }

        public UMLAttribute getAttributeByName(String name) {
            UMLAttribute result = attributes.get(name);
            if (result == null) {
                for (UMLClass umlClass : extendsSet) {
                    result = umlClass.getAttributeByName(name);
                    if (result != null) {
                        break;
                    }
                }
            }
            return result;
        }

        @Override public String toString() {
            return "UML class: " + name;
        }

        public boolean instanceOf(UMLClass superclass) {
            return name.equals(superclass.name) || superclass.extendedBySet.stream().anyMatch(this::instanceOf);
        }

        public Set<UMLAttribute> getPkSet() {
            Set<UMLAttribute> result = new HashSet<>(pkSet);
            extendsSet.forEach(umlClass -> result.addAll(umlClass.getPkSet()));
            return result;
        }

        public String getName() {
            return name;
        }
    }

    public final class UMLAttribute {
        private final String name;
        private final UniquenessType uniquenessType;
        private final boolean isPartOfPk;

        private UMLAttribute(String name, Object attributeMap) {
            this.name = name;
            // PARSER.objectFromMap(attributeMap, "variable-type");
            isPartOfPk = PARSER.booleanFromMap(attributeMap, "pk");
            uniquenessType = getUniquenessTypeFromMap(attributeMap);
        }

        private boolean isPartOfPk() {
            return isPartOfPk;
        }

        @Override public String toString() {
            return "UML attribute: " + name;
        }

        public String getName() {
            return name;
        }
    }

    public final class UMLAssociation {
        private final String name;
        private final String description;
        private final Map<String, UMLRole> roles = new HashMap<>();

        private UMLAssociation(String name, String description, Seqable rolesMaps) {
            this.name = name;
            this.description = description;

            PARSER.findAllItemsByType(rolesMaps, "role").forEach(roleMap ->
                    roles.put(PARSER.keywordNameFromMap(roleMap, "name"), new UMLRole(roleMap)));

            persistanceModelManagerModule.addAssociation(name, roles.keySet(), null);
        }

        public Set<String> getRolesNames() {
            return roles.keySet();
        }

        public UMLRole getRoleByName(String name) {
            return roles.get(name);
        }

        @Override public String toString() {
            return "UML association: " + name;
        }
    }

    public final class UMLRole {
        private final String name;
        private final String description;
        private final String targetClass;
        private final UniquenessType uniquenessType;

        private UMLRole(Object roleMap) {
            name = PARSER.keywordNameFromMap(roleMap, "name");
            description = PARSER.stringFromMap(roleMap, "description");
            targetClass = PARSER.keywordNameFromMap(roleMap, "target-class");
            uniquenessType = getUniquenessTypeFromMap(roleMap);
        }

        @Override public String toString() {
            return "UML role: " + name;
        }

        public String getTargetClass() {
            return targetClass;
        }
    }
}
