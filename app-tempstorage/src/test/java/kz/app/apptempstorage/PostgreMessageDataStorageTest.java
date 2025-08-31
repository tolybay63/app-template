package kz.app.apptempstorage;

import kz.app.appcore.model.*;
import kz.app.appdbtools.repository.*;
import kz.app.apptempstorage.config.*;
import kz.app.apptempstorage.config.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TemporaryStorageConfig.class)
class PostgreMessageDataStorageTest {

    @Autowired
    private Db db;

    @Autowired
    private PostgreMessageDataStorage storage;

    @Test
    void testWriteAndRead() {
        FileData fileData = new FileData();
        fileData.setText("Sample text");
        fileData.setSource("Sample source");
        List<FileData> fileDataList = Collections.singletonList(fileData);

        // Perform write operation
        String guid = Assertions.assertDoesNotThrow(() -> storage.write(fileDataList, 1000L));

        // Perform read operation
        List<FileData> retrievedData = storage.read(guid);
        assertNotNull(retrievedData);
        assertEquals(1, retrievedData.size());
        assertEquals("Sample text", retrievedData.get(0).getText());
        assertEquals("Sample source", retrievedData.get(0).getSource());
    }

}
