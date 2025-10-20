package kz.app.applink;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.applink.service.LinkPlan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class LinkPlanTest {

    @Autowired
    LinkPlan linkPlan;

    @Test
    void loadPlan_test() throws Exception {
        DbRec params = new DbRec();
        params.put("date", "2025-07-29");
        params.put("periodType", 11);
        params.put("objLocation", 1077);
        List<DbRec> res = linkPlan.loadPlan(params);
        UtDb.outTable(res);
    }
    //


}
