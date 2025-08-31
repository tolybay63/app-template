package kz.app.appmain;

import kz.app.appcore.model.*;
import kz.app.appcore.utils.*;
import kz.app.appdbtools.repository.*;
import kz.app.appmain.service.*;
import kz.app.appstorage.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.io.*;
import java.util.*;

@SpringBootTest
class AppTest {

    @Autowired
    private DictDao dictDao;

    @Autowired
    private Db db;

    @Autowired
    @Qualifier("contentStorage")
    private StorageRepository storageRepository;

    @Test
    void list() throws Exception {
        List<DbRec> list = db.loadList("Dict", null);
        UtDb.outTable(list);
    }

    @Test
    void createDict() throws Exception {
        long id = db.insertRec("Dict", Map.of("name", "AttributeType"));
        System.out.println("Dict.id: " + id);

        List<DbRec> list = db.loadList("Dict", null);
        UtDb.outTable(list);
    }

    @Test
    void loadDict() throws Exception {
        List<DbRec> list = dictDao.loadDict("AttributeType");
        UtDb.outTable(list);
    }


    String fileHash = "8004928d7a4e";
    String fileName = "src/test/java/kz/app/appmain/appTest-001.txt";

    @Test
    void uploadFile() throws Exception {
        File file = new File(fileName);

        System.out.println("file: " + file);
        System.out.println(UtFile.loadString(file.getAbsolutePath()));

        storageRepository.uploadFile(file, fileHash);
        System.out.println("uploadFile, fileHash: " + fileHash);
    }

    @Test
    void downloadFile() throws Exception {
        uploadFile();

        //
        File file = storageRepository.downloadFile(fileHash);
        System.out.println("file: " + file);
        System.out.println(UtFile.loadString(file));

        //
        storageRepository.deleteFile(fileHash);
        System.out.println("deleteFile, fileHash: " + fileHash);

        //
        file = storageRepository.downloadFile(fileHash);
        System.out.println("file: " + file);
    }

}