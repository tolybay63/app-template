package kz.app.apptempstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.app.appcore.model.FileData;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtString;
import kz.app.appdbtools.repository.Db;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageDataStoragePostgre implements MessageDataStorage {

    private final Db db;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageDataStoragePostgre(Db db) {
        this.db = db;
    }

    @Override
    public <T> String write(List<T> dataList, long id) throws Exception {
        String guid = genGuid();
        LocalDateTime dt = LocalDateTime.now();
        String json = objectMapper.writeValueAsString(dataList);
        //
        Map<String, Object> params = new HashMap<>();
        params.put("messageData", guid);
        params.put("id", id);
        params.put("dt", dt);
        params.put("json", json);
        //
        db.insertRec("MessageStorage", params);
        //
        return guid;
    }

    @Override
    public  <T> List<T> read(String guid) {
        try {
            Map<String, Object> record = db.loadRec("MessageStorage", Map.of("messageData", guid));
            if (record == null || !record.containsKey("json")) {
                return Collections.emptyList();
            }

            String json = UtCnv.toString(record.get("json"));
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, FileData.class));

        } catch (Exception e) {
            throw new RuntimeException("Failed to read data from message storage", e);
        }
    }

    private final SecureRandom rnd = new SecureRandom();

    private String genGuid() {
        return UtString.toHexString(rnd.nextLong());
    }

}
