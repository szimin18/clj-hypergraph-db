package unification.tool.module.model.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Table{
    private Map<String,Column> columns = new HashMap<>();
    private final String name;
    private final String definition;

    public Table(String name,String definition){
        this.name = name;
        this.definition = definition;
    }

    public void addColumn(Column column,String definition){
        columns.put(definition,column);
    }

    public Column getColumn(String columnDefinition){
        return columns.get(columnDefinition);
    }

    public Collection<String> getColumnDefinitions(){
        return columns.keySet();
    }

    public Collection<Column> getColumns(){
        return columns.values();
    }

    public String getName(){
        return name;
    }

    public String getDefinition(){
        return definition;
    }
}