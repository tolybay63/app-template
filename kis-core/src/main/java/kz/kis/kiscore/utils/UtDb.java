package kz.kis.kiscore.utils;

import kz.kis.kiscore.model.*;

import java.util.*;

public class UtDb {

    public static <T> Set<T> uniqueValues(List<DbRec> recs, String field) {
        Set<T> res = new HashSet<>();

        if (recs == null) {
            return res;
        }

        //
        for (Map<String, Object> rec : recs) {
            res.add((T) rec.get(field));
        }

        return res;
    }

    public static Map<Object, Object> uniquePairs(List<DbRec> recs, String keyField, String valueField) {
        Map<Object, Object> res = new HashMap<>();

        for (Map<String, Object> rec : recs) {
            res.put(rec.get(keyField), rec.get(valueField));
        }

        return res;
    }

    public static void outTable(List<DbRec> res) {
        outTable(res, 0);
    }

    public static void outTable(List<DbRec> res, int limit) {
        UtOutTable ut = new UtOutTable(res);
        StringBuilder sb = new StringBuilder();
        ut.saveTo(sb, limit);
        System.out.println(sb);
    }

    public static void outRecord(Map<String, Object> rec) {
        System.out.println(rec.toString());
    }

    /**
     * Для каждого значения ключевого поля keyField собирает из store список записей.
     *
     * @param store    источник значений
     * @param keyField ключевое поле для группировки
     * @return Map<значение ключевого поля, список с собранными записями>
     */
    public static Map<Object, List<DbRec>> collectGroupBy_records(List<DbRec> store, String keyField) {
        Map<Object, List<DbRec>> res = new MapNumber();

        for (DbRec rec : store) {
            Object keyValue = rec.get(keyField);

            //
            List<DbRec> listRecords = res.get(keyValue);

            //
            if (listRecords == null) {
                listRecords = new ArrayList<>();
                res.put(keyValue, listRecords);
            }

            //
            listRecords.add(rec);
        }

        return res;
    }


}
