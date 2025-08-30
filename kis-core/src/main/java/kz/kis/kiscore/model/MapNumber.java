package kz.kis.kiscore.model;

import kz.kis.kiscore.utils.*;

import java.util.*;

/**
 * Пытается привести числовой ключ к числовому типу МИНИМАЛЬНОЙ длины.
 * Полезно, если используются константы типа Long, а в Map значения типа Integer.
 * Так бывает, если Map приехала с БД, где jdbc-драйвер тип подставил сам.
 */
public class MapNumber extends HashMap {

    public MapNumber() {
        super();
    }

    @Override
    public Object get(Object key) {
        key = getMinimalNumberType(key);

        return super.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        Object keyValue = getMinimalNumberType(key);
        return super.put(keyValue, value);
    }

    private Object getMinimalNumberType(Object key) {
        if (key instanceof Long keyLong) {
            if (keyLong >= Integer.MIN_VALUE && keyLong <= Integer.MAX_VALUE) {
                key = UtCnv.toInt(key);
            }
        } else if (key instanceof Integer || key instanceof Short || key instanceof Byte) {
            key = UtCnv.toInt(key);
        }

        return key;
    }


}
