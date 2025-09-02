package kz.app.appclient;

import kz.app.appclient.service.ClientDao;
import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appdbtools.repository.Db;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppTest {

    
    @Autowired
     ClientDao clientDao;

    @Test
    public void testLoad() throws Exception {
        List<DbRec> res = clientDao.loadClient(0);
        UtDb.outTable(res);
    }



}
