package kz.app.appnsi;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appnsi.service.NsiDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class AppNsiTest {

    @Autowired
    NsiDao nsiDao;

    @Test
    void loadDefects_test() throws Exception {
        List<DbRec> res = nsiDao.loadDefects(0L);
        UtDb.outTable(res);
    }


    @Test
    void loadDepartments_test() throws Exception {
        List<DbRec> res = nsiDao.loadDepartments("Typ_Location", "Prop_LocationMulti");
        UtDb.outTable(res);
    }


    @Test
    void set_test() throws Exception {
        Set<Long> set = new HashSet<>();
        set.add(0L);
        set.add(1L);
        System.out.println(set.toArray()[0]);
    }
}
