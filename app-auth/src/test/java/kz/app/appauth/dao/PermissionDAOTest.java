package kz.app.appauth.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.util.*;

@SpringBootTest
public class PermissionDAOTest {

    @Autowired
    private PermissionDAO permissionDAO;

    @Test
    void getPermissionsByGrpTest() throws Exception {
        Map result =  permissionDAO.getPermissionsByGrp(null);
    }

    @Test
    void getPermissionsByUsrTest() throws Exception {
        Map result =  permissionDAO.getPermissionsByUsr(List.of(1L));
    }
}