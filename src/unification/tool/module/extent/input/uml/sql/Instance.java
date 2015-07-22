package unification.tool.module.extent.input.uml.sql;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Instance {
        private final String umlClassname;

        private Map<String,Mapping> mappings = new HashMap<>();

        public Instance(String umlClassname){
            this.umlClassname = umlClassname;
        }

        public Mapping getMapping(String columnName){
            return mappings.get(columnName);
        }

        public Collection<Mapping> getMappings(){
            return mappings.values();
        }

        public String getUMLClassname(){
            return umlClassname;
        }

        public void addMapping(String columnName,Mapping mapping){
            mappings.put(columnName,mapping);
        }
    }