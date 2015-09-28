package unification.tool.module.extent.output.uml.ldap;

import java.util.Map;

class MapEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private final V value;

    public MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override public K getKey() {
        return key;
    }

    @Override public V getValue() {
        return value;
    }

    @Override public Object setValue(Object value) {
        throw new UnsupportedOperationException();
    }
}
