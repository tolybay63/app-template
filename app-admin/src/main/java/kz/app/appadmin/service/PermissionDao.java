package kz.app.appadmin.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class PermissionDao {
    private final Db dbAdmin;


    public PermissionDao(Db dbAdmin) {
        this.dbAdmin = dbAdmin;
    }

    public List<DbRec> loadPermissions() throws Exception {
        return dbAdmin.loadSql("""
            select * from Permis where 0=0 order by ord
        """, null);
    }

    private void validateRec(String id) throws Exception {
        List<DbRec> stTmp = dbAdmin.loadSql("""
                select r.name
                from AuthRolePermis p
                	left join authrole r on p.authrole=r.id
                where p.permis=:id
        """, Map.of("id", id));
        if (!stTmp.isEmpty()) {
            throw new XError("Используется в роли [{0}]", stTmp.getFirst().getString("name"));
        }

        stTmp = dbAdmin.loadSql("""
            select r.fullname
            from AuthUserPermis p
            left join authuser r on p.authuser=r.id
            where p.permis =:id
        """, Map.of("id", id));
        if (!stTmp.isEmpty()) {
            throw new XError("Используется в привилегии пользователя [{0}]", stTmp.getFirst().getString("fullname"));
        }
    }

    public void delete(DbRec rec) throws Exception {
        validateRec(rec.getString("id"));

        String sql = """
            delete from Permis where id=:id;
        """;
        dbAdmin.execSql(sql, Map.of("id", rec.getString("id")));
    }

    public DbRec update(DbRec rec) throws Exception {
        dbAdmin.updateRec("Permis", rec);
        return dbAdmin.loadRec("Permis", rec.getLong("id"));
    }

    public DbRec insert(DbRec rec) throws Exception {
        String sql = """
            insert into Permis (id, parent, text, ord)
            values (:id, :parent, :text, :ord)
        """;

        int ord = dbAdmin.loadSql("select max(ord) as max from Permis", null)
                .getFirst().getInt("max");

        rec.put("ord", ord+1);
        dbAdmin.execSql(sql, rec);
        //
        return dbAdmin.loadRec("Permis", rec.getLong("id"));
    }

    public Set<String> getLeaf(String id) throws Exception {
        List<DbRec> st = dbAdmin.loadSql("select * from Permis", null);
        Map<String, DbRec> map = getMapping(st);
        Set<String> leaf = new HashSet<String>();

        DbRec record = map.get(id);

        while (true) {
            if (record != null) {
                leaf.add(record.getString("id"));
                record = map.get(record.getString("parent"));
            } else {
                break;
            }
        }
        return leaf;
    }

    private Map<String, DbRec> getMapping(List<DbRec> lst) {
        Map<String, DbRec> res = new HashMap<>();
        for (DbRec map : lst) {
            res.put(map.getString("id"), map);
        }
        return res;
    }
}
