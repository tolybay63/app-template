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
    void loadObjectServed_test() throws Exception {
        List<DbRec> res = objectDao.loadObjectServed(1899);
        UtDb.outTable(res);
    }

    @Test
    public void saveObjectServed_ins_test() throws Exception {
        DbRec map = new DbRec();
        map.put("name", "тест жб мост 5");
        map.put("fullName", "жб мост 5км 5пк (Мосты) 5");
        map.put("linkCls", 1004);
        map.put("objObjectType", 1025);
        map.put("pvObjectType", 1166);
        map.put("StartKm", 1);
        map.put("FinishKm", 1);
        map.put("StartPicket", 50);
        map.put("FinishPicket", 5);
        map.put("fvSide", 1070);
        map.put("pvSide", 1035);
        map.put("Specs", "жб");
        map.put("LocationDetails", "река Шар");
        map.put("PeriodicityReplacement", 3);
        map.put("Number", "1");
        map.put("InstallationDate", "2022-01-01");
        map.put("CreatedAt", "2025-07-07");
        map.put("UpdatedAt", "2025-07-07");
        map.put("Description", "Железобетонный мост 1");
        map.put("objUser", 1003);
        map.put("pvUser", 1087);
        map.put("objSection", 1870);
        map.put("pvSection", 1243);
        List<DbRec> res = objectDao.saveObjectServed("ins", map);
        UtDb.outTable(res);
    }

    @Test
    public void saveObjectServed_upd_test() throws Exception {

        List<DbRec> res = objectDao.loadObjectServed(2165);
        DbRec map = res.getFirst();
//        map.put("id", 1899);
//        map.put("cls", 1042);
        map.put("name", "Uтест жб мост 5");
        map.put("fullName", "Uжб мост 5км 5пк (Мосты) 5");
        map.put("StartKm", 1);
        map.put("FinishKm", 1);
        map.put("StartPicket", 50);
        map.put("FinishPicket", 5);
        map.put("fvSide", 1070);
        map.put("pvSide", 1035);
        map.put("Specs", "жб");
        map.put("LocationDetails", "река Шар");
        map.put("PeriodicityReplacement", 3);
        map.put("Number", "1");
        map.put("InstallationDate", "2022-01-01");
        map.put("CreatedAt", "2025-07-07");
        map.put("UpdatedAt", "2025-07-07");
        map.put("Description", "Железобетонный мост 1");
        map.put("objUser", 1003);
        map.put("pvUser", 1087);
        map.put("objSection", 1870);
        map.put("pvSection", 1243);
        List<DbRec> resQ = objectDao.saveObjectServed("upd", map);
        UtDb.outTable(resQ);
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
