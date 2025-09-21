package kz.app.structure.service;

import kz.app.appcore.model.DbRec;
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
