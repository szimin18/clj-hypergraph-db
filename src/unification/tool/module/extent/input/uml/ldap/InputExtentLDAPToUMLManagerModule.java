package unification.tool.module.extent.input.uml.ldap;

import unification.tool.module.extent.input.IInputExtentModelManagerModule;

public class InputExtentLDAPToUMLManagerModule implements IInputExtentModelManagerModule {
    public InputExtentLDAPToUMLManagerModule(InputExtentLDAPToUMLModule modelModule) {
    }

    public static InputExtentLDAPToUMLManagerModule newInstance(InputExtentLDAPToUMLModule modelModule) {
        return new InputExtentLDAPToUMLManagerModule(modelModule);
    }

    @Override public void readInput() {

    }
}
