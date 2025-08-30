package kz.kis.kisauth.manager;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.util.*;

@SpringBootTest
public class PermissionsManagerTest {

    @Autowired
    private PermissionManager permissionManager;

    @Test
    void getUserPermissions() throws Exception {
        Map result = permissionManager.getUserPermissions(null);
    }

    @Test
    void getGroupPermissions() throws Exception {
        Map result = permissionManager.getGroupPermissions(null);
    }

}