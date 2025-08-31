package kz.app.appdbtools.repository;

import java.util.*;

public interface DbTyped {


    <T> List<T> loadList(String tableName, Map<String, Object> params, Class<T> clazz) throws Exception;

    <T> List<T> loadSql(String sql, Map<String, Object> params, Class<T> clazz) throws Exception;

    <T> T loadRec(String tableName, Map<String, Object> params, Class<T> clazz) throws Exception;

    <T> T loadRec(String tableName, Map<String, Object> params, boolean recordRequired, Class<T> clazz) throws Exception;

    <T> T loadRec(String tableName, long id, Class<T> clazz) throws Exception;

    <T> T loadRec(String tableName, long id, boolean recordRequired, Class<T> clazz) throws Exception;

    <T> T loadSqlRec(String sql, Map<String, Object> params, Class<T> clazz) throws Exception;

    <T> T loadSqlRec(String sql, Map<String, Object> params, boolean recordRequired, Class<T> clazz) throws Exception;

}
