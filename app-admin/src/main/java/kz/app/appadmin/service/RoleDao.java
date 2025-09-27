package kz.app.appadmin.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtString;
import kz.app.appcore.utils.XError;
import kz.app.appdata.service.UtEntityData;
import kz.app.appdbtools.repository.Db;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RoleDao {

    private final Db dbAdmin;

    public RoleDao(Db dbAdmin) {
        this.dbAdmin = dbAdmin;
    }


    //*************************************************************************//
    //***                   Методы групп пользователей                      ***//
    //*************************************************************************//

    public List<DbRec> loadRoles() throws Exception {
        return dbAdmin.loadSql("""
            select id, name, fullName as fullName, cmt from AuthRole where 0=0
        """, null);
    }

    public DbRec insertRole(DbRec rec) throws Exception {
        UtEntityData ue = new UtEntityData(dbAdmin, "AuthRole");
        long id = ue.getNextId("AuthRole");
        rec.put("id", id);
        rec.putIfAbsent("fullName", rec.getString("name"));
        dbAdmin.insertRec("AuthRole", rec);
        return dbAdmin.loadRec("AuthRole", id);
    }

    public DbRec updateRole(DbRec rec) throws Exception {
        dbAdmin.updateRec("AuthRole", rec);
        return rec;
    }

    public void deleteRole(long id) throws Exception {
        dbAdmin.deleteRec("AuthRole", id);
    }

    //**** Permissions

    public String getPermissions(long role) throws Exception {
        List<DbRec> st = dbAdmin.loadSql("""
            select p.text from AuthRolePermis r, Permis p where r.authRole=:id and r.permis=p.id
            order by p.ord
        """, Map.of("id", role));

        Set<String> set = UtDb.uniqueValues( st,"text");
        return UtString.join(set, "; ");
    }

    public List<DbRec> loadRolePermissions(long role) throws Exception {
        return dbAdmin.loadSql("""
            with a as (
                select permis from AuthRolePermis where authRole=:role
            )
            select p.* from Permis p, a where p.id=a.permis order by p.ord
        """, Map.of("role", role));
    }

    public List<DbRec> loadRolePermissionsForUpd(long role) throws Exception {
        return dbAdmin.loadSql("""
            select p.*, a.id as idInTable, case when a.id is null then false else true end as checked
            from permis p
            left join AuthRolePermis a on p.id=a.permis and a.authRole=:role
            order by p.ord
        """, Map.of("role", role));
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void saveRolePermis(Map<String, Object> params) throws Exception {
        long role = UtCnv.toLong(params.get("role"));
        List<Map<String, Object>> lstData = (List<Map<String, Object>>) params.get("data");

        //Old ids
        List<DbRec> oldSt = dbAdmin.loadSql("select id from AuthRolePermis where authRole=:r", Map.of("r", role));
        Set<Object> oldIds = UtDb.uniqueValues(oldSt, "id");

        //New ids
        Set<Object> newIds = new HashSet<>();
        for (Map<String, Object> map : lstData) {
            newIds.add(UtCnv.toLong(map.get("idInTable")));
        }
        //Deleting
        for (DbRec r : oldSt) {
            if (!newIds.contains(r.getLong("id"))) {
                try {
                    dbAdmin.deleteRec("AuthRolePermis", r.getLong("id"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        // Saving
        UtEntityData ue = new UtEntityData(dbAdmin, "AuthRolePermis");
        for (Map<String, Object> map : lstData) {
            DbRec r = new DbRec();
            if (!oldIds.contains(UtCnv.toLong(map.get("idInTable")))) {
                long id = ue.getNextId("AuthRolePermis");
                r.put("id", id);
                r.put("authRole", role);
                r.put("permis", UtCnv.toString(map.get("id")));
                dbAdmin.insertRec("AuthRolePermis", r);
            } else {
                r.put("id", UtCnv.toLong(map.get("idInTable")));
                r.put("authRole", role);
                r.put("permis", UtCnv.toString(map.get("id")));
                dbAdmin.updateRec("AuthRolePermis", r);
            }
        }

    }


}
