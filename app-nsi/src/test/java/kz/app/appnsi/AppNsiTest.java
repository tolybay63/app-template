package kz.app.appnsi;

import kz.app.appnsi.service.NsiDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AppNsiTest {

    @Autowired
    NsiDao nsiDao;

/*
    @Test
    void loadDefects_test() throws Exception {
        List<DbRec> res = nsiDao.loadDefects(0L);
        UtDb.outTable(res);
    }

    @Test
    void loadSourceCollections_test() throws Exception {
        List<DbRec> res = nsiDao.loadSourceCollections(0L);
        UtDb.outTable(res);
    }

    @Test
    void loadDepartments_test() throws Exception {
        List<DbRec> res = nsiDao.loadDepartments("Typ_Location", "Prop_LocationMulti");
        UtDb.outTable(res);
    }

    @Test
    void loadDepartmentsWithFile_test() throws Exception {
        DbRec res = nsiDao.loadDepartmentsWithFile(2292);
        String deps = res.getString("departments");
        List<DbRec> files = (List<DbRec>) res.get("files");

        System.out.println(deps);
        UtDb.outTable(files);
    }
*/


}
