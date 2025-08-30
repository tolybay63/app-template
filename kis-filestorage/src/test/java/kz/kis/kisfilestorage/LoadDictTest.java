package kz.kis.kisfilestorage;

import kz.kis.kiscore.model.*;
import kz.kis.kiscore.utils.*;
import kz.kis.kisfilestorage.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.util.*;

@SpringBootTest
class LoadDictTest {

    @Autowired
    private DictDao dictDao;

    @Test
    void loadDict() throws Exception {
        List<DbRec> list = dictDao.loadDict("AttributeType");
        UtDb.outTable(list);
    }

}