# app-annotations

Модуль с разработанными аннотациями для проекта "Smart Catalog"

### Аннотация @Track

Предназначена для логирования вызова помеченных методов. Выводит в лог сведения о классе в котором находится метод, имя
отслеживаемого метода, принятых им аргументов, время начала работы, время завершения работы и длительность выполнения
метода.

Использование:

```java
@Track
@PostMapping("/login")
public ResponseEntity<Map> login(
        HttpServletRequest request,
        HttpServletResponse response
        )throws Exception{

```

Результат в log

````
08:53:22 [INFO ] track - c31dd48265f84bd6 stack start
08:53:22 [INFO ] track - c31dd48265f84bd6 kz.app.appauth.controller.AuthController.login args HttpServletRequest body: {"username":"admin","password":"admin"}; LifecycleHttpServletResponse;  started at 2025-03-20T08:53:22.472433
08:53:22 [INFO ] track - c31dd48265f84bd6 JdbcTemplateDbImpl.loadList args String: "Usr"; Map: {"name":"admin"};  started at 2025-03-20T08:53:22.842121
08:53:22 [INFO ] track - c31dd48265f84bd6 JdbcTemplateDbImpl.loadList finished at 2025-03-20T08:53:22.861529 in 19 ms
08:53:22 [INFO ] track - c31dd48265f84bd6 JdbcTemplateDbImpl.loadList args String: "Usr"; Map: {"name":"admin"};  started at 2025-03-20T08:53:22.867178
08:53:22 [INFO ] track - c31dd48265f84bd6 JdbcTemplateDbImpl.loadList finished at 2025-03-20T08:53:22.918418 in 51 ms
08:53:22 [INFO ] track - c31dd48265f84bd6 kz.app.appauth.controller.AuthController.login finished at 2025-03-20T08:53:22.918749 in 446 ms
08:53:22 [INFO ] track - c31dd48265f84bd6 stack end
````

Поддерживается вызовы вложенных методов, при этом обеспечивается сплошная трассировка от главного вызывающего метода. 