package kz.app.apppersonnal;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.apppersonnal.service.PersonnalDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppPersonnalTest {

    @Autowired
    PersonnalDao personnalDao;

    @Test
    void Test1() throws Exception {
        List<DbRec> res = personnalDao.getObjList(1064);
        UtDb.outTable(res);
    }




}
