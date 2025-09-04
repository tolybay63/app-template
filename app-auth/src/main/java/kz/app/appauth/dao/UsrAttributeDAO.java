package kz.app.appauth.dao;

import kz.app.appauth.persistance.constant.*;
import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appdbtools.repository.Db;
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