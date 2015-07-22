package unification.tool.module.extent.input.uml.sql;

import unification.tool.module.model.sql.Table;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Puszek_SE on 2015-07-22.
 */
public class InputExtentTable {

    private final Table table;

    private Set<Instance> instances = new HashSet<>();
    private Set<Association> associations = new HashSet<>();


    public InputExtentTable(Table table){
        this.table = table;
    }

    public void addInstance(Instance instance){
        instances.add(instance);
    }

    public void addAssoctiation(Association association){
        associations.add(association);
    }

    public Table getTable() {
        return table;
    }

    public Set<Association> getAssociations() {
        return associations;
    }

    public Set<Instance> getInstances() {
        return instances;
    }
}
