package kz.app.appadmin.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtString;
import kz.app.appcore.utils.XError;
import kz.app.appcore.utils.consts.FD_AccessLevel_consts;
import kz.app.appdata.service.UtEntityData;
import kz.app.appdbtools.repository.Db;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public List<DbRec> loadGroupForSelect(long group) throws Exception {

        //todo AuthUser au = authSvc.getCurrentUser();
        //long al = au.getAttrs().getLong("accessLevel");

        long al = 10;
        List<DbRec> st = dbAdmin.loadSql("""
                    select * from authUserGr
                    where (id in (
                        select authUserGr from authUser
                        where accessLevel <= :al
                        ) or id not in (
                        select authUserGr from authUser
                        where accessLevel <= :al
                        )) and id <> :id
                """, Map.of("al", al, "id", group));

        Set<Object> ids = UtDb.uniqueValues(st, "id");
        st.forEach(r -> {
            if (!ids.contains(r.getLong("parent")))
                r.put("parent", null);
        });
        return st;
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

    public void deleteGroup(long group) throws Exception {
        dbAdmin.deleteRec("AuthUserGr", group);
    }

    //*************************************************************************//
    //***                          Методы пользователей                     ***//
    //*************************************************************************//

    public DbRec newRec(long gr) throws Exception {
        DbRec rec = new DbRec();
        rec.put("authUserGr", gr);
        rec.put("locked", 0);
        rec.put("accessLevel", FD_AccessLevel_consts.common);
        return rec;
    }

    public List<DbRec> loadUsers(long group) throws Exception {
        return dbAdmin.loadSql("""
                    select id, authUserGr as authUserGr, accessLevel as accessLevel, login, email,
                        name, fullName as fullName, passwd, phone, locked, cmt
                    from AuthUser where authUserGr=:id
                """, Map.of("id", group));
    }

    public DbRec loadUser(long user) throws Exception {
        return dbAdmin.loadSqlRec("""
                    select id, authUserGr as authUserGr, accessLevel as accessLevel, login, email,
                        name, fullName as fullName, passwd, phone, locked, cmt
                    from AuthUser where id=:id
                """, Map.of("id", user));
    }

    public DbRec insertUser(DbRec rec) throws Exception {
        UtEntityData ue = new UtEntityData(dbAdmin, "AuthUser");
        if (!rec.getString("login").isEmpty()
                && !rec.getString("passwd").isEmpty()
                && !rec.getString("email").isEmpty()
                && !rec.getString("name").isEmpty()) {
            rec.putIfAbsent("accessLevel", 1L);
            rec.putIfAbsent("fullName", rec.getString("name"));
            rec.remove("psw2");
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

    //Permissions

    public List<DbRec> loadUserPermissions(long user) throws Exception {
        return dbAdmin.loadSql("""
            with a as (
                select permis, accessLevel from AuthUserPermis where authUser=:user
            )
            select p.*, a.accessLevel from Permis p, a where p.id=a.permis order by p.ord
        """, Map.of("user", user));
    }

    public List<DbRec> loadUserPermissionsForUpd(long user) throws Exception {
        return dbAdmin.loadSql("""
            select p.*, a.id as idInTable, a.accessLevel,
             case when a.id is null then false else true end as checked
            from permis p
            left join AuthUserPermis a on p.id=a.permis and a.authUser=:user
            order by p.ord
        """, Map.of("user", user));
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void saveUserPermissions(Map<String, Object> params) throws Exception {
        long user = UtCnv.toLong(params.get("user"));
        List<Map<String, Object>> lstData = (List<Map<String, Object>>) params.get("data");

        //Old ids
        List<DbRec> oldSt = dbAdmin.loadSql("select id from AuthUserPermis where authUser=:u", Map.of("u", user));
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
                    dbAdmin.deleteRec("AuthUserPermis", r.getLong("id"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        // Saving
        UtEntityData ue = new UtEntityData(dbAdmin, "AuthUserPermis");
        for (Map<String, Object> map : lstData) {
            DbRec r = new DbRec();
            if (!oldIds.contains(UtCnv.toLong(map.get("idInTable")))) {
                long id = ue.getNextId("AuthUserPermis");
                r.put("id", id);
                r.put("authUser", user);
                r.put("permis", UtCnv.toString(map.get("id")));
                r.put("accessLevel", UtCnv.toLong(map.get("accessLevel")));
                dbAdmin.insertRec("AuthUserPermis", r);
            } else {
                r.put("id", UtCnv.toLong(map.get("idInTable")));
                r.put("authUser", user);
                r.put("permis", UtCnv.toString(map.get("id")));
                r.put("accessLevel", UtCnv.toLong(map.get("accessLevel")));
                dbAdmin.updateRec("AuthUserPermis", r);
            }
        }

    }

    //Roles

    public List<DbRec> loadUserRoles(long user) throws Exception {
        return dbAdmin.loadSql("""
            select r.id, r.name, r.fullname as fullName, r.cmt
            from AuthRoleUser u
                left join AuthRole r on u.authRole=r.id
            where u.authUser=:user
        """, Map.of("user", user));
    }

    public List<DbRec> loadUserRolesForUpd(long user) throws Exception {
        return dbAdmin.loadSql("""
            select r.id, r.name, r.fullname as fullName, r.cmt,
                case when u.id is null then false else true end as checked
            from AuthRole r
                left join AuthRoleUser u on r.id=u.authRole and u.authUser=:user
        """, Map.of("user", user));
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void saveUserRoles(Map<String, Object> params) throws Exception {
        long user = UtCnv.toLong(params.get("user"));
        List<Map<String, Long>> data = (List<Map<String, Long>>) params.get("dta");


        //long user, List<Map<String, Long>> data
        //Old ids : id(AuthRoleUser)
        List<DbRec> oldSt = dbAdmin.loadSql("select id, authRole from AuthRoleUser where authUser=:u", Map.of("u", user));
        Set<Object> oldIds = UtDb.uniqueValues(oldSt, "authRole");

        //New ids
        Set<Object> newIds = new HashSet<>();
        for (Map<String, Long> map : data) {
            newIds.add(UtCnv.toLong(map.get("id")));
        }

        //Deleting
        for (DbRec r : oldSt) {
            if (!newIds.contains(r.getLong("authRole"))) {
                try {
                    dbAdmin.deleteRec("AuthRoleUser", r.getLong("id"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        // Saving
        UtEntityData ue = new UtEntityData(dbAdmin, "AuthRoleUser");
        for (Map<String, Long> map : data) {
            DbRec r = new DbRec();
            if (!oldIds.contains(UtCnv.toLong(map.get("id")))) {
                long id = ue.getNextId("AuthRoleUser");
                r.put("id", id);
                r.put("authUser", user);
                r.put("authRole", UtCnv.toLong(map.get("id")));
                r.put("ord", id);
                dbAdmin.insertRec("AuthRoleUser", r);
            }
        }
    }

    //************* todo
    private String checkAccount(long idUser) throws Exception {
        //...
        return "";
    }

}
