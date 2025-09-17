package kz.app.appplan;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appplan.service.PlanDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppPlanTest {

    @Autowired
    PlanDao planDao;

    @Test
    void test1() throws Exception {
        DbRec params = new DbRec();
        List<DbRec> res = planDao.loadPlan(params);
        UtDb.outTable(res);
    }


}
