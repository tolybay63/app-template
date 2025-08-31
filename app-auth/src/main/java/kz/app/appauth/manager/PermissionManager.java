package kz.app.appauth.manager;

import jakarta.annotation.*;
import kz.app.appauth.dao.*;
import kz.app.appauth.exceptions.*;
import kz.app.appauth.persistance.constant.*;
import kz.app.appauth.service.*;
import kz.app.appauth.utils.*;
import kz.app.appfile.constants.*;
import kz.app.appfile.service.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;

import java.util.*;

import static kz.app.appauth.persistance.constant.PermissionType.*;

@Service
public class PermissionManager {
    private final PermissionDict permissionDict;
    private final PermissionDAO permissionDAO;
    private final GrpService grpService;
    private final Map<Long, Object> userPermissionsCache;
    private final FileStorageDao fileStorageDao;
    private final Db db;


    public PermissionManager(
            PermissionDict permissionDict,
            PermissionDAO permissionDAO,
            GrpService grpService,
            Db db,
            Map<Long, Object> userPermissionsCache
    ) {
        this.permissionDict = permissionDict;
        this.permissionDAO = permissionDAO;
        this.grpService = grpService;
        this.userPermissionsCache = userPermissionsCache;
        this.db = db;
        this.fileStorageDao = new FileStorageDao(db);
    }

    public Map getPermissionsByUsr(Long usr, String objectId) throws Exception {
        return getUsrPermissionFromCache(usr, objectId);
    }

    public Map getUsrPermissionFromCache(Long user, String objectId) throws Exception {
        // Из objectId получаем его itemType и itemId (Это папка или файл? И его айди)
        Map<String, Object> objectIdData = parseObjectId(objectId);
        Long itemType = UtCnv.toLong(objectIdData.get("itemType"));
        Long itemId = UtCnv.toLong(objectIdData.get("itemId"));

        if (userPermissionsCache.isEmpty()) {
            userPermissionsCache.putAll(getUserPermissions(List.of(user)));
        }

        // По пользователю получаем его напрямую заданные права
        if (!userPermissionsCache.containsKey(user)) {
            throw new Exception("User doesn't have permission for user " + user);
        }
        Map itemTypePermission = UtCnv.toMap(userPermissionsCache.get(user));

        // Ищем прямые права по itemType
        Map subjectTypePermission = UtCnv.toMap(itemTypePermission.get(itemType));

        // Если по текущему itemType права не найдены, то ищем по другому
        // Если itemType это файл и прямых прав нету, то берем известные права на папки
        if (subjectTypePermission.isEmpty()) {
            if (itemType == ItemType.FILE) {
                subjectTypePermission = UtCnv.toMap(itemTypePermission.get(ItemType.DIRECTORY));
            }
        }

        // Тут сначала пытаемся получить прямые пользовательские права
        // Если таковых нет, берем групповые
        Map itemPermission = new HashMap();
        if (subjectTypePermission.containsKey(PermissionSubject.USR)) {
            itemPermission = UtCnv.toMap(subjectTypePermission.get(PermissionSubject.USR));
        } else if (subjectTypePermission.containsKey(PermissionSubject.GRP)) {
            itemPermission = UtCnv.toMap(subjectTypePermission.get(PermissionSubject.GRP));
        }

        // Сначала пробуем получить прямые права по айди айтема
        // Если прямых нету, то ищем унаследованные через его ближайшую родительскую папку
        Map permission = null;
        if (itemPermission.containsKey(itemId)) {
            permission = UtCnv.toMap(itemPermission.get(itemId));
        } else {
            List<DbRec> parents = getParentsDirs(itemType, itemId).reversed();

            for (Map<String, Object> parent : parents) {
                Long parentId = UtCnv.toLong(parent.get("id"));
                if (itemPermission.containsKey(parentId)) {
                    permission = UtCnv.toMap(itemPermission.get(parentId));
                    break;
                }
            }
        }

        return permission;
    }

