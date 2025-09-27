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
public class UserDao {

    private final Db dbAdmin;

    public UserDao(Db dbAdmin) {
        this.dbAdmin = dbAdmin;
    }


    //*************************************************************************//
    //***                   Методы групп пользователей                      ***//
    //*************************************************************************//

    public List<DbRec> loadGroup() throws Exception {
        return dbAdmin.loadSql("""
                    select * from AuthUserGr where 0=0
                """, null);
    }

    public DbRec insertGroup(DbRec rec) throws Exception {
        UtEntityData ue = new UtEntityData(dbAdmin, "AuthUserGr");
        long id = ue.getNextId("AuthUserGr");
        rec.put("id", id);
        dbAdmin.insertRec("AuthUserGr", rec);
        return dbAdmin.loadRec("AuthUserGr", id);
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
                    select id, authUserGr as authUserGr, accessLevel as accessLevel, login, email,
                        name, fullName as fullName, passwd, phone, locked, cmt
                    from AuthUser where authUserGr=:id
                """, Map.of("id", id));
    }

    public DbRec insertUser(DbRec rec) throws Exception {
        UtEntityData ue = new UtEntityData(dbAdmin, "AuthUser");
        if (!rec.getString("login").isEmpty()
                && !rec.getString("passwd").isEmpty()
                && !rec.getString("email").isEmpty()
                && !rec.getString("name").isEmpty()) {
            rec.putIfAbsent("accessLevel", 1L);
            rec.putIfAbsent("fullName", rec.getString("name"));
            rec.put("locked", 0);
            long id = ue.getNextId("AuthUser");
            rec.put("id", id);
            rec.put("passwd", UtString.md5Str(rec.getString("passwd")));
            dbAdmin.insertRec("AuthUser", rec);
            return dbAdmin.loadRec("AuthUser", id);
        } else {
            throw new XError("Заполните обязательные поля");
        }
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
