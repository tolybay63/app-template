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

    /**
     *
     * @param idsObj (id1, id2,...)
     * @param idsCls (id1, id2,...)
     * @return [{id:1, cls:1, name: 'n1', fullName: 'fn1'}, {id:2, cls:1, name: 'n2', fullName: 'fn2'}]
     * @throws Exception Exception
     */
    public List<DbRec> getObjInfo(String idsObj, String idsCls) throws Exception {
        String whe = idsCls.isEmpty() ? " o.id in "+idsObj : " o.cls in "+idsCls;
        return dbObject.loadSql("""
             select o.id, o.cls, v.name, v.fullName from Obj o, ObjVer v where o.id=v.ownerVer and
        """ + whe, null);
    }

    /**
     *
     * @param params Map, keys:
     *               1. ids - ids Owner,
     *               2. codProp - prop of ObjRef
     * @return [{id:1, cls:1, name: 'n1', fullName: 'fn1'}, {id:2, cls:1, name: 'n2', fullName: 'fn2'}]
     * @throws Exception Exception
     */
    public List<DbRec> getObjInfoFromData(DbRec params) throws Exception {
        return dbObject.loadSql("""
            select o.id, v.obj, ov.name, ov.fullName
            from Obj o
                left join DataProp d on d.objorrelobj=o.id and prop=:codProp
                left join DataPropVal v on d.id=v.dataProp
                left join ObjVer ov on v.obj=ov.ownerVer and ov.lastVer=1
            where o.id in
        """ + params.getString("ids"), params);
    }




}
