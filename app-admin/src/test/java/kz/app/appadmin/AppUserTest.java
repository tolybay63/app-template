package kz.app.appadmin;

import kz.app.appadmin.service.UserDao;
import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appdbtools.repository.Db;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppUserTest {

    @Autowired
    Db dbAdmin;

    @Autowired
    UserDao userDao;

    @Test
    void loadGroup_test() throws Exception {
        List<DbRec> res = userDao.loadGroup();
        UtDb.outTable(res);
    }

    @Test
    void insertGroup_test() throws Exception {
        DbRec rec = new DbRec();
        rec.put("name", "Group Test");
        rec.put("fullName", "Group Test Test");
        rec.put("cmt", "Group For Sprint");
        DbRec res = userDao.insertGroup(rec);
        UtDb.outRecord(res);
    }

    @Test
    void updateGroup_test() throws Exception {
        DbRec rec = dbAdmin.loadRec("AuthUserGr", 2, true);
        rec.put("name", "Group Test Update");
        rec.put("cmt", "Group For Sprint Update");
        DbRec res = userDao.updateGroup(rec);
        UtDb.outRecord(res);
    }

    @Test
    void deleteGroup_test() throws Exception {
        userDao.deleteGroup(1001);
    }

    //**********************

    @Test
    void loadUsers_test() throws Exception {
        List<DbRec> res = userDao.loadUsers(2);
        UtDb.outTable(res);
    }

    @Test
    void insertUser_test() throws Exception {
        DbRec rec = new DbRec();

        //rec.put("login", "user");
        rec.put("passwd", "123");
        rec.put("name", "User Test");
        rec.put("email", "user@test.com");
        rec.put("authUserGr", 2);

        DbRec res = userDao.insertUser(rec);
        UtDb.outRecord(res);
    }

}