    public List<DbRec> buildPermissionsList(List<DbRec> permissionSubjects, Long directory, Long file, boolean groupOnly) throws Exception {
        List<DbRec> parents = permissionDAO.getPaths(directory, file);
        List<Long> ids = permissionSubjects.stream().map(it -> UtCnv.toLong(it.get("id"))).toList();
        Map<Long, Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>>> permissionsBySubject;

        String subjectFiled;
        if (!groupOnly) {
            permissionsBySubject = getUserPermissions(ids);
            subjectFiled = "usr";
        } else {
            permissionsBySubject = getGroupPermissions(ids);
            subjectFiled = "grp";
        }

        List<DbRec> result = new ArrayList<>();
        for (Map<String, Object> subject : permissionSubjects) {
            DbRec perms = createPermissionMap(directory, file, subjectFiled, subject);

            Long id = UtCnv.toLong(subject.get("id"));

            Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> subjectPermissions = permissionsBySubject.get(id);

            Map<Long, Map<Long, Boolean>> userPermissionsByFiles = new HashMap<>();
            Map<Long, Map<Long, Boolean>> groupPermissionsByFiles = new HashMap<>();

            Map<Long, Map<Long, Boolean>> userPermissionsByDirectory = new HashMap<>();
            Map<Long, Map<Long, Boolean>> groupPermissionsByDirectory = new HashMap<>();

            if (subjectPermissions != null) {
                if (!groupOnly) {
                    userPermissionsByFiles = subjectPermissions.get(ItemType.FILE).get(PermissionSubject.USR);
                    groupPermissionsByFiles = subjectPermissions.get(ItemType.FILE).get(PermissionSubject.GRP);

                    userPermissionsByDirectory = subjectPermissions.get(ItemType.DIRECTORY).get(PermissionSubject.USR);
                    groupPermissionsByDirectory = subjectPermissions.get(ItemType.DIRECTORY).get(PermissionSubject.GRP);
                } else {
                    userPermissionsByFiles = new HashMap<>();
                    groupPermissionsByFiles = subjectPermissions.get(ItemType.FILE).get(PermissionSubject.GRP);

                    userPermissionsByDirectory = new HashMap<>();
                    groupPermissionsByDirectory = subjectPermissions.get(ItemType.DIRECTORY).get(PermissionSubject.GRP);
                }
            }

            if (file != null && userPermissionsByFiles != null && userPermissionsByFiles.containsKey(file)) {
                putMapPermission(perms, userPermissionsByFiles, false, false, file);

            } else if (file != null && groupPermissionsByFiles != null && groupPermissionsByFiles.containsKey(file)) {
                putMapPermission(perms, groupPermissionsByFiles, false, true, file);

            } else if (directory != null && userPermissionsByDirectory != null && userPermissionsByDirectory.containsKey(directory)) {
                putMapPermission(perms, userPermissionsByDirectory, false, false, directory);

            } else if (directory != null && groupPermissionsByDirectory != null && groupPermissionsByDirectory.containsKey(directory)) {
                putMapPermission(perms, groupPermissionsByDirectory, false, true, directory);

            } else {
                for (Map<String, Object> parent : parents) {
                    Long parentId = UtCnv.toLong(parent.get("id"));

                    if (userPermissionsByDirectory != null && userPermissionsByDirectory.containsKey(parentId)) {
                        putMapPermission(perms, userPermissionsByDirectory, true, false, parentId);
                        break;

                    } else if (groupPermissionsByDirectory != null && groupPermissionsByDirectory.containsKey(parentId)) {
                        putMapPermission(perms, groupPermissionsByDirectory, true, true, parentId);
                        break;
                    }
                }
            }

            result.add(perms);
        }

        return result;
    }

