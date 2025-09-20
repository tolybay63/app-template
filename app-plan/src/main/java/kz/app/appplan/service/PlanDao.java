package kz.app.appplan.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtPeriod;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.appnsi.service.NsiDao;
import kz.app.object.service.ObjectDao;
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
    private final NsiDao nsiService;
    private final ObjectDao objectService;

    public PlanDao(Db dbPlan, MetaDao metaService, StructureDao structureService, NsiDao nsiService, ObjectDao objectService) {
        this.dbPlan = dbPlan;
        this.metaService = metaService;
        this.structureService = structureService;
        this.nsiService = nsiService;
        this.objectService = objectService;
    }


    public List<DbRec> loadPlan(DbRec params) throws Exception {
        List<DbRec> st = metaService.getCls("Typ_WorkPlan");
        Set<Long> ids = st.stream()
                .map(map -> (Long) map.get("id"))
                .collect(Collectors.toSet());
        String whe = "(" + ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + ")";
        String wheV1 = " ";
        String wheV7 = " ";
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
            whe = " o.id=" + params.getLong("id");
        else {
            whe = " o.cls in " + whe;

            DbRec mapCls = metaService.getIdFromCodOfEntity("Cls", "Cls_LocationSection", "");
            DbRec objRec = structureService.getObjRec(params.getLong("objLocation"));
            long clsLocation = objRec.getLong("cls");

            if (clsLocation == mapCls.getLong("Cls_LocationSection")) {
                Set<Object> idsObjLocation = structureService.getIdsObjLocation(params.getLong("objLocation"));
                String wheIds = "(" + idsObjLocation.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + ")";
                if (wheIds.equals("()")) {
                    wheIds = "(0) ";
                }

                wheV1 = " and v1.obj in " + wheIds;
            }
            long pt = params.getLong("periodType");
            //todo
            if (pt == 0) pt = 11;
            String dte = params.getString("date");
            if (dte.isEmpty()) {
                dte = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            }
            UtPeriod utPeriod = new UtPeriod();
            LocalDate d1 = utPeriod.calcDbeg(LocalDate.parse(dte, DateTimeFormatter.ISO_DATE), pt, 0);
            LocalDate d2 = utPeriod.calcDend(LocalDate.parse(dte, DateTimeFormatter.ISO_DATE), pt, 0);
            wheV7 = " and v7.dateTimeVal between :d1 and :d2";
            //
            paramSql.put("d1", d1);
            paramSql.put("d2", d2);
        }

        String sqlPlan = """
                select o.id, o.cls, v.name, null as nameCls,
                    v1.id as idLocationClsSection, v1.propVal as pvLocationClsSection,
                        v1.obj as objLocationClsSection, null as nameLocationClsSection,
                    v2.id as idObject, v2.propVal as pvObject, v2.obj as objObject,
                        null as nameClsObject, null as fullNameObject,
                    v3.id as idStartKm, v3.numberVal as StartKm,
                    v4.id as idFinishKm, v4.numberVal as FinishKm,
                    v5.id as idStartPicket, v5.numberVal as StartPicket,
                    v6.id as idFinishPicket, v6.numberVal as FinishPicket,
                    v7.id as idPlanDateEnd, v7.dateTimeVal as PlanDateEnd,
                    v8.id as idUser, v8.propVal as pvUser, v8.obj as objUser, null as fullNameUser,
                    v9.id as idCreatedAt, v9.dateTimeVal as CreatedAt,
                    v10.id as idUpdatedAt, v10.dateTimeVal as UpdatedAt,
                    v11.id as idWork, v11.propVal as pvWork, v11.obj as objWork,
                        null as nameClsWork, null as fullNameWork
                from Obj o
                    left join ObjVer v on o.id=v.ownerver and v.lastver=1
                    left join DataProp d1 on d1.objorrelobj=o.id and d1.prop=:Prop_LocationClsSection
                    inner join DataPropVal v1 on d1.id=v1.dataprop """ + wheV1 + """
                left join DataProp d2 on d2.objorrelobj=o.id and d2.prop=:Prop_Object
                left join DataPropVal v2 on d2.id=v2.dataprop
                left join DataProp d3 on d3.objorrelobj=o.id and d3.prop=:Prop_StartKm
                left join DataPropVal v3 on d3.id=v3.dataprop
                left join DataProp d4 on d4.objorrelobj=o.id and d4.prop=:Prop_FinishKm
                left join DataPropVal v4 on d4.id=v4.dataprop
                left join DataProp d5 on d5.objorrelobj=o.id and d5.prop=:Prop_StartPicket
                left join DataPropVal v5 on d5.id=v5.dataprop
                left join DataProp d6 on d6.objorrelobj=o.id and d6.prop=:Prop_FinishPicket
                left join DataPropVal v6 on d6.id=v6.dataprop
                left join DataProp d7 on d7.objorrelobj=o.id and d7.prop=:Prop_PlanDateEnd
                inner join DataPropVal v7 on d7.id=v7.dataprop""" + wheV7 +
                """
                            left join DataProp d8 on d8.objorrelobj=o.id and d8.prop=:Prop_User
                            left join DataPropVal v8 on d8.id=v8.dataprop
                            left join DataProp d9 on d9.objorrelobj=o.id and d9.prop=:Prop_CreatedAt
                            left join DataPropVal v9 on d9.id=v9.dataprop
                            left join DataProp d10 on d10.objorrelobj=o.id and d10.prop=:Prop_UpdatedAt
                            left join DataPropVal v10 on d10.id=v10.dataprop
                            left join DataProp d11 on d11.objorrelobj=o.id and d11.prop=:Prop_Work
                            left join DataPropVal v11 on d11.id=v11.dataprop
                        where""" + whe;

        List<DbRec> stPlan = dbPlan.loadSql(sqlPlan, paramSql);

        //... Пересечение
        String wheCls = metaService.getIdsCls("Typ_Location");
        //select o.id, v.name from Obj o, ObjVer v
        List<DbRec> stLocation = structureService.objIdName(wheCls);
        Map<Long, DbRec> mapLocation = getMapping(stLocation);
        //
        //select c.id, v.name  from Cls c, ClsVer v
        List<DbRec> stCls = metaService.getCls("Typ_Work");
        Map<Long, DbRec> mapCls = getMapping(stCls);
        //
        String idsCls = getWhereIds(stCls);
        //select o.id, o.cls, v.fullName, null as nameClsWork from o, ObjVer v
        List<DbRec> stWork = nsiService.getObjInfo(idsCls);
        for (DbRec map : stWork) {
            map.put("nameClsWork", mapCls.get(map.getLong("cls")).getString("name"));
        }
        Map<Long, DbRec> mapWork = getMapping(stWork);
        //
        //select c.id, v.name  from Cls c, ClsVer v
        stCls = metaService.getCls("Typ_Object");
        idsCls = getWhereIds(stCls);
        mapCls = getMapping(stCls);
        //select o.id, o.cls, v.fullName, null as nameClsObject from Obj
        List<DbRec> stObject = objectService.getObjInfo(idsCls);
        for (DbRec map : stObject) {
            if (mapCls.containsKey(map.getLong("cls"))) {
                map.put("nameClsObject", mapCls.get(map.getLong("cls")).getString("name"));
            }
        }
        Map<Long, DbRec> mapObject = getMapping(stObject);
        //
        DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Personnel", "");

        /*
        map = apiMeta().get(ApiMeta).getIdFromCodOfEntity("Cls", "Cls_Personnel", "")
        //
        Store stUser = loadSqlService("""
            select o.id, o.cls, v.fullName
            from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.cls=${map.get("Cls_Personnel")}
        """, "", "personnaldata")
        StoreIndex indUser = stUser.getIndex("id")

         */


        //
        return stPlan;

    }

    private Map<Long, DbRec> getMapping(List<DbRec> lst) {
        Map<Long, DbRec> res = new HashMap<>();
        for (DbRec map : lst) {
            res.put(map.getLong("id"), map);
        }
        return res;
    }

    // return (id1,id2,...)
    private String getWhereIds(List<DbRec> lst) {
        // Получение Set значений id
        Set<Long> idSet = lst.stream()
                .map(map -> (Long) map.get("id"))
                .collect(Collectors.toSet());
        // Преобразование Set в строку через запятую
        String ids = "(" + idSet.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + ")";
        if (ids.equals("()")) ids = "(0)";
        return ids;
    }

}
