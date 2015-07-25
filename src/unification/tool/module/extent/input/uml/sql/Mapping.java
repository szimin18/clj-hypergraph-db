package unification.tool.module.extent.input.uml.sql;

public class Mapping{
    private final String columnName;
    private final String attributeName;
    private final Boolean isPrimaryKey;

    public Mapping(String columnName,String attributeName){
        this.columnName = columnName;
        this.attributeName = attributeName;
        isPrimaryKey = false;
    }

    public Mapping(String columnName,String attributeName,Boolean isPrimaryKey){
        this.columnName = columnName;
        this.attributeName = attributeName;
        this.isPrimaryKey = isPrimaryKey;
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