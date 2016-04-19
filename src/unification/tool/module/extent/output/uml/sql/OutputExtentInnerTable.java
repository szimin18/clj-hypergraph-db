package unification.tool.module.extent.output.uml.sql;

import unification.tool.module.model.sql.Table;

/**
 * Created by Puszek_SE on 2015-07-22.
 */
public class OutputExtentInnerTable extends OutputExtentTable {

    private final String mainAttribute;

    public OutputExtentInnerTable(Table table, String umlClassname, String attributeName) {
        super(table, umlClassname);

        mainAttribute = attributeName;
    }

    public String getMainAttribute() {
        return mainAttribute;
    }
}
