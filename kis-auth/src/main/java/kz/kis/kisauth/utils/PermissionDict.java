package kz.kis.kisauth.utils;

import kz.kis.kisauth.manager.*;
import kz.kis.kiscore.utils.*;
import kz.kis.kisfile.service.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

import static kz.kis.kisauth.persistance.constant.PermissionType.*;
import static kz.kis.kisfile.constants.ItemType.*;

@AllArgsConstructor
@Component
public class PermissionDict {

    private final FileStorageDao fileStorageDao;

    static Map<Long, String> itemTypeRu = Map.of(
            FILE, "файла",
            DIRECTORY, "папки",
            OTHER, "системной функции"
    );

    public static Map<Long, String> permissionTypeRu = Map.of(
            LIST_DIRECTORY, "Просмотр папки",
            VIEW_FILE, "Просмотр содержимого файла",
            CREATE_FILE, "Создание файлов и папок",
            EDIT_FILE_ATTR, "Изменение аттрибутов",
            DOWNLOAD_FILE, "Скачивание файлов",
            EDIT_FILE, "Редактирование файлов",
            DELETE_FILE, "Удаление файлов и папок",
            DIRECTORY_ADMIN, "Администрирование"
    );

    public String getResponseMessageFoPermissionCheck(String objectId, Long permissionType) throws Exception {
        Map<String, Object> objectData = PermissionManager.parseObjectId(objectId);
        long itemType = UtCnv.toLong(objectData.get("itemType"));
        long itemId = UtCnv.toLong(objectData.get("itemId"));

        String objectName = getObjectName(itemType, itemId);

        return "Вы не имеете права на %s для %s '%s'".formatted(
                permissionTypeRu.get(permissionType), itemTypeRu.get(itemType), objectName
        );
    }

    public String getResponseMessagePermissionNotFound(String objectId) throws Exception {
        Map<String, Object> objectData = PermissionManager.parseObjectId(objectId);
        long itemType = UtCnv.toLong(objectData.get("itemType"));
        long itemId = UtCnv.toLong(objectData.get("itemId"));

        String objectName = getObjectName(itemType, itemId);

        return "Не найдены права для %s %s".formatted(itemTypeRu.get(itemType), objectName);
    }

    private String getObjectName(long itemType, long itemId) throws Exception {
        String objectName;
        if (itemType == OTHER) {
            objectName = "";
        } else if (itemType == FILE) {
            objectName = fileStorageDao.getFileName(itemId);
        } else {
            objectName = fileStorageDao.getDirectoryName(itemId);
        }

        return objectName;
    }

}
