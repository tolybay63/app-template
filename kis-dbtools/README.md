# Модуль kis-dbtools

`kis-dbtools` — это утилита для работы с базой данных, использующая `JdbcTemplate` и `NamedParameterJdbcTemplate` из Spring JDBC. Модуль предоставляет базовые функции для выполнения операций вставки, обновления, удаления и загрузки данных из базы данных. Этот модуль предназначен для использования в других модулях вашего проекта и не является самостоятельным Spring-приложением.

## Возможности

- Вставка записей в базу данных с автоинкрементным ключом.
- Обновление существующих записей.
- Удаление записей по идентификатору или другим параметрам.
- Загрузка данных с использованием SQL-запросов с параметрами.
- Простой и гибкий интерфейс для работы с базой данных без необходимости прямого написания SQL-кода.

## Установка

1. Добавьте `kis-dbtools` в зависимости вашего проекта. В `build.gradle` вашего модуля укажите:

```groovy
dependencies {
    implementation project(':kis-dbtools')
}
```
2. Убедитесь, что в вашем проекте настроен DataSource, так как JdbcTemplate и NamedParameterJdbcTemplate зависят от него.

   Пример настройки DataSource:
```java
@Configuration
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        // Настройте и верните DataSource
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public Db db(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new JdbcTemplateDbImpl(jdbcTemplate, namedParameterJdbcTemplate);
    }
}
```
## Использование
После настройки зависимости kis-dbtools в вашем модуле, вы можете использовать предоставляемые методы для взаимодействия с базой данных.

Пример использования в сервисе:
```java
@Service
public class FileService {

    private final Db db;

    public FileService(Db db) {
        this.db = db;
    }

    public long addFileRecord(String fileName, long size) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("fileName", fileName);
        params.put("size", size);
        return db.insertRec("File", params);
    }

    public void updateFileRecord(long id, String newFileName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("fileName", newFileName);
        db.updateRec("File", params);
    }

    public List<Rec> listFiles() throws Exception {
        String sql = "SELECT * FROM File";
        return db.loadSql(sql, new HashMap<>());
    }
}
```
## Методы
- insertRec(String tableName, Map params) — Вставка записи в таблицу. Возвращает идентификатор вставленной записи.
- updateRec(String tableName, Map params) — Обновление записи в таблице по ID.
- deleteRec(String tableName, Map params) — Удаление записи по параметрам.
- deleteRec(String tableName, long id) — Удаление записи по ID.
- loadSql(String sql, Map params) — Выполнение SQL-запроса и получение списка результатов.

## Тестирование
Вы можете использовать JUnit и Spring Test для тестирования вашего модуля. Например:
```java
@SpringBootTest
public class DbTests {

    @Autowired
    private Db db;

    @Test
    public void testInsert() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Test File");
        long id = db.insertRec("File", params);
        assertTrue(id > 0);
    }
}
```