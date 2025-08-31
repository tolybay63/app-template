package kz.app.appdbtools.repository;

import kz.app.appcore.model.*;

import java.util.*;

public interface Db {

    void execSql(String sql, Map<String, Object> params) throws Exception;

    long insertRec(String tableName, Map<String, Object> values) throws Exception;

    void updateRec(String tableName, Map<String, Object> values) throws Exception;

    void updateRec(String tableName, Map<String, Object> values, Map<String, Object> params) throws Exception;

    void deleteRec(String tableName, Map<String, Object> params) throws Exception;

    void deleteRec(String tableName, long id) throws Exception;

    List<DbRec> loadList(String tableName, Map<String, Object> params) throws Exception;

    List<DbRec> loadSql(String sql, Map<String, Object> params) throws Exception;

    DbRec loadRec(String tableName, Map<String, Object> params) throws Exception;

    DbRec loadRec(String tableName, Map<String, Object> params, boolean recordRequired) throws Exception;

    DbRec loadRec(String tableName, long id) throws Exception;

    DbRec loadRec(String tableName, long id, boolean recordRequired) throws Exception;

    DbRec loadSqlRec(String sql, Map<String, Object> params) throws Exception;

    DbRec loadSqlRec(String sql, Map<String, Object> params, boolean recordRequired) throws Exception;

    Cursor openSql(String sql, Map<String, Object> params) throws Exception;

}
