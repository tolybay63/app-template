package kz.app.appcore.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapNoCase<TYPE> extends LinkedHashMap<String, TYPE> {

    public TYPE get(Object key) {
        key = ((String) key).toLowerCase();
        return super.get(key);
    }

    public TYPE put(String key, TYPE value) {
        key = key.toLowerCase();
        return super.put(key, value);
    }

    public TYPE remove(Object key) {
        key = ((String) key).toLowerCase();
        return super.remove(key);
    }

    public boolean containsKey(Object key) {
        key = ((String) key).toLowerCase();
        return super.containsKey(key);
    }

    public void putAll(Map<? extends String, ? extends TYPE> m) {
        int size = m.size();
        if (size > 0) {
            for (Map.Entry<? extends String, ? extends TYPE> entry : m.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

}
