package kz.app.appinspection.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtPeriod;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.appnsi.service.NsiDao;
import kz.app.apppersonnal.service.PersonnalDao;
import kz.app.appplan.service.PlanDao;
import kz.app.appobject.service.ObjectDao;
import kz.app.structure.service.StructureDao;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InspectionDao {
    private final Db dbInspection;
    private final MetaDao metaService;
    private final StructureDao structureService;
    private final NsiDao nsiService;
    private final ObjectDao objectService;
    private final PersonnalDao personnalService;
    private final PlanDao planService;

    public InspectionDao(Db dbInspection, MetaDao metaService, StructureDao structureService, NsiDao nsiService,
                         ObjectDao objectService, PersonnalDao personnalService, PlanDao planService) {
        this.dbInspection = dbInspection;
        this.metaService = metaService;
        this.structureService = structureService;
        this.nsiService = nsiService;
        this.objectService = objectService;
        this.personnalService = personnalService;
        this.planService = planService;
    }


    public List<DbRec> loadInspection(DbRec params) throws Exception {
        DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Inspection", "");
        String whe;
        String wheV1 = "";
        String wheV7 = "";
        //
        DbRec paramSql = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");
        if (params.containsKey("id"))
            whe = "o.id=" + params.get("id");
        else {
            whe = "o.cls = " + map.getLong("Cls_Inspection");
            //
            DbRec mapCls = metaService.getIdFromCodOfEntity("Cls", "Cls_LocationSection", "");
            DbRec objRec = structureService.getObjRec(params.getLong("objLocation"));
            long clsLocation = objRec==null ? 0 : objRec.getLong("cls");
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
        map = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");
        List<DbRec> stInspection = dbInspection.loadSql("""
            select o.id, o.cls, v.name, null as nameCls,
                v1.id as idLocationClsSection, v1.propVal as pvLocationClsSection,
                    v1.obj as objLocationClsSection, null as nameLocationClsSection,
                v2.id as idWorkPlan, v2.propVal as pvWorkPlan, v2.obj as objWorkPlan,
                v3.id as idStartKm, v3.numberVal as StartKm,
                v4.id as idFinishKm, v4.numberVal as FinishKm,
                v5.id as idStartPicket, v5.numberVal as StartPicket,
                v6.id as idFinishPicket, v6.numberVal as FinishPicket,
                v7.id as idFactDateEnd, v7.dateTimeVal as FactDateEnd,
                v8.id as idUser, v8.propVal as pvUser, v8.obj as objUser, null as fullNameUser,
                v9.id as idCreatedAt, v9.dateTimeVal as CreatedAt,
                v10.id as idUpdatedAt, v10.dateTimeVal as UpdatedAt,
                v11.id as idFlagDefect, v11.propVal as pvFlagDefect, null as fvFlagDefect, null as nameFlagDefect,
                v12.id as idStartLink, v12.numberVal as StartLink,
                v13.id as idFinishLink, v13.numberVal as FinishLink,
                v14.id as idReasonDeviation, v14.multiStrVal as ReasonDeviation,
                v15.id as idFlagParameter, v15.propVal as pvFlagParameter, null as fvFlagParameter, null as nameFlagParameter
            from Obj o
                left join ObjVer v on o.id=v.ownerver and v.lastver=1
                left join DataProp d1 on d1.objorrelobj=o.id and d1.prop=:Prop_LocationClsSection
                inner join DataPropVal v1 on d1.id=v1.dataprop
            """ + wheV1 + """
                left join DataProp d2 on d2.objorrelobj=o.id and d2.prop=:Prop_WorkPlan
                left join DataPropVal v2 on d2.id=v2.dataprop
                left join DataProp d3 on d3.objorrelobj=o.id and d3.prop=:Prop_StartKm
                left join DataPropVal v3 on d3.id=v3.dataprop
                left join DataProp d4 on d4.objorrelobj=o.id and d4.prop=:Prop_FinishKm
                left join DataPropVal v4 on d4.id=v4.dataprop
                left join DataProp d5 on d5.objorrelobj=o.id and d5.prop=:Prop_StartPicket
                left join DataPropVal v5 on d5.id=v5.dataprop
                left join DataProp d6 on d6.objorrelobj=o.id and d6.prop=:Prop_FinishPicket
                left join DataPropVal v6 on d6.id=v6.dataprop
                left join DataProp d7 on d7.objorrelobj=o.id and d7.prop=:Prop_FactDateEnd
                inner join DataPropVal v7 on d7.id=v7.dataprop
            """ + wheV7 + """ 
                left join DataProp d8 on d8.objorrelobj=o.id and d8.prop=:Prop_User
                left join DataPropVal v8 on d8.id=v8.dataprop
                left join DataProp d9 on d9.objorrelobj=o.id and d9.prop=:Prop_CreatedAt
                left join DataPropVal v9 on d9.id=v9.dataprop
                left join DataProp d10 on d10.objorrelobj=o.id and d10.prop=:Prop_UpdatedAt
                left join DataPropVal v10 on d10.id=v10.dataprop
                left join DataProp d11 on d11.objorrelobj=o.id and d11.prop=:Prop_FlagDefect
                left join DataPropVal v11 on d11.id=v11.dataprop
                left join DataProp d12 on d12.objorrelobj=o.id and d12.prop=:Prop_StartLink
                left join DataPropVal v12 on d12.id=v12.dataprop
                left join DataProp d13 on d13.objorrelobj=o.id and d13.prop=:Prop_FinishLink
                left join DataPropVal v13 on d13.id=v13.dataprop
                left join DataProp d14 on d14.objorrelobj=o.id and d14.prop=:Prop_ReasonDeviation
                left join DataPropVal v14 on d14.id=v14.dataprop
                left join DataProp d15 on d15.objorrelobj=o.id and d15.prop=:Prop_FlagParameter
                left join DataPropVal v15 on d15.id=v15.dataprop
            where
        """ + whe, map);
        //... Пересечение
        String idsObjLocation = getWhereIds(stInspection, "objLocationClsSection");
        List<DbRec> stObjLocation = structureService.objIdName(idsObjLocation, "");
        Map<Long, DbRec> mapLocation = getMapping(stObjLocation);
        //
        String idsWorkPlan = getWhereIds(stInspection, "objWorkPlan");
        map = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");
        map.put("ids", idsWorkPlan);
        List<DbRec> stWorkPlanProps = planService.loadWorkPlan(map);
        Map<Long, DbRec> mapWorkPlan = getMapping(stWorkPlanProps);
        //
        String pvsFlagDefect = getWhereIds(stInspection, "pvFlagDefect");
        List<DbRec> stFlagDefect = metaService.getFactorValsInfo(pvsFlagDefect);
        Map<Long, DbRec> mapFlagDefect = getMapping(stFlagDefect);
        //
        String pvsFlagParameter = getWhereIds(stInspection, "pvFlagParameter");
        List<DbRec> stFlagParameter = metaService.getFactorValsInfo(pvsFlagParameter);
        Map<Long, DbRec> mapFlagParameter = getMapping(stFlagParameter);
        //
        map = metaService.getIdFromCodOfEntity("Cls", "Cls_Personnel", "");
        //select o.id, o.cls, v.fullName
        List<DbRec> stUser = personnalService.getObjList(map.getLong("Cls_Personnel"));
        Map<Long, DbRec> mapUser = getMapping(stUser);
        //
        for (DbRec r : stInspection) {
            if (mapLocation.get(r.getLong("objLocationClsSection")) != null)
                r.put("nameLocationClsSection", mapLocation.get(r.getLong("objLocationClsSection")).getString("name"));
            if (mapWorkPlan.get(r.getLong("objWorkPlan")) != null) {
                r.put("objWork", mapWorkPlan.get(r.getLong("objWorkPlan")).getLong("objWork"));
                r.put("objObject", mapWorkPlan.get(r.getLong("objWorkPlan")).getLong("objObject"));
                r.put("PlanDateEnd", mapWorkPlan.get(r.getLong("objWorkPlan")).getString("PlanDateEnd"));
                r.put("ActualDateEnd", mapWorkPlan.get(r.getLong("objWorkPlan")).getString("ActualDateEnd"));
            }
            if (mapFlagDefect.get(r.getLong("pvFlagDefect")) != null) {
                r.put("fvFlagDefect", mapFlagDefect.get(r.getLong("pvFlagDefect")).getLong("factorVal"));
                r.put("nameFlagDefect", mapFlagDefect.get(r.getLong("pvFlagDefect")).getString("name"));
            }
            if (mapFlagParameter.get(r.getLong("pvFlagParameter")) != null) {
                r.put("fvFlagParameter", mapFlagParameter.get(r.getLong("pvFlagParameter")).getLong("factorVal"));
                r.put("nameFlagParameter", mapFlagParameter.get(r.getLong("pvFlagParameter")).getString("name"));
            }
            if (mapUser.get(r.getLong("objUser")) != null) {
                r.put("fullNameUser", mapUser.get(r.getLong("objUser")).getString("fullName"));
            }
        }
        //
        String idsWork = getWhereIds(stInspection, "objWork");
        //select o.id, v.fullName from Obj
        List<DbRec> stWork = nsiService.getObjInfo(idsWork, "");
        Map<Long, DbRec> mapWork = getMapping(stWork);
        //
        String idsObject = getWhereIds(stInspection, "objObject");
        //select o.id, v.fullName from Obj
        List<DbRec> stObject = objectService.getObjInfo(idsWork, "");
        Map<Long, DbRec> mapObject = getMapping(stObject);
        //
        for (DbRec r : stInspection) {
            if (mapWork.get(r.getLong("objWork")) != null)
                r.put("fullNameWork", mapWork.get(r.getLong("objWork")).getString("fullName"));
            if (mapObject.get(r.getLong("objObject")) != null)
                r.put("fullNameWork", mapObject.get(r.getLong("objObject")).getString("fullName"));
        }
        //
        map = metaService.getIdFromCodOfEntity("Prop", "Prop_Section", "");
        map.put("ids", idsObject);
        stObject = objectService.getObjInfoFromData(map);
        mapObject = getMapping(stObject);
        //
        for (DbRec r : stInspection) {
            if (mapObject.get(r.getLong("objObject")) != null) {
                r.put("objSection", mapObject.get(r.getLong("objObject")).getLong("obj"));
                r.put("nameSection", mapObject.get(r.getLong("objObject")).getString("name"));
            }
        }
        //
        return stInspection;
    }

    //todo Кандидат для общего использования
    private Map<Long, DbRec> getMapping(List<DbRec> lst) {
        Map<Long, DbRec> res = new HashMap<>();
        for (DbRec map : lst) {
            res.put(map.getLong("id"), map);
        }
        return res;
    }

    //todo Кандидат для общего использования
    // return (id1,id2,...)
    private String getWhereIds(List<DbRec> lst, String fld) {
        // Получение Set значений id
        Set<Long> idSet = lst.stream()
                .map(map -> (Long) map.get(fld))
                .collect(Collectors.toSet());
        // Преобразование Set в строку через запятую
        String ids = "(" + idSet.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + ")";
        if (ids.equals("()")) ids = "(0)";
        return ids;
    }

}
