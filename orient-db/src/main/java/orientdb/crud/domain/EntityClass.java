package orientdb.crud.domain;

/**
 * Created by Puszek_SE on 2015-04-12.
 */

//CREATED FOR OBJECT API, UNUSED
public class EntityClass {
    static int test = 0;
    String name="EXAMPLE";

    int var=0;
    public int getVar(){
        return var;
    }
    public void setVar(int newVar){
        var = newVar;
    }

    EntityClass parent = null;

    public EntityClass(){
        name=name+test;
        raise();
    }

    public EntityClass(EntityClass parent){
        name=name+"hasParent"+test;
        raise();
    }

    synchronized private void raise(){
        test++;
    }

    public EntityClass getParent(){
        return parent;
    }
}
