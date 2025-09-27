package kz.app.appadmin;

import kz.app.appadmin.service.RoleDao;
import kz.app.appadmin.service.UserDao;
import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appdbtools.repository.Db;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class AppRoleTest {

    @Autowired
    Db dbAdmin;

    @Autowired
    RoleDao roleDao;

    @Test
    void test_loadRole() throws Exception {
        List<DbRec> res = roleDao.loadRoles();
        UtDb.outTable(res);
    }

    @Test
    void test_insertRole() throws Exception {
        DbRec rec = new DbRec();
        rec.put("name", "Тестировщик");
        rec.put("cmt", "Group For Sprint");
        DbRec res = roleDao.insertRole(rec);
        UtDb.outRecord(res);
    }

    @Test
    void test_updateRole() throws Exception {
        DbRec rec = dbAdmin.loadRec("AuthRole", 1003, true);
        rec.put("fullname", "Тестировщик Update");
        DbRec res = roleDao.updateRole(rec);
        UtDb.outRecord(res);
    }

    @Test
    void test_deleteRole() throws Exception {
        roleDao.deleteRole(1003);
        UtDb.outTable(roleDao.loadRoles());
    }

    //

    @Test
    void test_loadRolePermission() throws Exception {
        String perm = roleDao.getPermissions(1002);
        System.out.println(perm);
    }




}
