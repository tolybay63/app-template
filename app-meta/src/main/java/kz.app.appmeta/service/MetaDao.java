package kz.app.appmeta.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MetaDao {

    private final Db db;


    public MetaDao(Db db) {
        this.db = db;
    }


    public DbRec getIdFromCodOfEntity(String entity, String cod, String prefixcod) throws Exception {
        String sql = "select id, cod from " + entity + " where cod like :cod";
        if (!prefixcod.isEmpty()) {
            //prefixcod = prefixcod + "%";
            sql = "select id, cod from " + entity + " where cod like :prefixcod";
        }
        List<DbRec> st = db.loadSql(sql, Map.of("cod", cod, "prefixcod", prefixcod));
        if (st.isEmpty()) {
            String cd = cod.isEmpty() ? prefixcod : cod;
            throw new XError("NotFoundCod@{0}", cd);
        }
        DbRec map = new DbRec();
        for (DbRec r : st) {
            map.put(r.getString("cod"), r.getLong("id"));
        }
        return map;
    }


}
