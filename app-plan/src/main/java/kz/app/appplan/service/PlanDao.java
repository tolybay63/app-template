package kz.app.appplan.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
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

        DbRec pms = metaService.getIdFromCodOfEntity("Typ", "Typ_WorkPlan", "");
        List<DbRec> stCls = db.loadSql("""
            select c.id , v.name
            from Cls c, ClsVer v
            where c.id=v.ownerVer and v.lastVer=1 and typ=:Typ_WorkPlan
        """, pms);
        //Set<Object> idsCls = stCls.getUniqueValues("id");
        Set<Long> idsCls = stCls.stream()
                .map(map -> (Long) map.get("id"))
                .collect(Collectors.toSet());
        String wheCls = idsCls.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));





        return stCls;
    }

}
