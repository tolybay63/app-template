package kz.app.appauth.persistance.constant;

import jakarta.annotation.*;
import jakarta.servlet.http.*;
import kz.app.appfile.constants.*;

import java.util.*;

public class PermissionPath {

    // Определяет url и параметры к пользовательскому доступу по правам доступа
    // Если url в этом списке нет, то будет выброшено исключение
    // Если возвращает null то проверка прав не нужна
    public static DbRec getPermissionParams(HttpServletRequest request) {
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
            case "/sync/auth/login" -> null;

            case "/auth/login" -> null;

            case "/auth/agent/login" -> null;

            case "/auth/agent/creds" -> null;

            case "/auth/logout" -> null;

            case "/auth/signup" -> null;

            case "/auth/getCurrentUser" -> null;

            // grp
            case "/grp/create" -> null;

            // permission
            case "/permission/check" -> null;

            case "/permission/can" -> null;

            case "/permission/getPermissionsByUsr" -> getPermission(PermissionType.DIRECTORY_ADMIN, parameters);

            case "/permission/getPermissionsByGrp" -> getPermission(PermissionType.DIRECTORY_ADMIN, parameters);

            case "/permission/getPermissionByParent" -> getPermission(PermissionType.DIRECTORY_ADMIN, parameters);

            case "/permission/deleteAllPermissionsByUsr" -> getPermission(PermissionType.DIRECTORY_ADMIN, parameters);

            case "/permission/deleteAllPermissionsByGrp" -> getPermission(PermissionType.DIRECTORY_ADMIN, parameters);

            case "/permission/set" -> getPermission(PermissionType.DIRECTORY_ADMIN, parameters);

            case "/permission/listByItem" -> getPermission(PermissionType.DIRECTORY_ADMIN, parameters);

            // usr
            case "/usr/getId" -> null;

            case "/usr/info" -> null;

            case "/usr/list" -> getPermissionApplication(PermissionType.ADMIN_READ);

            case "/usr/create" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            case "/usr/update" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            case "/usr/disable" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            case "/usr/sync" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            case "/usr/setOwnUsr" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            case "/usr/resetPassword" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            //
            case "/watcher/retryFile" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            case "/watcher/retryDirectory" -> getPermissionApplication(PermissionType.ADMIN_MODIFY);

            // channel
            case "/channel/generateTelegramLink" -> null;

            case "/channel/list" -> null;

            case "/channel/add" -> null;

            case "/channel/delete" -> null;

            // notification
            case "/notification/list" -> null;

            case "/notification/count" -> null;

            case "/notification/markRead" -> null;

            // subscription
            case "/subscription/add" -> null;

            case "/subscription/delete" -> null;

            case "/subscription/loadRec" -> null;

            case "/subscription/listItems" -> null;

            // attribute
            case "/attribute/list" -> null;

            case "/attribute/create" -> null;

            case "/attribute/update" -> null;

            case "/attribute/delete" -> null;

            // dict
            case "/dict/load" -> null;

            // edit
            case "/edit/list" -> null;

            case "/edit/lock" -> null;

            case "/edit/save" -> null;

            case "/edit/saveRelease" -> null;

            case "/edit/cancel" -> null;

            // dir
            case "/dir/createDirectory" -> getDirectoryPermission(PermissionType.CREATE_FILE, parameters, "parent");

            case "/dir/renameDirectory" -> getDirectoryPermission(PermissionType.EDIT_FILE, parameters, "id");

            case "/dir/renameFile" -> getFilesPermission(PermissionType.EDIT_FILE, parameters, "id");

            case "/dir/uploadFile" -> getDirectoryPermission(PermissionType.CREATE_FILE, parameters, "directory");

            case "/dir/downloadFile" -> getFilesPermission(PermissionType.DOWNLOAD_FILE, parameters, "id");

            case "/dir/moveFile" -> getPermission(parameters, UtCnv.toMap("destinationDirectory", ItemType.DIRECTORY + ":" + PermissionType.CREATE_FILE, "id", ItemType.FILE + ":" + PermissionType.DELETE_FILE));

            case "/dir/copyFile" -> getDirectoryPermission(PermissionType.CREATE_FILE, parameters, "destinationDirectory");

            case "/dir/getFileInfo" -> getFilesPermission(PermissionType.VIEW_FILE, parameters);

            case "/dir/getDirectory" -> getDirectoryPermission(PermissionType.VIEW_FILE, parameters, "dir");

            case "/dir/getDirectoryInfo" -> getDirectoryPermission(PermissionType.VIEW_FILE, parameters, "directories");

            case "/dir/updateFile" -> getFilesPermission(PermissionType.EDIT_FILE, parameters, "id");

            case "/dir/deleteFile" -> getFilesPermission(PermissionType.DELETE_FILE, parameters, "id");

            case "/dir/copyDirectory" -> getPermission(parameters, UtCnv.toMap("destinationDirectory", ItemType.DIRECTORY + ":" + PermissionType.CREATE_FILE, "id", ItemType.DIRECTORY + ":" + PermissionType.DOWNLOAD_FILE));

            case "/dir/moveDirectory" -> getPermission(parameters, UtCnv.toMap("destinationDirectory", ItemType.DIRECTORY + ":" + PermissionType.CREATE_FILE, "id", ItemType.DIRECTORY + ":" + PermissionType.DELETE_FILE));

            case "/dir/deleteDirectory" -> getDirectoryPermission(PermissionType.DELETE_FILE, parameters, "id");

            case "/dir/deleteFilePermanently" -> getFilesPermission(PermissionType.DELETE_FILE, parameters, "id");

            case "/dir/list" -> getDirectoryPermission(PermissionType.LIST_DIRECTORY, parameters);

            case "/dir/listDirs" -> getDirectoryPermission(PermissionType.LIST_DIRECTORY, parameters);

            case "/dir/path" -> getDirectoryPermission(PermissionType.VIEW_FILE, parameters);

            case "/dir/tree" -> getDirectoryPermission(PermissionType.LIST_DIRECTORY, parameters);

            // report
            case "/report/activity" -> null;

            // search
            case "/search/find" -> null;

            // view
            case "/view/filePageInfo" -> getFilesPermission(PermissionType.VIEW_FILE, parameters);

            case "/view/page" -> getFilesPermission(PermissionType.VIEW_FILE, parameters);

            case "/view/preview" -> getFilesPermission(PermissionType.VIEW_FILE, parameters);

            // processing
            case "/processing/loadList" -> null;

            // file-sync
            case "/file-audit/diff/file" -> null;

            case "/file-audit/diff/directory" -> null;

            // В случае если URL не известен вызываем Exception
            default -> throw new RuntimeException("Unknown url: " + url);
        };
    }

    private static DbRec getPermissionApplication(long permissionType) {

        return getPermissionRec(permissionType, getOtherId());
    }

    private static DbRec getPermission(Long permissionType, Map<String, String[]> parameters) {

        if (parameters.containsKey("file") && parameters.get("file") != null) {
            return getFilesPermission(permissionType, parameters);
        } else if (parameters.containsKey("directory") && parameters.get("directory") != null) {
            return getDirectoryPermission(permissionType, parameters);
        } else if (parameters.containsKey("dir") && parameters.get("dir") != null) {
            return getDirectoryPermission(permissionType, parameters, "dir");
        }

        throw new RuntimeException("Unknown urls params");
    }

    public static DbRec getFilesPermission(Long permissionType, Map parameters) {

        return getPermissionRec(permissionType, getFieldId(ItemType.FILE, "file", parameters));
    }

    private static DbRec getFilesPermission(Long permissionType, Map<String, String[]> parameters, String paramName) {

        return getPermissionRec(permissionType, getFieldId(ItemType.FILE, paramName, parameters));
    }

    private static DbRec getDirectoryPermission(Long permissionType, Map<String, String[]> parameters) {

        return getPermissionRec(permissionType, getFieldId(ItemType.DIRECTORY, "directory", parameters));
    }

    private static DbRec getDirectoryPermission(Long permissionType, Map<String, String[]> parameters, String paramName) {

        return getPermissionRec(permissionType, getFieldId(ItemType.DIRECTORY, paramName, parameters));
    }

    private static DbRec getPermission(Map<String, String[]> parameters, Map<String, Object> permissionRules) {

        DbRec result = new DbRec();

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

    private static DbRec getPermissionRec(Long permissionType, String objectId) {

        return new DbRec(UtCnv.toMap("permissionType", permissionType, "objectId", objectId));
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