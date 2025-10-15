package kz.app.appmeta.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UtEntityMeta {
    Db db;
    String tableName;

    public UtEntityMeta(Db db, String tableName) {
        this.db = db;
        this.tableName = tableName;
    }

    public long getNextId(String tableName) throws Exception {
        String sql = "SELECT nextval('g_"+tableName+"') as id";
        List<DbRec> lst = db.loadSql(sql, null);
        return lst.getFirst().getLong("id");
    }

    private boolean existsField(String tableName, String fieldName) throws Exception {
        return db.loadSql("""
            SELECT exists (
                SELECT 1
                FROM information_schema.columns
                WHERE table_schema = 'public'
                AND table_name = :tableName
                AND column_name = :fieldName
            )
        """, Map.of("tableName", tableName.toLowerCase(), "fieldName", fieldName.toLowerCase())).getFirst().getBoolean("exists");
    }

    public DbRec setDomain(String tableName, DbRec params) throws Exception {
        DbRec rec = new DbRec();
        List<DbRec> st = db.loadSql("""
            SELECT column_name
            FROM information_schema.columns
            WHERE table_schema = 'public'
            AND table_name=:tb
        """, Map.of("tb", tableName.toLowerCase()));
        List<String> lst = new ArrayList<>();
        for (DbRec r : st) {
            lst.add(r.getString("column_name"));
        }
        for (String key : params.keySet()) {
            if (lst.contains(key.toLowerCase())) {
                rec.put(key, params.get(key));
            }
        }
        return rec;
    }

    public long insertEntity(DbRec params) throws Exception {
        if (!params.getString("tableName").isEmpty())
            tableName = params.getString("tableName");

        DbRec rec = setDomain(tableName, params);
        rec.putIfAbsent("accessLevel", 1L);
        String cod = rec.getString("cod");
        //
        throw new XError("Not implemented");
        //
    }

    public void deleteEntity(long id) throws Exception {
        //
        throw new XError("Not implemented");
        //
    }

    public void updateEntity(DbRec params) throws Exception {
        //
        throw new XError("Not implemented");
        //
    }





}
