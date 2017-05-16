package unification.tool.module.model.sql;

import java.util.Optional;

public class Mapping{
    private final String columnName;
    private final String attributeName;
    private final Boolean isPrimaryKey;
    private final Optional<String> prefix;

    public Mapping(String columnName,String attributeName){
        this(columnName, attributeName, null);
    }

    public Mapping(String columnName, String attributeName, String prefix){
        this(columnName, attributeName, false, null);
    }

    public Mapping(String columnName, String attributeName, Boolean isPrimaryKey, String prefix) {
        this.columnName = columnName;
        this.attributeName = attributeName;
        this.isPrimaryKey = isPrimaryKey;
        this.prefix = Optional.ofNullable(prefix);
    }

    public Optional<String> getPrefix() {
        return prefix;
    }

    public Boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getAttributeName() {
        return attributeName;
    }
}