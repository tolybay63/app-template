package kz.app.apptempstorage;

import com.fasterxml.jackson.databind.*;
import kz.app.appcore.model.*;
import kz.app.appcore.utils.*;
import kz.app.appdbtools.repository.*;
import org.springframework.stereotype.*;

import java.security.*;
import java.time.*;
import java.util.*;

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
