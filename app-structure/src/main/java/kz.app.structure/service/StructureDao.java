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

    public StructureDao(Db dbStructure, MetaDao metaService) {
        this.dbStructure = dbStructure;
        this.metaService = metaService;
    }


    public List<DbRec> getObj(long cls) throws Exception {
        List<DbRec> st = dbStructure.loadSql("""
            select * from Cls where cls=:cls
        """, Map.of("cls", cls));
        if (st.isEmpty()) {
            throw new XError("Not fount Object (cls={0})", cls);
        }
        return st;
    }


    public Set<Object> getIdsObjLocation(long obj) {
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


}
