package kz.app.appplan.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtPeriod;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.structure.service.StructureDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlanDao {
    private final Db dbPlan;
    private final MetaDao metaService;
    private final StructureDao structureService;

    public PlanDao(Db dbPlan, MetaDao metaService, StructureDao structureService) {
        this.dbPlan = dbPlan;
        this.metaService = metaService;
        this.structureService = structureService;
    }


    public List<DbRec> loadPlan(DbRec params) throws Exception {
        List<DbRec> st = metaService.getCls("Typ_WorkPlan");
        Set<Long> ids = st.stream()
                .map(map -> (Long) map.get("id"))
                .collect(Collectors.toSet());
        String whe = "(" + ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + ")";
        String wheV1 = "";
        String wheV7 = "";
        //
        Map<Long, DbRec> mapClsWork = new HashMap<>();
        for (DbRec map : st) {
            Long id = map.getLong("id");
            mapClsWork.put(id, map);
        }
        //
        DbRec paramSql = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");
        //
        if (params.containsKey("id"))
            whe = "o.id="+params.getLong("id");
        else{
            whe = "o.cls in " + whe;

            DbRec mapCls = metaService.getIdFromCodOfEntity("Cls", "Cls_LocationSection", "");
            List<DbRec> stLocation = structureService.getObj(mapCls.getLong("Cls_LocationSection"));
            long clsLocation = stLocation.getFirst().getLong("id");

            if (clsLocation == params.getLong("Cls_LocationSection")) {
                Set<Object> idsObjLocation = structureService.getIdsObjLocation(params.getLong("objLocation"));
                String wheIds = "(" + idsObjLocation.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + ")";

                wheV1 = "and v1.obj in " + wheIds;
            }
            long pt = params.getLong("periodType");
            String dte = params.getString("date");
            UtPeriod utPeriod = new UtPeriod();
            LocalDate d1 = utPeriod.calcDbeg(LocalDate.parse(dte, DateTimeFormatter.ISO_DATE), pt, 0);
            LocalDate  d2 = utPeriod.calcDend(LocalDate.parse(dte, DateTimeFormatter.ISO_DATE), pt, 0);
            wheV7 = "and v7.dateTimeVal between :d1 and :d2";
            //
            paramSql.put("d1", d1);
            paramSql.put("d2", d2);
        }





        // temporary
        return dbPlan.loadSql("""
            select o.*, v.name, v.fullName, v.dbeg, v.dend from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.cls in
        """+whe+" order by o.id", null);


    }

}
