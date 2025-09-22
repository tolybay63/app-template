package kz.app.structure;

import kz.app.structure.service.StructureDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Set;

@SpringBootTest
public class AppStructureTest {

    @Autowired
    StructureDao structureDao;

    @Test
    void Test1() throws Exception {
        Set<Object> res = structureDao.getIdsObjLocation(1069);
        System.out.println(res);
    }




}
