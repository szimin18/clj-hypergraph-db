package orientdb.crud;

import com.orientechnologies.orient.core.metadata.schema.OType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Puszek_SE on 2015-04-12.
 */
public class TestScenario {

    public enum Type{
        STANDARD,
        EXTENDS
    }

    public class Property{
        public Type type;
        Object value;

        public Property(Type type,Object value){
            this.type = type;
            this.value = value;
        }

        public Property(Type type){
            this.type = type;
        }

        public Object get(){
            return value;
        }
    }

    Map<String,Map<String,Property>> testClasses = new HashMap<String,Map<String,Property>>(){{
        put("Base",new HashMap<String,Property>());
        this.get("Base").put("param1",new Property(Type.STANDARD, OType.STRING));

        put("Child",new HashMap<String, Property>());
        this.get("Child").put("Base",new Property(Type.EXTENDS));
        this.get("Child").put("param2",new Property(Type.STANDARD,OType.INTEGER));
    }};

}