    public Map<Long, Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>>> getGroupPermissions(@Nullable List<Long> grpList) throws Exception {
        Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> grpPermissions = permissionDAO.getPermissionsByGrp(grpList);

        Set<Long> grps = grpPermissions.keySet();

        Map<Long, Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>>> result = new HashMap<>();

        for (Long grp : grps) {
            Map<Long, Map<Long, Map<Long, Boolean>>> grpPermissionsByItemType = grpPermissions.get(grp);
            Map<Long, Map<Long, Boolean>> grpPermissionsByFiles;
            Map<Long, Map<Long, Boolean>> grpPermissionsByDirectories;

            if (grpPermissionsByItemType.containsKey(ItemType.FILE)) {
                grpPermissionsByFiles = new HashMap<>(grpPermissionsByItemType.get(ItemType.FILE));
            } else {
                grpPermissionsByFiles = new HashMap<>();
            }

            if (grpPermissionsByItemType.containsKey(ItemType.DIRECTORY)) {
                grpPermissionsByDirectories = new HashMap<>(grpPermissionsByItemType.get(ItemType.DIRECTORY));
            } else {
                grpPermissionsByDirectories = new HashMap<>();
            }

            Map<Long, Map<Long, Map<Long, Boolean>>> permissionsBySubjectDirectories = new HashMap<>();
            permissionsBySubjectDirectories.put(PermissionSubject.GRP, grpPermissionsByDirectories);

            Map<Long, Map<Long, Map<Long, Boolean>>> permissionsBySubjectFiles = new HashMap<>();
            permissionsBySubjectFiles.put(PermissionSubject.GRP, grpPermissionsByFiles);

            Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> resultByItemType = new HashMap<>();
            resultByItemType.put(ItemType.FILE, permissionsBySubjectFiles);
            resultByItemType.put(ItemType.DIRECTORY, permissionsBySubjectDirectories);

            result.put(grp, resultByItemType);
        }

        return result;
    }

    public Map<Long, Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>>> getUserPermissions(@Nullable List<Long> usrs) throws Exception {

        if (usrs == null || usrs.isEmpty()) {
            List<DbRec> usrList = db.loadList("Usr", new HashMap<>());
            usrs = usrList.stream().map(it -> UtCnv.toLong(it.get("id"))).toList();
        }

        //
        Map<Long, List<Long>> usrGrps = grpService.getGrpsByUsr(usrs);

        //
        Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> userPermissions = permissionDAO.getPermissionsByUsr(usrs);
        Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> grpPermissions = permissionDAO.getPermissionsByGrp(getListGrps(usrGrps));

        Map<Long, Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>>> result = new HashMap<>();

        for (Long user : usrs) {
            Map<Long, Map<Long, Map<Long, Boolean>>> usrPermissionsByItemType = userPermissions.get(user);

            if (usrPermissionsByItemType == null) {
                usrPermissionsByItemType = new HashMap<>();
            }

            //
            Map<Long, Map<Long, Boolean>> usrPermissionsByDirectories = new HashMap<>();
            Map<Long, Map<Long, Boolean>> usrPermissionsByFiles = new HashMap<>();
            Map<Long, Map<Long, Boolean>> usrPermissionsByOther = new HashMap<>();

            List<Long> grps = usrGrps.get(user);

            Map<Long, Map<Long, Boolean>> grpPermissionsByDirectories = new HashMap<>();
            Map<Long, Map<Long, Boolean>> grpPermissionsByFiles = new HashMap<>();
            Map<Long, Map<Long, Boolean>> grpPermissionsByOther = new HashMap<>();

            if (grps != null) {
                for (Long grp : grps) {
                    Map<Long, Map<Long, Map<Long, Boolean>>> grpPermissionsByItemType = grpPermissions.get(grp);

                    //
                    calculatePermissions(grpPermissionsByItemType, grpPermissionsByDirectories, ItemType.DIRECTORY);

                    //
                    calculatePermissions(grpPermissionsByItemType, grpPermissionsByFiles, ItemType.FILE);

                    //
                    calculatePermissions(grpPermissionsByItemType, grpPermissionsByOther, ItemType.OTHER);
                }
            }

            //
            calculatePermissions(usrPermissionsByItemType, usrPermissionsByDirectories, ItemType.DIRECTORY);

            //
            calculatePermissions(usrPermissionsByItemType, usrPermissionsByFiles, ItemType.FILE);

            //
            calculatePermissions(usrPermissionsByItemType, usrPermissionsByOther, ItemType.OTHER);

            //
            Map<Long, Map<Long, Map<Long, Boolean>>> permissionsBySubjectDirectories = new HashMap<>();
            calculatePermissions(permissionsBySubjectDirectories, usrPermissionsByDirectories, grpPermissionsByDirectories);

            //
            Map<Long, Map<Long, Map<Long, Boolean>>> permissionsBySubjectFiles = new HashMap<>();
            calculatePermissions(permissionsBySubjectFiles, usrPermissionsByFiles, grpPermissionsByFiles);

            //
            Map<Long, Map<Long, Map<Long, Boolean>>> otherPermissions = new HashMap<>();
            calculatePermissions(otherPermissions, usrPermissionsByOther, grpPermissionsByOther);

            //
            Map<Long, Map<Long, Map<Long, Map<Long, Boolean>>>> resultByItemType = new HashMap<>();
            resultByItemType.put(ItemType.FILE, permissionsBySubjectFiles);
            resultByItemType.put(ItemType.DIRECTORY, permissionsBySubjectDirectories);
            resultByItemType.put(ItemType.OTHER, otherPermissions);

            result.put(user, resultByItemType);
        }

        return result;

    }

