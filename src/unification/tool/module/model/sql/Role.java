package unification.tool.module.model.sql;

/**
 * Created by Puszek_SE on 2015-07-22.
 */
public class Role {
    private final String columnName;
    private final String roleName;

    public Role(String columnName, String roleName){
        this.columnName = columnName;
        this.roleName = roleName;
    }
}
