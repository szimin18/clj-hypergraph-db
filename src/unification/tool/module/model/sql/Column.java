package unification.tool.module.model.sql;

public class Column{
        private final String name;
        private final String definition;
        private final Boolean notNull;
        private final Boolean primaryKey;

        public Column(String name, String definition){
            this.name = name;
            this.definition = definition;
            notNull = false;
            primaryKey = false;
        }

        public Column(String name, String definition,Boolean primaryKey){
            this.name = name;
            this.definition = definition;
            this.primaryKey = primaryKey;
            notNull = false;
        }

        public Column(String name, String definition, Boolean primaryKey, Boolean notNull){
            this.name = name;
            this.definition = definition;
            this.primaryKey = primaryKey;
            this.notNull = notNull;
        }

        public String getName(){
            return name;
        }

        public String getDefinition(){
            return definition;
        }

        public Boolean canBeNull(){
            return !notNull;
        }

        public Boolean isPrimaryKey(){
            return primaryKey;
        }
    }