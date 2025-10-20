package kz.app.appinspection;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appinspection.service.InspectionDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppInspectionTest {

    @Autowired
    InspectionDao inspectionDao;

/*
    @Test
    void loadInspection_test() throws Exception {
        DbRec params = new DbRec();
        params.put("date", "2025-07-29");
        params.put("periodType", 11);
        params.put("objLocation", 1077);
        List<DbRec> res = inspectionDao.loadInspection(params);
        UtDb.outTable(res);
    }
*/


}
