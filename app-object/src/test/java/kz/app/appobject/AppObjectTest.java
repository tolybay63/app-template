package kz.app.appobject;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appobject.service.ObjectDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class AppObjectTest {

    @Autowired
    ObjectDao objectDao;

    @Test
    void Test1() throws Exception {
        List<DbRec> res = objectDao.loadObjectServed(0);
        UtDb.outTable(res);
    }

    @Test
    void Test2() throws Exception {
        List<DbRec> res = objectDao.getObjInfo("(1068,1069,1070)", "");
        UtDb.outTable(res);
    }

    @Test
    void Test3() throws Exception {
        DbRec params = new DbRec();
        params.put("ids", "(1428,1160)");
        params.put("codProp", 1142);
        List<DbRec> res = objectDao.getObjInfoFromData(params);
        UtDb.outTable(res);
    }

}
