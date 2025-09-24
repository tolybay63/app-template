package kz.app.appadmin.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtString;
import kz.app.appcore.utils.XError;
import kz.app.appdata.service.UtEntityData;
import kz.app.appdbtools.repository.Db;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserDao {

    private final Db db;

    public UserDao(Db db) {
        this.db = db;
    }


    //*** Методы групп пользователей ***//
    public List<DbRec> loadGroup() throws Exception {
        return db.loadSql("""
            select * from AuthUserGr where 0=0
        """, null);
    }

    public DbRec insertGroup(DbRec rec) throws Exception {
        UtEntityData ue = new UtEntityData(db, "AuthUserGr");
        long id = ue.getNextId("AuthUserGr");
        rec.put("id", id);
        db.insertRec("AuthUserGr", rec);
        return db.loadRec("AuthUserGr", id);
    }

    public DbRec updateGroup(DbRec rec) throws Exception {
        db.updateRec("AuthUserGr", rec);
        return rec;
    }

    public void deleteGroup(long id) throws Exception {
        db.deleteRec("AuthUserGr", id);
    }

    //*** Методы пользователей ***//
    public List<DbRec> loadUsers(long id) throws Exception {
        return db.loadSql("""
            select * from AuthUser where authUserGr=:id
        """, Map.of("id", id));
    }

    public DbRec insertUser(DbRec rec) throws Exception {
        UtEntityData ue = new UtEntityData(db, "AuthUser");
        long id = ue.getNextId("AuthUser");
        rec.put("id", id);
        rec.put("passwd", UtString.md5Str(rec.getString("passwd")));
        db.insertRec("AuthUser", rec);
        return db.loadRec("AuthUser", id);
    }

    public DbRec updateUser(DbRec rec) throws Exception {
        db.updateRec("AuthUser", rec);
        return rec;
    }

    public void deleteUser(long idUser) throws Exception {
        String str = checkAccount(idUser);
        if (!str.isEmpty()) {
            throw new XError("Существует аккаунт пользователя [{0}]", str);
        }
        db.deleteRec("AuthUser", idUser);
    }

    private String checkAccount(long idUser) throws Exception {
        //...
        return "";
    }

}
