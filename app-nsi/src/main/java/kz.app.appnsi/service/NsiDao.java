package kz.app.appnsi.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NsiDao {
    private final Db dbNsi;
    private final MetaDao metaService;

    public NsiDao(@Qualifier("dbNsi") Db dbNsi, MetaDao metaService) {
        this.dbNsi = dbNsi;
        this.metaService = metaService;
    }

    public List<DbRec> loadDefects(long obj) {


        return null;
    }

    public List<DbRec> getObjInfo(String idsCls) throws Exception {
        return dbNsi.loadSql("""
            select o.id, o.cls, v.fullName, null as nameClsWork
            from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.cls in
        """+idsCls, null);
    }
}
