package kz.app.object.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ObjectDao {

    private final Db dbObject;
    private final MetaDao metaService;

    public ObjectDao(@Qualifier("dbObject") Db dbObject, MetaDao metaService) {
        this.dbObject = dbObject;
        this.metaService = metaService;
    }

    public List<DbRec> getObjInfo(String idsCls) throws Exception {
        return dbObject.loadSql("""
            select o.id, o.cls, v.fullName, null as nameClsObject
            from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.cls in
        """+idsCls, null);
    }



}
