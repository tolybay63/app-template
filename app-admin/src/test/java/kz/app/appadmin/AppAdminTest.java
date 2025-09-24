package kz.app.appadmin;

import kz.app.appadmin.service.AdminDao;
import kz.app.appadmin.service.UserDao;
import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppAdminTest {

    @Autowired
    AdminDao adminDao;

    @Autowired
    UserDao userDao;


    @Test
    void test1() throws Exception {
        List<DbRec> res = adminDao.loadGroup();
        UtDb.outTable(res);
    }


    @Test
    void test2() throws Exception {
        List<DbRec> res = adminDao.loadUsers(2);
        UtDb.outTable(res);
    }

    @Test
    void test3() throws Exception {
        List<DbRec> res = userDao.loadGroup();
        UtDb.outTable(res);
    }


}
