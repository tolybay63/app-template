package kz.app.appadmin.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appmeta.service.MetaDao;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
@Component
public class DictDao {


    //private final Db dbAdmin;
    private final MetaDao metaService;

    public DictDao(/*Db dbAdmin,*/ MetaDao metaService) {
        //this.dbAdmin = dbAdmin;
        this.metaService = metaService;
    }

    public Map<Long, String> loadDict(String dictName) throws Exception {
        long al = getAccessLevel();
        List<DbRec> st = metaService.loadDict(dictName, al);
        Map<Long, String> map = new HashMap<>();
        for (DbRec r : st) {
            map.put(r.getLong("id"), r.getString("text"));
        }
        return map;

    }

    private long getAccessLevel() throws Exception {
        long al = 1;

/*
        long al = mdb.getApp().bean(AuthService.class)
                .getCurrentUser()
                .getAttrs()
                .getLong("accessLevel")
*/

        if (al == 0)
            throw new XError("notLogined");
        return al;
    }

}
