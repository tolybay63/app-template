package kz.kis.kisauth.persistance.constant;

import java.util.*;

public class PermissionType {

    // Для файлов/каталогов

    // Просмотр каталога (задаётся только для папки, для файла не имеет значения)
    public static long LIST_DIRECTORY = 1;

    // Просмотр содержимого файла
    public static long VIEW_FILE = 2;

    // Создание файлов и папок
    public static long CREATE_FILE = 3;

    // Изменение аттрибутов (а также имени) файла/каталога
    public static long EDIT_FILE_ATTR = 4;

    // Скачивание файлов
    public static long DOWNLOAD_FILE = 5;

    // Редактирование файлов (взятие файла в работу и загрузка новой версии)
    public static long EDIT_FILE = 6;

    // Удаление файлов и папок
    public static long DELETE_FILE = 7;

    // Администрирование (право раздавать права)
    public static long DIRECTORY_ADMIN = 8;


    // Для пользователя

    // Просто пользователь, прошедший авторизацию
    public static long USER = 100;

    // Для администрирования
    public static long ADMIN_READ = 800;
    public static long ADMIN_MODIFY = 900;


    // Права установлены по возрастанию
    public static Set<Long> getPermissionTypes() {
        Set<Long> permissionTypes = new LinkedHashSet<>();
        permissionTypes.add(LIST_DIRECTORY);
        permissionTypes.add(VIEW_FILE);
        permissionTypes.add(CREATE_FILE);
        permissionTypes.add(EDIT_FILE);
        permissionTypes.add(DELETE_FILE);
        permissionTypes.add(DIRECTORY_ADMIN);
        permissionTypes.add(EDIT_FILE_ATTR);
        permissionTypes.add(DOWNLOAD_FILE);
        permissionTypes.add(ADMIN_READ);
        permissionTypes.add(ADMIN_MODIFY);

        return permissionTypes;
    }

    public static Map<Long, Set<Long>> getDependentPermissions() {
        Map<Long, Set<Long>> dependentPermissions = new LinkedHashMap<>();

        Set<Long> permissionTypes = getPermissionTypes();

        for (Long permissionType : permissionTypes) {
            Set<Long> perms = new LinkedHashSet<>();

            if (permissionType == VIEW_FILE) {
                perms.add(LIST_DIRECTORY);
            } else if (permissionType == CREATE_FILE) {
                perms.add(VIEW_FILE);
                perms.add(LIST_DIRECTORY);
                perms.add(DOWNLOAD_FILE);
            } else if (permissionType == EDIT_FILE) {
                perms.add(VIEW_FILE);
                perms.add(CREATE_FILE);
                perms.add(LIST_DIRECTORY);
                perms.add(DOWNLOAD_FILE);
            } else if (permissionType == DOWNLOAD_FILE) {
                perms.add(VIEW_FILE);
                perms.add(LIST_DIRECTORY);
            } else if (permissionType == DELETE_FILE) {
                perms.add(VIEW_FILE);
                perms.add(LIST_DIRECTORY);
                perms.add(CREATE_FILE);
                perms.add(EDIT_FILE);
                perms.add(DOWNLOAD_FILE);
            } else if (permissionType == DIRECTORY_ADMIN) {
                perms.add(DELETE_FILE);
                perms.add(EDIT_FILE);
                perms.add(CREATE_FILE);
                perms.add(EDIT_FILE_ATTR);
                perms.add(DOWNLOAD_FILE);
                perms.add(VIEW_FILE);
                perms.add(LIST_DIRECTORY);
            } else if (permissionType == ADMIN_MODIFY) {
                perms.add(ADMIN_READ);
            }

            dependentPermissions.put(permissionType, perms);
        }

        return dependentPermissions;
    }
}