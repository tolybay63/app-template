package kz.app.apppersonnal.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class PersonnalDao {

    private final Db dbPersonnal;
    private final MetaDao metaService;

    public PersonnalDao(@Qualifier("dbPersonnal") Db dbPersonnal, MetaDao metaService) {
        this.dbPersonnal = dbPersonnal;
        this.metaService = metaService;
    }

    public List<DbRec> getObjInfo(String idsObj, String idsCls) throws Exception {
        String whe = idsCls.isEmpty() ? " o.id in "+idsObj : " o.cls in "+idsCls;
        return dbPersonnal.loadSql("""
             select o.id, o.cls, v.name, v.fullName from Obj o, ObjVer v where o.id=v.ownerVer and
        """ + whe, null);
    }

    public List<DbRec> getObjList(long cls) throws Exception {
        return dbPersonnal.loadSql("""
            select o.id, o.cls, v.name, v.fullName, null as nameCls
            from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.cls=:cls
        """, Map.of("cls", cls));
    }

    //todo Метод должен быть во всех data-сервисах
    public List<DbRec> getRefData(int isObj, long owner, String whePV) throws Exception {
        return dbPersonnal.loadSql("""
                    select d.id from DataProp d, DataPropVal v
                    where d.id=v.dataProp and d.isObj=:isObj and v.propVal in
        """ + whePV + " and obj=:owner", Map.of("isObj", isObj, "owner", owner));
    }

    public void checkUser(long id) throws Exception {
        DbRec map = metaService.getIdFromCodOfEntity("Prop", "Prop_UserId", "");
        //map.put("id", id);
        List<DbRec> lst = dbPersonnal.loadSql("""
            select v.fullname as name
            from Obj o
            left join ObjVer v on o.id=v.ownerVer and v.lastVer=1
            left join DataProp d1 on d1.objorrelobj=o.id and d1.prop=:Prop_UserId
            inner join DataPropVal v1 on d1.id=v1.dataProp and v1.strVal=
        '"""+id+"'", map);
        if (!lst.isEmpty()) {
            throw new XError("Существует аккаунт пользователя [{0}]", lst.getFirst().getString("name"));
        }
    }


}
