# AppAuth

Сервис авторизации и аутентификации пользователя в системе SMART CATALOG. Сервис нужен,
чтобы контролировать права доступа к отдельным файлам и каталогам.

Умеет:

- Регистрировать новых пользователей (`/auth/signup`)
- Выполнять аутентификацию пользователей (`/auth/login`)
- Контролирует доступ к endpoint-ам: выкидывает 401 ошибку при отсутствии авторизации.
- Для endpoint-та `/dir/*` проверяет права доступа к файлам и каталогам (см.
  методы `getPermssions*`)

## API /auth/**

При включенном `app-auth` все API, кроме `/auth/**`, заблокированы и требуют авторизации
пользователя.

### Регистрация нового пользователя

Для регистрации нового пользователя необходимо выполнить `POST` запрос к:

`/auth/signup`

С телом запроса в формате JSON:

```json
{
  "username": "user",
  "password": "user",
  "email": "user@org.com"
}
```

Новый пользователь будет записан в таблицу базы данных "Usr"

### Проверка наличия авторизации

После успешной авторизации, заголовок `Authorization` должен появляться в каждом
HTTP-запросе, который требует аутентификации. Без него сервер может отклонить запрос как
неавторизованный.

HTTP-запрос к `/auth/**` аутентификации не требует.

### Авторизация при запросах через браузер

Для логина пользователя необходимо выполнить `POST` запрос к:

`/auth/login`

С телом запроса в формате JSON:

```json
{
  "username": "username",
  "password": "password"
}
```
Если пользователь заблокирован либо не зарегистрирован запрос вернет ошибку 401

### Авторизация пользователя при тестировании

Для тестирования API в Postman (за исключением методов `/auth**`), во
вкладке `Authorization` необходимо выбрать `Basic auth` и указывать логин и пароль для
всех методов.

При тестировании через ```MockMVC``` в заголовках необходимо указать авторизационные данные
в формате Basic auth (```.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Util.encode("mixshttt2:password"))```). 

Пример:
```java
MvcResult result = mockMvc.perform(multipart("/dir/uploadFile")
                        .file(mockFile)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Util.encode("mixshttt2:password"))
                        .param("directoryId", String.valueOf(rootDirectoryId))
                        .param("author", "test-author"))
                .andExpect(status().isOk())
                .andReturn();
```
Аналогично можно в методе @BeforeEach проинициализировать ```mockMvc``` используя ```MockMvcBuilders```
Предварительно нужно проинициализировать ```webApplicationContext``` при помощи аннотации ```@Autowired```.

```java
@Autowired
    private WebApplicationContext webApplicationContext;
```
Пример:
```java
mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Util.encode("mixshttt2:password"))).build();
```

#### Для тестирования ```НЕ http методов``` авторизация не требуется.

### Получить свойства текущего пользователя в Java-коде

Если в вашем коде нужно получить текущего пользователя:

```java
import kz.app.appauth.service.UserService;

UserEntity user = UserService.getCurrentUser();
```
Пример:

```java
import kz.app.appauth.service.UserService;

void findUserEntity() throws Exception {
        UserEntity user = userService.getCurrentUser();
        
        System.out.println(user.getUsername());
}
```

Либо

```java
UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
```