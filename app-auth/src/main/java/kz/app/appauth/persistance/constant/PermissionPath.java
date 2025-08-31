package kz.app.appauth.persistance.constant;

import jakarta.annotation.*;
import jakarta.servlet.http.*;
import kz.app.appcore.utils.*;

import java.util.*;

public class PermissionPath {

    // Определяет url и параметры к пользовательскому доступу по правам доступа
    // Если url в этом списке нет, то будет выброшено исключение
    // Если возвращает null то проверка прав не нужна
    public static Map getPermissionParams(HttpServletRequest request) {
        // Получаем url к которому стучится клиент
        String url = request.getRequestURI();

        // Узнаем что у него в параметрах
        Map<String, String[]> parameters = request.getParameterMap();

        // Есл параметров нет, то и проверять нечего
        if (Objects.isNull(parameters)) {

            return null;
        }

        if (url.contains("dzi")) {
            return null;
        }

        // Где null подразумевается, что этот запрос не нуждается в проверке по правам доступа
        return switch (url) {

            // auth

            case "/auth/login" -> null;

            case "/auth/logout" -> null;

            case "/auth/signup" -> null;

            case "/auth/getCurrentUser" -> null;

            // grp
            case "/grp/create" -> null;

            // permission
            case "/permission/check" -> null;

            case "/permission/can" -> null;

            case "/permission/set" -> getPermission(PermissionType.ADMIN_MODIFY, parameters);

            case "/permission/listByItem" -> getPermission(PermissionType.ADMIN_READ, parameters);

            // usr
            case "/usr/getId" -> null;

            case "/usr/info" -> null;

            case "/usr/list" -> getPermissionApplication(PermissionType.ADMIN_READ);

            case "/usr/create" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            case "/usr/update" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            case "/usr/disable" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            case "/usr/resetPassword" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);


            // dict
            case "/dict/load" -> null;

            // В случае если URL не известен вызываем Exception
            default -> throw new RuntimeException("Unknown url: " + url);
        };
    }

    private static Map getPermissionApplication(long permissionType) {

        return getPermissionRec(permissionType, getOtherId());
    }

    private static Map getPermission(Long permissionType, Map<String, String[]> parameters) {

        if (parameters.containsKey("file") && parameters.get("file") != null) {
            return getFilesPermission(permissionType, parameters);
        } else if (parameters.containsKey("directory") && parameters.get("directory") != null) {
            return getDirectoryPermission(permissionType, parameters);
        } else if (parameters.containsKey("dir") && parameters.get("dir") != null) {
            return getDirectoryPermission(permissionType, parameters, "dir");
        }

        throw new RuntimeException("Unknown urls params");
    }

    public static Map getFilesPermission(Long permissionType, Map parameters) {

        return getPermissionRec(permissionType, getFieldId(ItemType.FILE, "file", parameters));
    }

    private static Map getFilesPermission(Long permissionType, Map<String, String[]> parameters, String paramName) {

        return getPermissionRec(permissionType, getFieldId(ItemType.FILE, paramName, parameters));
    }

    private static Map getDirectoryPermission(Long permissionType, Map<String, String[]> parameters) {

        return getPermissionRec(permissionType, getFieldId(ItemType.DIRECTORY, "directory", parameters));
    }

    private static Map getDirectoryPermission(Long permissionType, Map<String, String[]> parameters, String paramName) {

        return getPermissionRec(permissionType, getFieldId(ItemType.DIRECTORY, paramName, parameters));
    }

    private static Map getPermission(Map<String, String[]> parameters, Map<String, Object> permissionRules) {

        Map result = new Map();

        Set<String> fields = permissionRules.keySet();

        for (String key : fields) {
            String permissionValues = UtCnv.toString(permissionRules.get(key));

            String[] values = permissionValues.split(":");

            Long itemType = UtCnv.toLong(values[0]);

            Long permissionType = UtCnv.toLong(values[1]);

            String objectId = getFieldId(itemType, key, parameters);

            result.put(objectId, permissionType);
        }

        return result;
    }

    private static Map getPermissionRec(Long permissionType, String objectId) {

        return new Map(UtCnv.toMap("permissionType", permissionType, "objectId", objectId));
    }

    // Возвращает objectId в формате ItemType-ItemId
    // Где ItemType это тип объекта (папка, файл) и ItemId его идентификатор в базе данных
    private static String getFieldId(Long itemType, String paramName, Map parameters) {

        Long objectId = getId(paramName, parameters);

        // Если 0, то значит параметр пуст
        // В таком случае возвращаем null, чтобы не проверять то чего нет
        if (objectId == 0) {
            return null;
        }

        return itemType + "-" + objectId;
    }

    private static String getOtherId() {

        return UtCnv.toString(ItemType.OTHER + "-" + 0);
    }

    public static String getFieldId(@Nullable Long dir, @Nullable Long file) {

        if (Objects.isNull(dir)) {
            return ItemType.FILE + "-" + file;
        } else {
            return ItemType.DIRECTORY + "-" + dir;
        }
    }

    // Возвращает ID файла или директории
    private static Long getId(String paramName, Map<String, Object> parameters) {

        Object param = parameters.get(paramName);

        long paramValue;
        if (param instanceof String[] params) {
            paramValue = UtCnv.toLong(params[0]);
        } else {
            paramValue = UtCnv.toLong(param);
        }

        return UtCnv.toLong(paramValue);
    }

}