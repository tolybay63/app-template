package kz.app.appmain.service;

import kz.app.appcore.model.*;
import kz.app.appdbtools.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

// dao utils

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

        //
        return res;
    }

    private String sqlLoadDict(String tableName) {
        return """
                select *
                from DictValue
                order by id
                """;
    }

}
