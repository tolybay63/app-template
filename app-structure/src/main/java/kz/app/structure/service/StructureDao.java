package kz.app.structure.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StructureDao {

    private final Db dbStructure;
    private final MetaDao metaService;

    public StructureDao(@Qualifier("dbStructure") Db dbStructure, MetaDao metaService) {
        this.dbStructure = dbStructure;
        this.metaService = metaService;
    }

    public List<DbRec> loadObjTreeForSelect(String codTyp, String codProp) throws Exception {
        String idsCls = metaService.getIdsCls(codTyp);

        List<DbRec> st = dbStructure.loadSql("""
            select o.id, v.name, v.objParent as parent, o.cls, 0 as pv
            from Obj o
                left join ObjVer v on o.id=v.ownerVer and v.lastVer=1
            where o.cls in
        """ + idsCls + " order by v.name", null);

        for (DbRec r : st) {
            Set<Long> setPV = metaService.getIdsPV(1, r.getLong("cls"), codProp);
            if ((Long) setPV.toArray()[0]==0L)
                throw new XError("Для класса [{0}] и пропа [{1}] не найден propVal", r.getLong("cls"), codProp);
            r.put("pv", setPV.toArray()[0]);
        }


        return st;
    }


    public DbRec getObjRec(long id) throws Exception {
        return dbStructure.loadRec("Obj", id, false);
    }

    public Set<Object> getIdsObjLocation(long obj) throws Exception {
        List<DbRec> st = dbStructure.loadSql("""
                   WITH RECURSIVE r AS (
                       SELECT o.id, v.objParent as parent
                       FROM Obj o, ObjVer v
                       WHERE o.id=v.ownerVer and v.lastVer=1 and v.objParent=:obj
                       UNION ALL
                       SELECT t.*
                       FROM ( SELECT o.id, v.objParent as parent
                              FROM Obj o, ObjVer v
                              WHERE o.id=v.ownerVer and v.lastVer=1
                            ) t
                          JOIN r
                              ON t.parent = r.id
                   ),
                   o as (
                   SELECT o.id, v.objParent as parent
                   FROM Obj o, ObjVer v
                   WHERE o.id=v.ownerVer and v.lastVer=1 and o.id=:obj
                   )
                   SELECT * FROM o
                   UNION ALL
                   SELECT * FROM r
                   where 0=0
                """, Map.of("obj", obj));

        return st.stream()
                .map(map -> (Long) map.get("id"))
                .collect(Collectors.toSet());
    }

    public List<DbRec> objIdName(String idsObj, String idsCls) throws Exception {
        String whe = idsCls.isEmpty() ? " o.id in "+idsObj : " o.cls in "+idsCls;
        return dbStructure.loadSql("""
                     select o.id, v.name, v.fullName from Obj o, ObjVer v where o.id=v.ownerVer and
                """ + whe, null);
    }


}
