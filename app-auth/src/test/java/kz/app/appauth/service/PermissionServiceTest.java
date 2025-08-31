package kz.app.appauth.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.context.annotation.*;
import org.springframework.test.context.*;

import java.util.*;

@SpringBootTest
@ComponentScan(basePackages = {"kz.app.appauth", "kz.app.appdbtools"})
@ActiveProfiles("local")
public class PermissionServiceTest {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private GrpService grpService;

    @Autowired
    private Db db;

    private final Long root = 1000L;

    private final Long directory = 1001L;

    private final Long file = 1000L;

    private final Long grp = 1L;

    @Test
    void testUserPermissions() throws Exception {
        //permissionService.reloadUserPermission(1L);
    }

    @Test
    void testGetPermissionsByGrp() throws Exception {
        List<DbRec> permissions = permissionService.getPermissionsByGrp(null, file, null);
        System.out.println(permissions);
    }

    @Test
    void testGetPermissionsByUsr() throws Exception {
        List<DbRec> permissions = permissionService.getPermissionsByUsr(null, file, null);
        System.out.println(permissions);
    }

    @Test
    void testGetPermissionsByParent() throws Exception {

    }

    @Test
    void testCreate() throws Exception {

        //
        Map<Long, Boolean> perms = new HashMap<>();
        perms.put(PermissionType.LIST_DIRECTORY, true);
        perms.put(PermissionType.VIEW_FILE, true);
        perms.put(PermissionType.CREATE_FILE, true);
        perms.put(PermissionType.EDIT_FILE_ATTR, true);
        perms.put(PermissionType.DOWNLOAD_FILE, true);
        perms.put(PermissionType.EDIT_FILE, true);
        perms.put(PermissionType.DELETE_FILE, false);
        perms.put(PermissionType.DIRECTORY_ADMIN, false);

        //
        permissionService.setPermissions(null, grp, null, directory, perms);
    }

    @Test
    void testUpdate() throws Exception {

    }

}