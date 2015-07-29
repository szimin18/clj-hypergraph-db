package unification.tool.module.model.sql;

public class MappingRelation extends Mapping{

    String associationName;


    public MappingRelation(String columnName, String attributeName){
        super(columnName,attributeName);
    }

    public MappingRelation(String columnName, String attributeName, Boolean isPrimaryKey){
        super(columnName,attributeName,isPrimaryKey);
    }

}