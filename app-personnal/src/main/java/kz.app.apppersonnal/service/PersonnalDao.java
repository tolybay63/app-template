package kz.app.apppersonnal.service;

import kz.app.appcore.model.DbRec;
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

    public List<DbRec> getObjInfo(String idsCls) throws Exception {
        return dbPersonnal.loadSql("""
            select o.id, o.cls, v.name, v.fullName, null as nameCls
            from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.cls in
        """+idsCls, null);
    }

    public List<DbRec> getObjList(long cls) throws Exception {
        return dbPersonnal.loadSql("""
            select o.id, o.cls, v.name, v.fullName, null as nameCls
            from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.cls=:cls
        """, Map.of("cls", cls));
    }


}
