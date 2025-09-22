package kz.app.appobject;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appobject.service.ObjectDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppObjectTest {

    @Autowired
    ObjectDao objectDao;

    @Test
    void loadObjectServed_Test() throws Exception {
        List<DbRec> res = objectDao.loadObjectServed(0);
        UtDb.outTable(res);

    }




}
