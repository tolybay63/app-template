package kz.kis.kiscore.utils;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.util.*;

public class UtJson {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Строка вида {"key": "value"} в Map
     */
    public static Map<String, Object> toMap(String jsonString) throws JsonProcessingException {
        if (jsonString == null || jsonString.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Object> map = objectMapper.readValue(jsonString, Map.class);
        return map;
    }

    public static Map<Long, Object> toMapWithLongKeys(String jsonString) throws JsonProcessingException {
        if (jsonString == null || jsonString.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, Object> map = objectMapper.readValue(jsonString, Map.class);
        return map;
    }

    public static String fromMap(Map<String, Object> map) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(map);
        return json;
    }

}
