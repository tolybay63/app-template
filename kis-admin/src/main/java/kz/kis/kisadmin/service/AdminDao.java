package kz.kis.kisadmin.service;

import kz.kis.kiscore.model.*;
import kz.kis.kisdbtools.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

// dao utils

/**
 *
 */
@Component
public class AdminDao {


    private final Db db;

    public AdminDao(Db db) {
        this.db = db;
    }

    static Map<String, String> dicts = Map.of(
            "DataType", "DataType",
            "Dict", "Dict",
            "DictValue", "DictValue",
            "AttributeType", "AttributeType"
    );

    public String loadA() throws Exception {
        return "Hello";
    }



}
