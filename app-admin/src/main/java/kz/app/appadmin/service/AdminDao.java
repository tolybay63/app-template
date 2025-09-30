package kz.app.appadmin.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 *
 */
@Component
public class AdminDao {


    private final Db dbAdmin;
    private final MetaDao metaService;

    public AdminDao(Db dbAdmin, MetaDao metaService) {
        this.dbAdmin = dbAdmin;
        this.metaService = metaService;
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
