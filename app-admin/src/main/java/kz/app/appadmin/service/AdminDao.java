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


    private final Db db;

    public AdminDao(Db db) {
        this.db = db;
    }


    public List<DbRec> loadGroup() throws Exception {
        return db.loadSql("""
            select * from AuthUserGr where 0=0
        """, null);
    }

    public List<DbRec> loadUsers(long idGroup) throws Exception {

        return db.loadSql("""
            select * from AuthUser where authUserGr=:id
        """, Map.of("id", idGroup));
    }


}
