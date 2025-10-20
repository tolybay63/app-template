package kz.app.applink;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.applink.service.LinkObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class LinkObjectTest {

    @Autowired
    LinkObject linkObject;

    @Test
    void loadObjectServed_test() throws Exception {
        List<DbRec> res = linkObject.loadObjectServed(0);
        UtDb.outTable(res);
    }
    //
    @Test
    void deleteOwnerWithProperties_test() throws Exception {
        linkObject.deleteOwnerWithProperties(1801);
    }


}
