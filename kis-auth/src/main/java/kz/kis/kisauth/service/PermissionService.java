package kz.kis.kisauth.service;

import jakarta.annotation.*;
import kz.kis.kisauth.dao.*;
import kz.kis.kisauth.manager.*;
import kz.kis.kisauth.persistance.constant.*;
import kz.kis.kisauth.utils.*;
import kz.kis.kiscore.model.*;
import kz.kis.kiscore.utils.*;
import kz.kis.kisdbtools.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class PermissionService {

    private final Db db;

    private final PermissionDAO permissionDAO;

    private final PermissionManager permissionManager;

    private final Map<Long, Object> userPermissionsCache;

    public PermissionService(Db db, PermissionDAO permissionDAO, PermissionManager permissionManager, Map<Long, Object> userPermissionsCache) {
        this.db = db;
        this.permissionDAO = permissionDAO;
        this.permissionManager = permissionManager;
        this.userPermissionsCache = userPermissionsCache;
    }

    public Map<Long, Boolean> setPermissions(Long usr, Long grp, Long file, Long directory, Map<Long, Boolean> permissions) throws Exception {

        Map<Long, Boolean> newPermissions = fillPermissions(permissions);

        Set<Long> keys = newPermissions.keySet();
        Map<String, Object> params = UtCnv.toMap(
                "usr", usr, "grp", grp, "directory", directory, "file", file, "active", true
        );

        db.deleteRec("Permission", UtCnv.toMap("usr", usr, "grp", grp, "directory", directory, "file", file));

        for (Long key : keys) {
            params.put("permission", newPermissions.get(key));
            params.put("permissionType", key);

            db.insertRec("Permission", params);
        }

        refreshPermissionsCache();

        return newPermissions;
    }

    public List<Map<Long, Boolean>> getPermissionsByItem(Long file, Long directory) throws Exception {
        String sql = "";
        if (file != null && directory != null) {
            sql = "select * from Permission where file = :file and directory = :directory";
        } else if (file != null) {
            sql = "select * from Permission where file = :file and directory is null";
        } else if (directory != null) {
            sql = "select * from Permission where file is null and directory = :directory";
        }

        Map<String, Object> params = UtCnv.toMap("file", file, "directory", directory);
        List<DbRec> result = db.loadSql(sql, params);

        List<Map<Long, Boolean>> permissions = new ArrayList<>();
        for (Map<String, Object> row : result) {
            Map<Long, Boolean> permission = new HashMap<>();
            permission.put(UtCnv.toLong(row.get("permissionType")), UtCnv.toBoolean(row.get("permission")));
            permissions.add(permission);
        }

        return permissions;
    }

    public void deleteAllPermissionsByUsr(Long usr, @Nullable Long dir, @Nullable Long file) throws Exception {
        if (dir == null && file == null) {
            throw new RuntimeException("Не заданы параметры для директории или файла");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("usr", usr);
        if (dir != null) {
            params.put("directory", dir);
        }
        if (file != null) {
            params.put("file", file);
        }

        db.deleteRec("Permission", params);

        refreshPermissionsCache();
    }

    public void deleteAllPermissionsByGrp(Long grp, @Nullable Long dir, @Nullable Long file) throws Exception {
        if (dir == null && file == null) {
            throw new RuntimeException("Не заданы параметры для директории или файла");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("grp", grp);
        if (dir != null) {
            params.put("directory", dir);
        }
        if (file != null) {
            params.put("file", file);
        }

        db.deleteRec("Permission", params);

        refreshPermissionsCache();
    }

    public void refreshPermissionsCache() throws Exception {
        List<Long> usrs = userPermissionsCache.keySet().stream().toList();

        if (usrs.isEmpty()) {
            return;
        }

        refreshPermissionsCache(usrs);
    }

    public void refreshPermissionsCache(List<Long> usrs) throws Exception {

        if (usrs.isEmpty()) {
            return;
        }

        userPermissionsCache.putAll(permissionManager.getUserPermissions(usrs));
    }

    public List<DbRec> getPermissionsByParent(
            @Nullable Long directory,
            @Nullable Long file,
            @Nullable Long usr,
            @Nullable Long grp
    ) throws Exception {
        Long dirParent;
        List<DbRec> paths = permissionDAO.getPaths(directory, file);

        if (paths == null || paths.isEmpty()) {
            throw new RuntimeException("Предки не обнаружены!");
        }

        Map<String, Object> params = new HashMap<>();
        if (grp != null) {
            params.put("id", grp);
        }

        //Если достаём для файла то берём id, так как первая папка в пути это уже родительская
        dirParent = UtCnv.toLong(file == null ? paths.getFirst().get("parent") : paths.getFirst().get("id"));

        if (usr != null) {
            return getPermissionsByUsr(usr, null, dirParent);
        } else {
            return getPermissionsByGrp(grp, null, dirParent);
        }
    }

    /**
     * @param usr
     * @param file
     * @param directory
     * @return
     * @throws Exception
     */
    public List<DbRec> getPermissionsByUsr(
            @Nullable Long usr,
            @Nullable Long file,
            @Nullable Long directory
    ) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (usr != null) {
            params.put("id", usr);
        }

        List<DbRec> usrs = db.loadList("Usr", params);

        return permissionManager.buildPermissionsList(usrs, directory, file, false);
    }

    /**
     * @param grp
     * @param file
     * @param directory
     * @return
     * @throws Exception
     */
    public List<DbRec> getPermissionsByGrp(
            @Nullable Long grp,
            @Nullable Long file,
            @Nullable Long directory
    ) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (grp != null) {
            params.put("id", grp);
        }

        List<DbRec> grps = db.loadList("Grp", params);

        return permissionManager.buildPermissionsList(grps, directory, file, true);
    }

    private Map<Long, Boolean> fillPermissions(Map<Long, Boolean> permissions) {
        Set<Long> keys = permissions.keySet();

        Long minKey = UtCnv.toLong(keys.stream().min(Long::compareTo).orElse(-1L));
        if (minKey == -1L) {
            throw new RuntimeException("Список устанавливаемых прав пуст");
        }

        Map<Long, Set<Long>> dependentPermissions = PermissionType.getDependentPermissions();
        for (Long key : keys) {
            Set<Long> dependents = dependentPermissions.get(key);
            for (Long dependent : dependents) {
                if (permissions.get(dependent) != null && (permissions.get(key) && !permissions.get(dependent))) {
                    throw new RuntimeException("Противоречащие права '" +
                            PermissionDict.permissionTypeRu.get(key) + "' и '" +
                            PermissionDict.permissionTypeRu.get(dependent) + "'"
                    );
                }
            }
        }

        Map<Long, Boolean> newPermissions = new HashMap<>();
        boolean isPermission = permissions.get(minKey);
        Set<Long> permissionTypes = dependentPermissions.get(minKey);
        for (Long permissionType : permissionTypes) {
            if (!permissions.containsKey(permissionType)) {
                newPermissions.put(permissionType, isPermission);
            }
        }

        for (Long key : keys) {
            newPermissions.put(key, permissions.get(key));
        }

        return newPermissions;
    }

}