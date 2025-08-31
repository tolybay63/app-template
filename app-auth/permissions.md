## Разграничение доступа

### Permissions (разрешения)

Фиксированный список разрешений, можно проверять только их.

- вход а систему
- администрирование
- другие, добавляете сами

### Пользователи и группы

Существуют пользователи и их группы, каждый пользователь входит в несколько групп.

### Права

Право есть связка: `permission`:`user|userGroup`:`forbid|permit`

Права назначаются для группы или для конкретного пользователя.

Права могут быть назначены как с разрешением, так и с запретом `forbid` или `permit`. Запрещающее правило имеет
приоритет над разрешающим.

### Проверка прав

При вызове соответствующих endpoint-ов (напр. `createDirectory`, `uploadFile`,
`downloadFile`, `listFiles`) должны проверяться соответствующие права.

### Метод kz.app.appauth.persistance.constant.PermissionPath.getPermissionParams(HttpServletRequest request)

Получает из Http запроса данные по URL и его параметры. По параметрам определяет по какой папке или файлу проверять
права и какие именно права проверять. Если url отсутствует в списке, то будет выброшена ошибка

```java
    throw new RuntimeException("Unknown url: "+url)
```

Во избежания этой ошибки при вызове вашей API необходимо список добавить следующую запись:

Если мы знаем что надо проверять права на файл:

```java
    case"/api/save/file"->getFilesPermission(PermissionType.EDIT_FILE,parameters);
```

Если мы знаем что надо проверять права на папку:

```java
    case"/api/save/directory"->getDirectoryPermission(PermissionType.EDIT_FILE,parameters);
```

Если проверять права не надо

```java
    case"/api/sayHelloMrAnderson"->null;
```
