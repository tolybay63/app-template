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
            select * from AuthRole where 0=0
        """, null);
    }

    public DbRec insertRole(DbRec rec) throws Exception {
        UtEntityData ue = new UtEntityData(dbAdmin, "AuthRole");
        long id = ue.getNextId("AuthRole");
        rec.put("id", id);
        dbAdmin.insertRec("AuthRole", rec);
        return dbAdmin.loadRec("AuthRole", id);
    }

    public DbRec updateGroup(DbRec rec) throws Exception {
        dbAdmin.updateRec("AuthUserGr", rec);
        return rec;
    }

    public void deleteGroup(long id) throws Exception {
        dbAdmin.deleteRec("AuthUserGr", id);
    }

    //*************************************************************************//
    //***                          Методы пользователей                     ***//
    //*************************************************************************//
    public List<DbRec> loadUsers(long id) throws Exception {
        return dbAdmin.loadSql("""
            select * from AuthUser where authUserGr=:id
        """, Map.of("id", id));
    }

    public DbRec insertUser(DbRec rec) throws Exception {
        UtEntityData ue = new UtEntityData(dbAdmin, "AuthUser");
        if (rec.getString("login").isEmpty()
                && rec.getString("passwd").isEmpty()
                    && rec.getString("email").isEmpty()
                        && rec.getString("name").isEmpty()) {
            throw new XError("Заполните обязательные поля");
        }
        rec.putIfAbsent("accessLevel", 1L);
        rec.putIfAbsent("fullName", rec.getString("name"));
        rec.put("locked", 0);
        long id = ue.getNextId("AuthUser");
        rec.put("id", id);
        rec.put("passwd", UtString.md5Str(rec.getString("passwd")));
        dbAdmin.insertRec("AuthUser", rec);
        return dbAdmin.loadRec("AuthUser", id);
    }

    public DbRec updateUser(DbRec rec) throws Exception {
        dbAdmin.updateRec("AuthUser", rec);
        return rec;
    }

    public void deleteUser(long idUser) throws Exception {
        String str = checkAccount(idUser);
        if (!str.isEmpty()) {
            throw new XError("Существует аккаунт пользователя [{0}]", str);
        }
        dbAdmin.deleteRec("AuthUser", idUser);
    }

    private String checkAccount(long idUser) throws Exception {
        //...
        return "";
    }

}
