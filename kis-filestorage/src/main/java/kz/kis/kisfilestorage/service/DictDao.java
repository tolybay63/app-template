package kz.kis.kisfilestorage.service;

import kz.kis.kiscore.model.*;
import kz.kis.kisdbtools.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 *
 */
@Component
public class DictDao {


    private final Db db;

    public DictDao(Db db) {
        this.db = db;
    }

    static Map<String, String> dicts = Map.of(
            "DataType", "DataType",
            "Dict", "Dict",
            "DictValue", "DictValue",
            "AttributeType", "AttributeType"
    );

    public List<DbRec> loadDict(String dictName) throws Exception {
        String tableName = dicts.get(dictName);
        if (tableName == null) {
            throw new RuntimeException("Не найден словарь: " + dictName);
        }

        //
        List<DbRec> res = db.loadSql(sqlLoadDict(tableName), null);

        // Специальные постобработчики
        if (dictName.equals("AttributeType")) {
            for (DbRec rec : res) {
                Object value = rec.get("renderParam");
                rec.remove("renderparam");
                rec.put("renderParam", value);
                //
                value = rec.get("render");
                if (value != null) {
                    String valueString = value.toString();
                    value = Arrays.stream(valueString.split(",")).toList();
                }
                rec.put("render", value);
            }
        }

        //
        return res;
    }

    private String sqlLoadDict(String tableName) {
        return """
                select *
                from %s
                order by id
                """.formatted(tableName);
    }

}
