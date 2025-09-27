package kz.app.appadmin.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtString;
import kz.app.appcore.utils.XError;
import kz.app.appdata.service.UtEntityData;
import kz.app.appdbtools.repository.Db;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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


}
