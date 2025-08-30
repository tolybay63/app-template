package kz.kis.kiscore.model;

import kz.kis.kiscore.utils.*;

import java.util.*;

public class DbRec extends HashMap<String, Object> {

    public DbRec() {
        super();
    }

    public DbRec(Map<String, Object> values) {

        for (String key : values.keySet()) {
            this.put(key, values.get(key));
        }
    }

    @Override
    public Object get(Object key) {

        String keyStr = (String) key;

        if (containsKey(keyStr)) {
            return super.get(keyStr);
        }

        return super.get(keyStr.toLowerCase());
    }

    /**
     * @return получить значение, приведённое к Boolean
     */
    public Boolean getBoolean(Object key) {

        return UtCnv.toBoolean(this.get(key));
    }

    /**
     * @return получить значение, приведённое к String
     */
    public String getString(Object key) {

        return UtCnv.toString(this.get(key));
    }

    /**
     * @return получить значение, приведённое к Long
     */
    public long getLong(Object key) {

        return UtCnv.toLong(this.get(key));
    }

    /**
     * @return получить значение, приведённое к Int
     */
    public int getInt(Object key) {

        return UtCnv.toInt(this.get(key));
    }

    /**
     * @return получить значение, приведённое к Map
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getMap(Object key) {

        return (Map<K, V>) this.get(key);
    }

}
