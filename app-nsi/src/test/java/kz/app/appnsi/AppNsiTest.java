package kz.app.appnsi;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appnsi.service.NsiDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppNsiTest {

    @Autowired
    NsiDao nsiDao;

    @Test
    void loadDefects_test() throws Exception {
        List<DbRec> res = nsiDao.loadDefects(0L);
        UtDb.outTable(res);
    }


}
