package kz.app.appobject.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class ObjectDao {

    private final Db dbObject;
    private final MetaDao metaService;

    public ObjectDao(@Qualifier("dbObject") Db dbObject, MetaDao metaService) {
        this.dbObject = dbObject;
        this.metaService = metaService;
    }


    public List<DbRec> loadObjectServed(long id) {
        //
        List<DbRec> res = new ArrayList<>();
        DbRec rec = new DbRec();
        rec.put("id", id);
        rec.put("name", "aaa_"+id);
        res.add(rec);
        //
        return res;
    }

    public List<DbRec> getObjInfo(String idsObj, String idsCls) throws Exception {
        String whe = idsCls.isEmpty() ? " o.id in "+idsObj : " o.cls in "+idsCls;
        return dbObject.loadSql("""
             select o.id, v.name, v.fullName from Obj o, ObjVer v where o.id=v.ownerVer and
        """ + whe, null);
    }

    public List<DbRec> getObjInfoFromData(DbRec params) throws Exception {
        return dbObject.loadSql("""
            select o.id, v.obj, ov.name
            from Obj o
                left join DataProp d on d.objorrelobj=o.id and prop=:Prop_Section
                left join DataPropVal v on d.id=v.dataProp
                left join ObjVer ov on v.obj=ov.ownerVer and ov.lastVer=1
            where o.id in
        """ + params.getString("ids"), params);
    }




}
