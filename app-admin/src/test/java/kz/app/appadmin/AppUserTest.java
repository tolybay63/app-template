package kz.app.appadmin;

import kz.app.appadmin.service.AdminDao;
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
public class AppUserTest {

    @Autowired
    Db db;

    @Autowired
    UserDao userDao;

    @Test
    void test_loadGroup() throws Exception {
        List<DbRec> res = userDao.loadGroup();
        UtDb.outTable(res);
    }

    @Test
    void test_insertGroup() throws Exception {
        DbRec rec = new DbRec();
        rec.put("name", "Group Test");
        rec.put("fullName", "Group Test Test");
        rec.put("cmt", "Group For Sprint");
        DbRec res = userDao.insertGroup(rec);
        UtDb.outRecord(Map.of(res.getLong("id"), res));
    }

    @Test
    void test_updateGroup() throws Exception {
        DbRec rec = db.loadRec("AuthUserGr", 1000);
        rec.put("name", "Group Test Update");
        rec.put("cmt", "Group For Sprint Update");
        DbRec res = userDao.updateGroup(rec);
        UtDb.outRecord(Map.of(res.getLong("id"), res));
    }

    @Test
    void test_deleteGroup() throws Exception {
        userDao.deleteGroup(1001);
    }

    //**********************

    @Test
    void test_loadUsers() throws Exception {
        List<DbRec> res = userDao.loadUsers(2);
        UtDb.outTable(res);
    }

    @Test
    void test_insertUser() throws Exception {
        DbRec rec = new DbRec();

        //rec.put("login", "user");
        rec.put("passwd", "123");
        rec.put("name", "User Test");
        rec.put("email", "user@test.com");
        rec.put("authUserGr", 2);

        DbRec res = userDao.insertUser(rec);
        UtDb.outRecord(Map.of(res.getLong("id"), res));
    }

}
