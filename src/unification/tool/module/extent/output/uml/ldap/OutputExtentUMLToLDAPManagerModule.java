package unification.tool.module.extent.output.uml.ldap;

import unification.tool.module.extent.output.IOutputExtentModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelManagerModule;
import unification.tool.module.intermediate.uml.IntermediateUMLModelModule;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OutputExtentUMLToLDAPManagerModule implements IOutputExtentModelManagerModule {
    private final OutputExtentUMLToLDAPModule modelModule;
    private final IntermediateUMLModelManagerModule intermediateModelManagerModule;
    private final IntermediateUMLModelModule intermediateModelModule;

    private OutputExtentUMLToLDAPManagerModule(OutputExtentUMLToLDAPModule modelModule) {
        this.modelModule = modelModule;
        intermediateModelManagerModule = modelModule.getIntermediateModelManagerModule();
        intermediateModelModule = intermediateModelManagerModule.getModelModule();
    }

    public static OutputExtentUMLToLDAPManagerModule newInstance(OutputExtentUMLToLDAPModule modelModule) {
        return new OutputExtentUMLToLDAPManagerModule(modelModule);
    }

    @Override public void writeOutput() {
        try {
            LdapContext context = new InitialLdapContext(modelModule.createEnvironment(), null);
            context.setRequestControls(null);

            modelModule.forEachMappingClass(mappingClass -> {

                intermediateModelManagerModule.getClassInstances(mappingClass.getName()).forEach(umlClassInstance -> {
                    mappingClass.forEachAddClassInstance(addClassInstance -> {
                        AddInstanceContext addInstanceContext = new AddInstanceContext();
                        addInstanceContext.addAttributeValues(
                                "objectclass", Collections.singletonList(addClassInstance.getClassStringName()));

                        addClassInstance.forEachMapping(attributeMapping -> {
                            addInstanceContext.addAttributeValues(
                                    addClassInstance.getAttributeStringName(attributeMapping.getLdapTargetAttributeName()),
                                    umlClassInstance.getAttributeValues(attributeMapping.getUmlSourceAttributeName(), String.class));
                        });

                        addClassInstance.forEachFKMapping(fkMapping -> {
                            intermediateModelManagerModule.getClassInstances(fkMapping.getForeignClassName())
                                    .forEach(foreignClassInstance -> {
                                        if (intermediateModelManagerModule.areAssociated(umlClassInstance, fkMapping.getPrimaryRoleName(),
                                                fkMapping.getAssociationName(), fkMapping.getForeignRoleName(), foreignClassInstance)) {
                                            addInstanceContext.addAttributeValues(fkMapping.getLdapAttributeName(),
                                                    foreignClassInstance.getAttributeValues(fkMapping.getForeignPKName(), String.class));
                                        }
                                    });
                        });

                        addInstanceContext.addToContext(context, modelModule.getContextName());
                    });
                });
            });
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private static final class AddInstanceContext extends DirContextAdapter {
        private final List<MapEntry<String, List<Object>>> values = new ArrayList<>();

        public void addAttributeValues(String attributeName, Iterable<String> values) {
            if (values.iterator().hasNext()) {
                MapEntry<String, List<Object>> attributeEntry = null;
                for (MapEntry<String, List<Object>> value : this.values) {
                    if (value.getKey().equals(attributeName)) {
                        attributeEntry = value;
                    }
                }
                if (attributeEntry == null) {
                    attributeEntry = new MapEntry<>(attributeName, new ArrayList<>());
                    this.values.add(attributeEntry);
                }

                values.forEach(attributeEntry.getValue()::add);
            }
        }

        public void addToContext(LdapContext context, String base) {
            try {
                String name = base;

                for (MapEntry<String, ?> attributeValueEntry : getAll()) {
                    name = String.format("%s=%s,%s", attributeValueEntry.getKey(), attributeValueEntry.getValue(), name);
//                    context.createSubcontext(new LdapName(name));
                }

                System.out.println("-- Binding " + name);

                values.forEach(entry -> {
                    System.out.println("--   Name " + entry.getKey());
                    entry.getValue().forEach(value -> System.out.println("--     Value " + value));
                });
                System.out.println();
                System.out.flush();

                //                context.bind(name, null, getAttributesForNonEmptyName());

                context.createSubcontext(new LdapName(name), getAttributesForNonEmptyName());

                System.out.println("####### Success #######");
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }

        private List<MapEntry<String, ?>> getAll() {
            ArrayList<MapEntry<String, ?>> result = new ArrayList<>();

            values.forEach(entry -> entry.getValue().forEach(value -> result.add(new MapEntry<>(entry.getKey(), value))));

            return result;
        }

        @Override protected Attributes getAttributesForNonEmptyName() {
            Attributes attributes = new BasicAttributes();

            values.forEach(entry -> {
                Attribute attribute = new BasicAttribute(entry.getKey());
                attributes.put(attribute);
                entry.getValue().forEach(attribute::add);
            });

            return attributes;
        }
    }
}
