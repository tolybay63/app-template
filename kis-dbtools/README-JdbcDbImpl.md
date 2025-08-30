# JdbcDbImpl: Простая реализация Db-интерфейса через JDBC

`JdbcDbImpl` — это низкоуровневая реализация интерфейса `Db` с использованием чистого JDBC (без JdbcTemplate). Она предназначена для выполнения SQL-запросов с параметрами и маппинга результатов в `Map<String, Object>`.

## ️Конструктор

```java
JdbcDbImpl(DataSource dataSource, SqlParamInterceptor paramInterceptor, SqlResultMapper resultMapper)
```

- `dataSource` — источник соединений JDBC.
- `paramInterceptor` — перехватчик параметров перед выполнением SQL.
- `resultMapper` — преобразователь строк результата (`ResultSet`) в `Map` или объект.

---

##  Пример использования

```java
Db db = new JdbcDbImpl(
    dataSource,
    new LoggingParamInterceptor(),
    new DefaultResultMapper()
);

long id = db.insertRec("users", Map.of("name", "Ivan", "age", 33));
Map<String, Object> user = db.loadRec("users", id);
```

---

##  SqlParamInterceptor — перехват параметров

Интерфейс:
```java
public interface SqlParamInterceptor {
    Map<String, Object> modifyParams(String sql, Map<String, Object> originalParams);
}
```

###  Применение:
- Логирование параметров
- Маскирование конфиденциальных данных (например, "password")
- Динамическое добавление или удаление параметров

### Пример реализации: `LoggingParamInterceptor`
```java
public class LoggingParamInterceptor implements SqlParamInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingParamInterceptor.class);

    @Override
    public Map<String, Object> modifyParams(String sql, Map<String, Object> originalParams) {
        logger.debug("SQL: {}", sql);
        logger.debug("Params: {}", originalParams);
        return originalParams;
    }
}
```

###  Пример использования
```java
Db db = new JdbcDbImpl(dataSource, new LoggingParamInterceptor(), new DefaultResultMapper());

db.insertRec("employees", Map.of("name", "Pavel", "salary", 50000));
```
Вывод в логи:
```
SQL: INSERT INTO employees (name, salary) VALUES (?, ?)
Params: {name=Pavel, salary=50000}
```

---

## SqlResultMapper — маппинг результата

Интерфейс:
```java
public interface SqlResultMapper {
    Map<String, Object> mapRow(ResultSet rs) throws SQLException;
}
```

### Применение:
- Преобразование `ResultSet` в `Map`, DTO, JSON или другой объект
- Пропуск лишних колонок
- Кастомный тип маппинга (например, enum, LocalDate и т.д.)

###  Пример реализации: `DefaultResultMapper`
```java
public class DefaultResultMapper implements SqlResultMapper {
    @Override
    public Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<>();
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            row.put(meta.getColumnLabel(i), rs.getObject(i));
        }
        return row;
    }
}
```

### Пример использования
```java
Db db = new JdbcDbImpl(dataSource, new LoggingParamInterceptor(), new DefaultResultMapper());

List<Rec> users = db.loadList("users", Map.of("active", true));
for (Map<String, Object> user : users) {
    System.out.println(user);
}
```

Вывод:
```
{name=Ivan, age=33, active=true}
{name=Elena, age=28, active=true}
```

---

## Альтернативные реализации SqlResultMapper

###  Маппинг в DTO
```java
public class UserDtoMapper implements SqlResultMapper {
    @Override
    public Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        return Map.of(
            "user", new UserDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("age")
            )
        );
    }
}
```

###  Фильтрация колонок
```java
public class SelectiveResultMapper implements SqlResultMapper {
    @Override
    public Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", rs.getObject("id"));
        row.put("name", rs.getObject("name"));
        return row;
    }
}
```

---

##  Поддерживаемые методы JdbcDbImpl

- `insertRec(table, values)`
- `updateRec(table, values)` / `updateRec(table, values, where)`
- `deleteRec(table, id)` / `deleteRec(table, where)`
- `loadRec(...)` / `loadList(...)`
- `loadSql(...)`, `loadSqlRec(...)`
- `execSql(...)`

---

##  Юнит тест
```java
@Test
void testInsertAndLoad() throws Exception {
    long id = db.insertRec("users", Map.of("name", "Olga", "age", 29));
    Map<String, Object> rec = db.loadRec("users", id);
    assertEquals("Olga", rec.get("name"));
}
```

---

Готово к использованию в проде, тестах и CLI-утилитах с минимальными зависимостями.
