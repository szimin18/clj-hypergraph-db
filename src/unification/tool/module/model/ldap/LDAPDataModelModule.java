package unification.tool.module.model.ldap;

import java.util.HashMap;
import java.util.Map;

public class LDAPDataModelModule {
    private static final Map<String, LDAPDataModelModule> parsedModels = new HashMap<>();

    public LDAPDataModelModule(String dataModelPath) {

    }

    public static LDAPDataModelModule getInstance(String dataModelPath) {
        LDAPDataModelModule parsedModel = parsedModels.get(dataModelPath);
        if (parsedModel == null) {
            parsedModel = new LDAPDataModelModule(dataModelPath);
            parsedModels.put(dataModelPath, parsedModel);
        }
        return parsedModel;
    }
}
