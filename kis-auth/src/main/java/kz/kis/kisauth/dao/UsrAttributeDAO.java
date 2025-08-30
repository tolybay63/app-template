package kz.kis.kisauth.dao;

import kz.kis.kisauth.persistance.constant.*;
import kz.kis.kiscore.model.*;
import kz.kis.kiscore.utils.*;
import kz.kis.kisdbtools.repository.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@AllArgsConstructor
public class UsrAttributeDAO {

    private final Db db;

    public void setAttrOwnUser(Long usr, boolean ownUser) throws Exception {

        String value = UtCnv.toString(UtCnv.toLong(ownUser));
        Map<String, Object> params = UtCnv.toMap("usr", usr, "attrType", UsrAttributeType.OWN_USER);
        DbRec rec = db.loadRec("UsrAttributes",  params);

        params.put("value", value);

        if (rec == null) {
            db.insertRec("UsrAttributes", params);
        } else {
            params.put("id", rec.getLong("id"));
            db.updateRec("UsrAttributes", params);
        }
    }
}