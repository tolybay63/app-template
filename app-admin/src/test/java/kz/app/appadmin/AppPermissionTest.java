package kz.app.appadmin;

import kz.app.appadmin.service.AdminDao;
import kz.app.appadmin.service.PermissionDao;
import kz.app.appadmin.service.UserDao;
import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

@SpringBootTest
public class AppPermissionTest {

    @Autowired
    PermissionDao permissionDao ;


    @Test
    void loadPermissions_test() throws Exception {
        List<DbRec> res = permissionDao.loadPermissions();
        UtDb.outTable(res);
    }


    @Test
    void getLeaf_test() throws Exception {
        Set<String> res = permissionDao.getLeaf("nsi:collection:ins");
        System.out.println(UtString.join(res, "; "));
    }


}
