package unification.tool.module.extent.output.uml.sql;

import unification.tool.module.model.sql.Mapping;
import unification.tool.module.model.sql.Table;

import java.util.*;

/**
 * Created by Puszek_SE on 2015-07-22.
 */
public class OutputExtentTable {

    private final Table table;
    private final String umlClassname;

    private List<Mapping> mappings = new LinkedList<>();

    public OutputExtentTable(Table table,String umlClassname) {
        this.umlClassname = umlClassname;
        this.table = table;
    }

    public List<Mapping> getMappings(){
        return mappings;
    }

    public String getUmlClassname(){
        return umlClassname;
    }

    public void addMapping(Mapping mapping){
        mappings.add(mapping);
    }

    public Table getTable() {
        return table;
    }



}
