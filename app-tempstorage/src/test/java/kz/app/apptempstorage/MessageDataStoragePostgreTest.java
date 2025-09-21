package kz.app.apptempstorage;

import kz.app.appcore.model.FileData;
import kz.app.appdbtools.repository.Db;
import kz.app.apptempstorage.config.TemporaryStorageConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TemporaryStorageConfig.class)
class MessageDataStoragePostgreTest {

    @Autowired
    private Db db;

    @Autowired
    private MessageDataStoragePostgre storage;

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
