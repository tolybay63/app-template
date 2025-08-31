package kz.app.appdbtools.repository.impl;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import kz.app.appcore.model.*;
import kz.app.appcore.utils.*;
import kz.app.appdbtools.repository.*;
import kz.app.appdbtools.repository.*;
import org.postgresql.util.*;
import org.slf4j.*;

import javax.sql.*;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.regex.*;

public class JdbcDbImpl implements Db {

    private static final Logger log = LoggerFactory.getLogger(JdbcDbImpl.class);

    private final DataSource dataSource;
    private final SqlParamInterceptor interceptor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern ALIAS_PATTERN = Pattern.compile("(?i)\\bAS\\s+([a-zA-Z0-9_]+)");
    private static final Pattern DOT_PATTERN = Pattern.compile("(?i)\\.([a-zA-Z0-9_]+)");

    public JdbcDbImpl(DataSource dataSource, SqlParamInterceptor interceptor) {
        this.dataSource = dataSource;
        this.interceptor = interceptor;
    }

    protected void bindParams(Connection conn, PreparedStatement stmt, String sql, Map<String, Object> params) throws Exception {
        if (params == null || params.isEmpty()) {
            return;
        }

        //
        Map<String, Object> finalParams = interceptor.modifyParams(sql, params);
        List<String> keys = extractNamedParameters(sql);

        //
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = finalParams.get(key);

            //
            log.info("  " + key + "=" + value);

            // Преобразования форматов и типов
            if (value instanceof Map) {
                PGobject jsonObj = new PGobject();
                jsonObj.setType("jsonb");
                jsonObj.setValue(objectMapper.writeValueAsString(value));
                stmt.setObject(i + 1, jsonObj);

            } else if (value instanceof Collection valueCollection) {
                Array valueArray = conn.createArrayOf("BIGINT", valueCollection.toArray());
                stmt.setArray(i + 1, valueArray);

            } else if (value instanceof Date dateValue) {
                stmt.setObject(i + 1, new java.sql.Timestamp(dateValue.getTime()), Types.TIMESTAMP);

            } else {
                stmt.setObject(i + 1, value);
            }
        }
    }

    protected List<DbRec> mapResultSet(String sql, ResultSet rs) throws SQLException, JsonProcessingException {
        List<DbRec> result = new ArrayList<>();

        // Если в запросе будут строка типа "...field as fieldName",
        // то вместо fieldname (в нижнем регистре) в DbRec попадёт fieldName (как в алиасе)
        Map<String, String> aliasMap = new LinkedHashMap<>();
        Matcher matcher = ALIAS_PATTERN.matcher(sql);
        while (matcher.find()) {
            String alias = matcher.group(1);
            aliasMap.put(alias.toLowerCase(), alias);
        }
        // Если в запросе будут строка типа "table.fieldName" или (в качестве хака) в комментариях "-- .fieldName",
        // то вместо fieldname (в нижнем регистре) в DbRec попадёт fieldName (как в запросе)
        // todo подгрузку структуры БД и передачу её драйверу, чтобы не надо было хаков (см. issue #215)
        matcher = DOT_PATTERN.matcher(sql);
        while (matcher.find()) {
            String alias = matcher.group(1);
            String aliasLowerCase = alias.toLowerCase();
            if (!aliasLowerCase.equals(alias)) {
                aliasMap.put(alias.toLowerCase(), alias);
            }
        }

        //
        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        //
        while (rs.next()) {
            DbRec row = new DbRec();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                String fieldName = resultSetMetaData.getColumnLabel(i);
                String mappedFieldName = aliasMap.getOrDefault(fieldName, fieldName);

                //
                Object value = rs.getObject(i);

                // Преобразования форматов и типов
                if (value instanceof PGobject valuePGobject) {
                    // Любой PGobject считаем jsonb и превращаем в Map
                    value = UtJson.toMap(valuePGobject.toString());
                }

                //
                row.put(mappedFieldName, value);
            }
            result.add(row);
        }

        return result;
    }

    private List<String> extractNamedParameters(String sql) {
        List<String> params = new ArrayList<>();
        int j = 0;
        while (true) {
            int i = sql.indexOf(":", j);

            //
            if (i == -1) {
                break;
            }

            // Для Postgres в тексте встречаются два двоеточия подряд, например
            // Directory.name::TEXT
            // или
            // VersionAttribute.value::BIGINT
            // В таких случаях - текущее двоеточие не признак параметра - пропускаем
            if (sql.charAt(i + 1) == ':') {
                j = i + 2;
                continue;
            }

            // Выбираем символы идентификатора
            j = i + 1;
            while (j < sql.length() && Character.isJavaIdentifierPart(sql.charAt(j))) {
                j++;
            }

            //
            String key = sql.substring(i + 1, j);
            params.add(key);
        }
        return params;
    }

    private PreparedStatement prepareNamedStatement(Connection conn, String sql, Map<String, Object> params) throws Exception {
        sql = replaceText(sql, params);

        List<String> keys = extractNamedParameters(sql);
        String newSql = sql;
        for (String key : keys) {
            newSql = newSql.replaceFirst(":" + key, "?");
        }

        log.debug("sql statement:");
        log.debug(newSql);

        PreparedStatement stmt = conn.prepareStatement(newSql);

        bindParams(conn, stmt, sql, params);

        return stmt;
    }

    private String replaceText(String sql, Map<String, Object> params) throws Exception {
        log.info("sql raw:");
        log.info(sql);

        //
        String sqlRes = sql;

        //
        int i = sqlRes.indexOf(":{");
        while (i != -1) {
            int j = sqlRes.indexOf("}", i);

            if (j == -1) {
                throw new Exception("not found match '}' at pos: " + i);
            }

            String key = sqlRes.substring(i + 2, j);
            String valueStr = getParamValueNoSqlInject(key, params);
            sqlRes = sqlRes.replace(":{" + key + "}", valueStr);

            i = sqlRes.indexOf(":{", j);
        }

        //
        if (!sqlRes.equals(sql)) {
            log.info("sql replaced:");
            log.info(sqlRes);
        }

        //
        return sqlRes;
    }

    private String getParamValueNoSqlInject(String key, Map<String, Object> params) throws Exception {
        Object value = params.get(key);

        if (value == null) {
            return "";

        } else if (value instanceof Collection valueCollection) {
            List<Long> valueListLong = UtCnv.toListLong(valueCollection);
            return UtString.join(valueListLong, ",");

        } else if (value instanceof String valueString) {
            List<Long> valueListLong = UtCnv.toListLong(valueString.split(","));
            return UtString.join(valueListLong, ",");

        } else if (value instanceof Number valueNumber) {
            return UtCnv.toString(valueNumber);

        } else {
            throw new Exception("not valid parameter type, parameter: " + key + ", value: " + value);
        }
    }

    @Override
    public void execSql(String sql, Map<String, Object> params) throws Exception {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = prepareNamedStatement(conn, sql, params)) {
            stmt.executeUpdate();
        }
    }

    @Override
    public long insertRec(String tableName, Map<String, Object> values) throws Exception {
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        for (Object key : values.keySet()) {
            columns.add(key.toString());
            placeholders.add(":" + key);
        }
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ") RETURNING id";

        //
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = prepareNamedStatement(conn, sql, values);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("No ID returned on insert.");
        }
    }

    @Override
    public void updateRec(String tableName, Map<String, Object> values) throws Exception {
        if (!values.containsKey("id")) throw new IllegalArgumentException("Missing 'id'");
        updateRec(tableName, values, Map.of("id", values.get("id")));
    }

    @Override
    public void updateRec(String tableName, Map<String, Object> values, Map<String, Object> params) throws Exception {
        StringJoiner updates = new StringJoiner(", ");
        for (Object key : values.keySet()) {
            updates.add(key + " = :" + key);
        }
        String sql = "UPDATE " + tableName + " SET " + updates + " WHERE ";
        StringJoiner where = new StringJoiner(" AND ");
        for (Object key : params.keySet()) {
            where.add(key + " = :" + key);
        }
        sql += where;

        Map<String, Object> allParams = new HashMap<>(values);
        allParams.putAll(params);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = prepareNamedStatement(conn, sql, allParams)) {
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteRec(String tableName, Map<String, Object> params) throws Exception {
        StringJoiner where = new StringJoiner(" AND ");
        for (Object key : params.keySet()) {
            where.add(key + " = :" + key);
        }
        String sql = "DELETE FROM " + tableName + " WHERE " + where;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = prepareNamedStatement(conn, sql, params)) {
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteRec(String tableName, long id) throws Exception {
        deleteRec(tableName, Map.of("id", id));
    }

    @Override
    public List<DbRec> loadList(String tableName, Map<String, Object> params) throws Exception {
        String sql = buildSelectQuery(tableName, params);
        return loadSql(sql, params);
    }

    @Override
    public List<DbRec> loadSql(String sql, Map<String, Object> params) throws Exception {
        log.info("~~~");
        QueryLogger queryLogger = new QueryLogger();
        queryLogger.logStart();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = prepareNamedStatement(conn, sql, params);
             ResultSet rs = stmt.executeQuery()) {

            List<DbRec> res = mapResultSet(sql, rs);

            queryLogger.logStop();
            log.info("res.size: " + res.size());
            log.info("sql duration: " + queryLogger.durationMsec());
            log.info("~~~ end");

            return res;

        } catch (Exception e) {
            log.error("exec.error: " + e.getMessage());
            log.info("~~~ end");
            throw e;
        }
    }

    @Override
    public DbRec loadRec(String tableName, Map<String, Object> params) throws Exception {
        return loadRec(tableName, params, false);
    }

    @Override
    public DbRec loadRec(String tableName, Map<String, Object> params, boolean recordRequired) throws Exception {
        List<DbRec> list = loadList(tableName, params);
        return returnRec(list, recordRequired);
    }

    @Override
    public DbRec loadRec(String tableName, long id) throws Exception {
        return loadRec(tableName, id, false);
    }

    @Override
    public DbRec loadRec(String tableName, long id, boolean recordRequired) throws Exception {
        return loadRec(tableName, Map.of("id", id), recordRequired);
    }

    @Override
    public DbRec loadSqlRec(String sql, Map<String, Object> params) throws Exception {
        return loadSqlRec(sql, params, false);
    }

    @Override
    public DbRec loadSqlRec(String sql, Map<String, Object> params, boolean recordRequired) throws Exception {
        List<DbRec> list = loadSql(sql, params);
        return returnRec(list, recordRequired);
    }

    @Override
    public Cursor openSql(String sql, Map<String, Object> params) throws Exception {
        throw new UnsupportedOperationException("Cursor support not implemented");
    }

    private String buildSelectQuery(String tableName, Map<String, Object> params) {
        StringBuilder paramsString = new StringBuilder();

        if (params != null) {
            for (String key : params.keySet()) {
                if (paramsString.isEmpty())
                    paramsString.append(" WHERE ");
                else
                    paramsString.append(" AND ");

                //
                if (params.get(key) == null) {
                    paramsString.append(key + " IS NULL");

                } else if (params.get(key) instanceof Collection<?>) {
                    paramsString.append(key + " = ANY (:" + key + ")");

                } else {
                    paramsString.append(key + " = :" + key);
                }
            }
        }

        return "SELECT * FROM " + tableName + paramsString;
    }

    DbRec returnRec(List<DbRec> res, boolean recordRequired) throws Exception {
        if (res.size() > 1) {
            throw new Exception("Record count > 1");
        }

        if (res.size() == 0) {
            if (recordRequired) {
                throw new Exception("Record not found");
            } else {
                return null;
            }
        } else {
            return res.get(0);
        }
    }


}
