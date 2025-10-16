package kz.app.applink;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.applink.service.LinkDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppLinkTest {

    @Autowired
    LinkDao linkDao;

    @Test
    void loadObjectServed_test() throws Exception {
        List<DbRec> res = linkDao.loadObjectServed(0);
        UtDb.outTable(res);
    }


}
