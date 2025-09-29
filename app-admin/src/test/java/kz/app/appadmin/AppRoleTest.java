package kz.app.appadmin;

import kz.app.appadmin.service.RoleDao;
import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appdbtools.repository.Db;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppRoleTest {

    @Autowired
    Db dbAdmin;

    @Autowired
    RoleDao roleDao;

    @Test
    void loadRoles_test() throws Exception {
        List<DbRec> res = roleDao.loadRoles();
        UtDb.outTable(res);
    }

    @Test
    void insertRole_test() throws Exception {
        DbRec rec = new DbRec();
        rec.put("name", "Тестировщик");
        rec.put("cmt", "Group For Sprint");
        DbRec res = roleDao.insertRole(rec);
        UtDb.outRecord(res);
    }

    @Test
    void updateRole_test() throws Exception {
        DbRec rec = dbAdmin.loadRec("AuthRole", 1003, true);
        rec.put("fullname", "Тестировщик Update");
        DbRec res = roleDao.updateRole(rec);
        UtDb.outRecord(res);
    }

    @Test
    void deleteRole_test() throws Exception {
        roleDao.deleteRole(2000);
        UtDb.outTable(roleDao.loadRoles());
    }

    //

    @Test
    void getRolePermissions_test() throws Exception {
        String perm = roleDao.getRolePermissions(1002);
        System.out.println(perm);
    }




}