    /**
     * Выполняет проверку прав пользователя, разрешает или запрещает выполнение операции
     *
     * @param user           id пользователя
     * @param objectId       строка в формате ItemType_ItemId описывающая тип объекта (файл/папка) и его id,
     *                       напр: 1_3921 - файл 3921, 2_1001 - папка 1001
     * @param permissionType тип права (чтение/редактирование и тд)
     * @return булево разрешение или запрет на операцию (permissionType)
     * @throws Exception в случае если прав не было найдено никаких
     */
    public boolean check(Long user, String objectId, Long permissionType) throws Exception {
        Map permission = getUsrPermissionFromCache(user, objectId);

        // Права хотя бы на корневой каталог должны быть
        if (permission != null) {
            if (permission.containsKey(permissionType)) {
                return UtCnv.toBoolean(permission.get(permissionType));
            } else {
                return false;
            }
        }

        // В противном случае скидываем ошибку
        String message = permissionDict.getResponseMessagePermissionNotFound(objectId);
        throw new PermissionException(HttpStatusCode.valueOf(430), message);
    }

    // В случае проверки прав на множество объектов
    public boolean check(Long user, Map<String, Object> permissionRules) throws Exception {

        Set<String> objectIds = permissionRules.keySet();

        for (String objectId : objectIds) {
            Long permissionType = UtCnv.toLong(permissionRules.get(objectId));

            boolean result = check(user, objectId, permissionType);

            if (!result) {
                String message = permissionDict.getResponseMessageFoPermissionCheck(objectId, permissionType);
                throw new PermissionException(HttpStatusCode.valueOf(430), message);
            }
        }

        return true;
    }

    private List<DbRec> getParentsDirs(Long itemType, Long itemId) throws Exception {
        List<DbRec> result;
        if (itemType == ItemType.DIRECTORY) {
            result = fileStorageDao.loadPath(itemId);
        } else {
            result = fileStorageDao.loadPathByFile(itemId);
        }

        return result;
    }

    public static Map<String, Object> parseObjectId(String objectId) {
        String[] objectIdData = objectId.split("-");

        return UtCnv.toMap("itemType", objectIdData[0], "itemId", objectIdData[1]);
    }

