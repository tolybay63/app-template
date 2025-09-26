package kz.app.appincident;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appincident.service.IncidentDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppIncidentTest {

    @Autowired
    IncidentDao incidentDao;

    @Test
    public void testLoad() throws Exception {
        List<DbRec> res = incidentDao.loadEvent(0);
        UtDb.outTable(res);
    }



}
