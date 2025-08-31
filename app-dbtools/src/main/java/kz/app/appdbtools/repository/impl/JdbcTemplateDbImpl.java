package kz.app.appdbtools.repository.impl;

public class JdbcTemplateDbImpl /*implements Db*/ {

/*
    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateDbImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcTemplateDbImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Track
    @Override
    public void execSql(String sql, Map params) throws Exception {
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Track
    @Override
    public long insertRec(String tableName, Map values) throws Exception {
        String sql = buildInsertQuery(tableName, values);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(values), keyHolder, new String[]{"id"});
        return keyHolder.getKey().longValue();
    }

    @Track
    @Override
    public void updateRec(String tableName, Map values) throws Exception {
        String sql = buildUpdateQuery(tableName, values);
        logger.debug("Executing SQL: {} with params: {}", sql, values);
        namedParameterJdbcTemplate.update(sql, values);
    }

    @Track
    @Override
    public void updateRec(String tableName, Map values, Map params) throws Exception {
        String sql = buildUpdateQuery(tableName, values, params);
        params.putAll(values);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Track
    @Override
    public void deleteRec(String tableName, Map params) throws Exception {
        String sql = buildDeleteQuery(tableName, params);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Track
    @Override
    public void deleteRec(String tableName, long id) throws Exception {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Track
    @Override
    public List<Rec> loadList(String tableName, Map params) {
        String sql = buildSelectQuery(tableName, params);
        return namedParameterJdbcTemplate.queryForList(sql, params);
    }

    @Override
    public Map<String, Object> loadRec(String tableName, Map params) throws Exception {
        return loadRec(tableName, params, true);
    }

    @Track
    @Override
    public Map<String, Object> loadRec(String tableName, Map params, boolean recordRequired) throws Exception {
        List<Rec> res = loadList(tableName, params);
        return returnRec(res, recordRequired);
    }

    @Override
    public Map<String, Object> loadRec(String tableName, long id, boolean recordRequired) throws Exception {
        Map params = UtCnv.toMap("id", id);
        return loadRec(tableName, params, recordRequired);
    }

    @Override
    public Map<String, Object> loadRec(String tableName, long id) throws Exception {
        return loadRec(tableName, id, true);
    }

    @Track
    @Override
    public List<Rec> loadSql(String sql, Map params) throws Exception {
        return namedParameterJdbcTemplate.queryForList(sql, params);
    }

    @Override
    public Map<String, Object> loadSqlRec(String sql, Map params) throws Exception {
        return loadSqlRec(sql, params, true);
    }

    @Track
    @Override
    public Map<String, Object> loadSqlRec(String sql, Map params, boolean recordRequired) throws Exception {
        List<Rec> res = namedParameterJdbcTemplate.queryForList(sql, params);
        return returnRec(res, recordRequired);
    }

    @Override
    public Cursor openSql(String sql, Map params) throws Exception {
        return null;
    }

    Map<String, Object> returnRec(List<Rec> res, boolean recordRequired) throws Exception {
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

    private String buildInsertQuery(String tableName, Map<String, Object> params) throws Exception {
        StringJoiner columns = new StringJoiner(", ", "(", ")");
        StringJoiner values = new StringJoiner(", ", "(", ")");

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            columns.add(key);

            // Если появляется параметр в виде мапы, то парсим его в строку формата json
            // и добавляем ::jsonb для преобразования внутри запроса
            if (value instanceof Map<?, ?>) {
                Map<String, Object> mapValue = (Map<String, Object>) value;
                params.put(key, UtJson.fromMap(mapValue));
                values.add(":" + key + "::jsonb");
            } else {
                values.add(":" + key);
            }
        }

        return String.format("INSERT INTO %s %s VALUES %s", tableName, columns, values);
    }

    private String buildUpdateQuery(String tableName, Map<String, Object> params) {
        StringJoiner setClause = new StringJoiner(", ");
        Long id = null;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("id")) {
                id = UtCnv.toLong(entry.getValue());
            } else {
                setClause.add(entry.getKey() + " = :" + entry.getKey());
            }
        }

        if (id == null) {
            throw new IllegalArgumentException("ID is required for update.");
        }

        return String.format("UPDATE %s SET %s WHERE id = :id", tableName, setClause);
    }

    private String buildSelectQuery(String tableName, Map<String, Object> params) {
        StringBuilder sqlBuilder = new StringBuilder();

        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) == null) {
                    if (sqlBuilder.isEmpty())
                        sqlBuilder.append(" WHERE " + key + " IS NULL ");
                    else
                        sqlBuilder.append("and " + key + " IS NULL ");
                } else if (params.get(key) instanceof Collection<?>) {
                    if (sqlBuilder.isEmpty())
                        sqlBuilder.append(" WHERE " + key + " IN (:" + key + ") ");
                    else
                        sqlBuilder.append("and " + key + " IN (:" + key + ") ");
                } else {
                    if (sqlBuilder.isEmpty())
                        sqlBuilder.append(" WHERE " + key + " = :" + key + " ");
                    else
                        sqlBuilder.append("and " + key + " = :" + key + " ");
                }
            }
        }

        System.out.println("SELECT * FROM " + tableName + sqlBuilder);

        return "SELECT * FROM " + tableName + sqlBuilder;
    }

    private String buildUpdateQuery(String tableName, Map<String, Object> values, Map<String, Object> params) {
        StringJoiner setClause = new StringJoiner(", ");
        StringJoiner paramsClause = new StringJoiner(", ");

        if (params.size() == 0) {
            throw new IllegalArgumentException("params is required for update.");
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            paramsClause.add(entry.getKey() + " = :" + entry.getKey());
        }

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            setClause.add(entry.getKey() + " = :" + entry.getKey());
        }


        return String.format("UPDATE %s SET %s WHERE %s", tableName, setClause, paramsClause);
    }

    private String buildDeleteQuery(String tableName, Map<String, Object> params) {
        StringJoiner whereClause = new StringJoiner(" AND ");

        for (String key : params.keySet()) {
            if (params.get(key) == null) {
                whereClause.add(key + " is null");
            } else {
                whereClause.add(key + " = :" + key);
            }
        }

        return "DELETE FROM " + tableName + " WHERE " + whereClause.toString();
    }
*/

}