    private void calculatePermissions(
            Map<Long, Map<Long, Map<Long, Boolean>>> permissionsBySubject,
            Map<Long, Map<Long, Boolean>> usrPermissionsByItem,
            Map<Long, Map<Long, Boolean>> grpPermissionsByItem
    ) {

        Set<Long> grpKeys = grpPermissionsByItem.keySet();
        Map<Long, Map<Long, Boolean>> tmpPermissionsByItem = new HashMap<>();
        if (grpKeys.isEmpty()) {
            Set<Long> usrKeys = usrPermissionsByItem.keySet();
            for (Long usrKey : usrKeys) {
                tmpPermissionsByItem.put(usrKey, usrPermissionsByItem.get(usrKey));
                permissionsBySubject.put(PermissionSubject.USR, tmpPermissionsByItem);
            }
        } else {
            for (Long grpKey : grpKeys) {
                if (usrPermissionsByItem.containsKey(grpKey)) {
                    tmpPermissionsByItem.put(grpKey, usrPermissionsByItem.get(grpKey));
                    permissionsBySubject.put(PermissionSubject.USR, tmpPermissionsByItem);
                } else {
                    tmpPermissionsByItem.put(grpKey, grpPermissionsByItem.get(grpKey));
                    permissionsBySubject.put(PermissionSubject.GRP, tmpPermissionsByItem);
                }
            }
        }
    }

    private void calculatePermissions(
            Map<Long, Map<Long, Map<Long, Boolean>>> permissionsByItemType,
            Map<Long, Map<Long, Boolean>> permissionsByItem,
            Long itemType
    ) {
        Set<Long> permissionTypes = getPermissionTypes();

        if (permissionsByItemType == null) {
            return;
        }

        Map<Long, Map<Long, Boolean>> tmpPermissionsByItem = permissionsByItemType.get(itemType);

        if (tmpPermissionsByItem == null) {
            return;
        }

        if (permissionsByItem.isEmpty()) {
            permissionsByItem.putAll(tmpPermissionsByItem);
            return;
        }

        for (Long key : tmpPermissionsByItem.keySet()) {
            if (!permissionsByItem.containsKey(key)) {
                permissionsByItem.put(key, tmpPermissionsByItem.get(key));
                continue;
            }

            Map<Long, Boolean> permissions = permissionsByItem.get(key);
            Map<Long, Boolean> tmpPermissions = tmpPermissionsByItem.get(key);

            for (Long permissionsType : permissionTypes) {
                Boolean permission = permissions.get(permissionsType);
                Boolean tmpPermission = tmpPermissions.get(permissionsType);

                if (permission == null || tmpPermission == null) {
                    permissions.put(permissionsType, false);
                    continue;
                }

                if (!tmpPermission) {
                    permissions.put(permissionsType, false);
                }
            }
        }
    }

    private List<Long> getListGrps(Map<Long, List<Long>> usrGrps) {
        Set<Long> grps = new HashSet<>();
        for (Long grpId : usrGrps.keySet()) {
            grps.addAll(usrGrps.get(grpId));
        }

        return new ArrayList<>(grps);
    }

    private void putMapPermission(
            Map<String, Object> perms,
            Map<Long, Map<Long, Boolean>> permissionsByItem,
            Boolean inherited,
            Boolean grpPermission,
            Long item
    ) {
        perms.put("inherited", inherited);
        perms.put("grpPermission", grpPermission);
        perms.put("permissions", permissionsByItem.get(item));
    }

    private DbRec createPermissionMap(Long directory, Long file, String subjectFiled, Map<String, Object> subject) {
        DbRec perms = new DbRec();

        if (file == null) {
            perms.put("directory", directory);
        } else {
            perms.put("file", file);
        }

        perms.put(subjectFiled, subject);
        perms.put("inherited", false);
        perms.put("permissions", getDefaultPermissionMap());
        perms.put("grpPermission", false);

        return perms;
    }

    private Map<Long, Boolean> getDefaultPermissionMap() {
        Map<Long, Boolean> permissions = new HashMap<>();
        Set<Long> permissionTypes = getPermissionTypes();

        for (Long permissionType : permissionTypes) {
            permissions.put(permissionType, false);
        }

        return permissions;
    }
}