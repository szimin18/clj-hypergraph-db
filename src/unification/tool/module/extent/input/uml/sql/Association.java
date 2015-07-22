package unification.tool.module.extent.input.uml.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Puszek_SE on 2015-07-22.
 */
public class Association {
    private final String associationName;

    private Map<String,Role> roles = new HashMap<>();

    public Association(String associationName){
        this.associationName = associationName;
    }

    public Collection<Role> getRoles(){
        return roles.values();
    }

    public Role getRole(String columnName){
        return roles.get(columnName);
    }

    public void addRole(String columnName,Role role){
        roles.put(columnName,role);
    }
}
