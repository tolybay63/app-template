package kz.app.appplan.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlanDao {
    private final Db db;
    private final MetaDao metaService;

    public PlanDao(Db db, MetaDao metaService) {
        this.db = db;
        this.metaService = metaService;
    }


    public List<DbRec> loadPlan(DbRec params) throws Exception {
        List<DbRec> st = metaService.getCls("Typ_WorkPlan");
        Set<Long> ids = st.stream()
                .map(map -> (Long) map.get("id"))
                .collect(Collectors.toSet());
        String whe = "(" + ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + ")";




        return db.loadSql("""
            select o.*, v.name, v.fullName, v.dbeg, v.dend from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.cls in
        """+whe+" order by o.id", null);


    }

}
