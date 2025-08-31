package kz.app.appauth.dao;

import jakarta.annotation.*;
import kz.app.appcore.model.*;
import kz.app.appcore.utils.*;
import kz.app.appfile.constants.*;
import kz.app.appfile.service.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@AllArgsConstructor
public class PermissionDAO {
    private final Db db;
    private final FileStorageDao fileStorageDao;

    /**
     * @param grps
     * @return
     * @throws Exception
     */
    public Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> getPermissionsByGrp(@Nullable List<Long> grps) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (grps != null && !grps.isEmpty()) {
            params.put("grp", grps);
        } else {
            params.put("usr", null);
        }

        List<DbRec> list = db.loadList("Permission", params);

        return buildPerimissionMap(list, "grp");
    }

    /**
     * @return
     */
    public Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> getPermissionsByUsr(@Nullable List<Long> usrs) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (usrs != null && !usrs.isEmpty()) {
            params.put("usr", usrs);
        } else {
            params.put("grp", null);
        }

        List<DbRec> list = db.loadList("Permission", params);

        return buildPerimissionMap(list, "usr");
    }

    /**
     * @return
     */
    public Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> getPermissionsByFile(@Nullable List<Long> files) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (files != null && !files.isEmpty()) {
            params.put("file", files);
        } else {
            params.put("directory", null);
        }

        List<DbRec> list = db.loadList("Permission", params);

        return buildPerimissionMap(list, "file");
    }

    /**
     * @return
     */
    public Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> getPermissionsByDirectory(@Nullable List<Long> directories) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (directories != null && !directories.isEmpty()) {
            params.put("directory", directories);
        } else {
            params.put("file", null);
        }

        return buildPerimissionMap(db.loadList("Permission", params), "directory");

    }

    /**
     * @param permissions
     * @param permissionSubject
     * @return
     */
    private Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> buildPerimissionMap(List<DbRec> permissions, String permissionSubject) {
        Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> result = new HashMap<>();

        for (Map<String, Object> permission : permissions) {
            // ID объекта по которой строим мапу с правами
            Long objectId = UtCnv.toLong(permission.get(permissionSubject));
            // Список прав по itemType (папка/файл/другое)
            Map<Long, Map<Long, Map<Long, Boolean>>> itemTypeMap;
            // Список прав по itemId
            Map<Long, Map<Long, Boolean>> itemMap;
            // Список прав доступа к файлу или папке
            Map<Long, Boolean> permissionMap;
            // ID файла или папки
            Long itemId;

            //
            if (result.containsKey(objectId)) {
                itemTypeMap = result.get(objectId);
            } else {
                itemTypeMap = new HashMap<>();
                result.put(objectId, itemTypeMap);
            }

            //
            if (permission.get("file") == null && permission.get("directory") == null) {
                if (itemTypeMap.containsKey(ItemType.OTHER)) {
                    itemMap = itemTypeMap.get(ItemType.OTHER);
                } else {
                    itemMap = new HashMap<>();
                    itemTypeMap.put(ItemType.OTHER, itemMap);
                }
            } else if (permission.get("file") == null) {
                if (itemTypeMap.containsKey(ItemType.DIRECTORY)) {
                    itemMap = itemTypeMap.get(ItemType.DIRECTORY);
                } else {
                    itemMap = new HashMap<>();
                    itemTypeMap.put(ItemType.DIRECTORY, itemMap);
                }
            } else {
                if (itemTypeMap.containsKey(ItemType.FILE)) {
                    itemMap = itemTypeMap.get(ItemType.FILE);
                } else {
                    itemMap = new HashMap<>();
                    itemTypeMap.put(ItemType.FILE, itemMap);
                }
            }

            //
            if (permission.get("file") == null && permission.get("directory") == null) {
                itemId = 0L;
            } else if (permission.get("file") == null) {
                itemId = UtCnv.toLong(permission.get("directory"));
            } else {
                itemId = UtCnv.toLong(permission.get("file"));
            }

            //
            if (itemMap.containsKey(itemId)) {
                permissionMap = itemMap.get(itemId);
            } else {
                permissionMap = new HashMap<>();
                itemMap.put(itemId, permissionMap);
            }

            //
            permissionMap.put(UtCnv.toLong(permission.get("permissionType")), UtCnv.toBoolean(permission.get("permission")));
        }

        return result;
    }

    public List<DbRec> getPaths(@Nullable Long directory, @Nullable Long file) throws Exception {
        List<DbRec> paths;

        if (file == null) {
            // Получаем путь к нашей директории, включая родителей
            paths = fileStorageDao.loadPath(directory).reversed();
        } else {
            // Получаем путь к нашему файлу, включая родителей
            paths = fileStorageDao.loadPathByFile(file).reversed();
        }

        return paths;
    }
}