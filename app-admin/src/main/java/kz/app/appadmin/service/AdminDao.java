package kz.app.appadmin.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// dao utils

/**
 *
 */
@Component
public class AdminDao {


    private final Db dbAdmin;

    public AdminDao(Db dbAdmin) {
        this.dbAdmin = dbAdmin;
    }



    public List<DbRec> loadUsers() throws Exception {

        return dbAdmin.loadSql("""
            select u.id, u.login, u.name, u.fullName, u.email, u.phone,
                u.authUserGr, g.name as nameGroup, u.cmt, g.cmt as cmtGroup
            from AuthUserGr g
            left join AuthUser u on g.id=u.authUserGr
            where 0=0
        """, null);
    }


}
