package unification.tool.module.persistance;

import java.util.HashMap;
import java.util.Map;

public abstract class IPersistanceManagerModule implements IPersistanceModelModule,IPersistanceInstanceModule{
    protected Map<String,Object> classes = new HashMap<>();
    protected Map<String,Object> associations = new HashMap<>();
}