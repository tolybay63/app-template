package kz.app.applink.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtPeriod;
import kz.app.appdbtools.repository.Db;
import kz.app.appnsi.service.NsiDao;
import kz.app.appobject.service.ObjectDao;
import kz.app.apppersonnal.service.PersonnalDao;
import kz.app.appplan.service.PlanDao;
import kz.app.structure.service.StructureDao;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LinkPlan extends LinkDao {

    private final StructureDao structureService;
    private final NsiDao nsiService;
    private final ObjectDao objectService;
    private final PersonnalDao personnalService;
    private final PlanDao planService;

    public LinkPlan(Db dbLink, StructureDao structureService, NsiDao nsiService, ObjectDao objectService, PersonnalDao personnalService, PlanDao planService) {
        super(dbLink);
        this.structureService = structureService;
        this.nsiService = nsiService;
        this.objectService = objectService;
        this.personnalService = personnalService;
        this.planService = planService;
    }


    public List<DbRec> loadPlan(DbRec params) throws Exception {
        List<DbRec> st = getCls("Typ_WorkPlan");
        String ids = UtDb.getWhereIds(st, "id");
        String whe = "";
        String wheV1 = "";
        String wheV7 = "";
        //
        Map<Long, DbRec> mapClsWork = UtDb.getMapping(st);
        //
        DbRec paramSql = getIdFromCodOfEntity("Prop", "", "Prop_%");
        //
        if (params.containsKey("id"))
            whe = " o.id=" + params.getLong("id");
        else {
            whe = " o.cls in " + ids;

            DbRec mapCls = getIdFromCodOfEntity("Cls", "Cls_LocationSection", "");

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
            wheV7 = " and v7.dateTimeVal between '"+d1+"' and '" + d2 +"'";
        }

        List<DbRec> stPlan = planService.loadPlan(wheV1, wheV7, whe, paramSql);

        //... Пересечение
        String wheCls = getIdsCls("Typ_Location");
        //select o.id, v.name from Obj o, ObjVer v
        List<DbRec> stLocation = structureService.objIdName("", wheCls);
        Map<Long, DbRec> mapLocation = UtDb.getMapping(stLocation);
        //
        //select c.id, v.name  from Cls c, ClsVer v
        List<DbRec> stCls = getCls("Typ_Work");
        Map<Long, DbRec> mapCls = UtDb.getMapping(stCls);
        //
        String idsCls = UtDb.getWhereIds(stCls, "id");
        //select o.id, o.cls, v.fullName, null as nameClsWork from o, ObjVer v
        List<DbRec> stWork = nsiService.getObjInfo("", idsCls);
        for (DbRec map : stWork) {
            if (mapCls.get(map.getLong("cls")) != null)
                map.put("nameClsWork", mapCls.get(map.getLong("cls")).getString("name"));
        }
        Map<Long, DbRec> mapWork = UtDb.getMapping(stWork);
        //
        //select c.id, v.name  from Cls c, ClsVer v
        stCls = getCls("Typ_Object");
        idsCls = UtDb.getWhereIds(stCls, "id");
        mapCls = UtDb.getMapping(stCls);
        //select o.id, o.cls, v.fullName, null as nameClsObject from Obj
        List<DbRec> stObject = objectService.getObjInfo("", idsCls);
        for (DbRec map : stObject) {
            if (mapCls.containsKey(map.getLong("cls"))) {
                map.put("nameClsObject", mapCls.get(map.getLong("cls")).getString("name"));
            }
        }
        Map<Long, DbRec> mapObject = UtDb.getMapping(stObject);
        //
        DbRec map = getIdFromCodOfEntity("Cls", "Cls_Personnel", "");
        //select o.id, o.cls, v.name, v.fullName, null as nameCls from Obj o...
        List<DbRec> stUser = personnalService.getObjList(map.getLong("Cls_Personnel"));
        Map<Long, DbRec> mapUser = UtDb.getMapping(stUser);

        for (DbRec rec : stPlan) {
            if (mapClsWork.containsKey(rec.getLong("cls"))) {
                rec.put("nameCls", mapClsWork.get(rec.getLong("cls")).getString("name"));
            }
            if (mapLocation.containsKey(rec.getLong("objLocationClsSection"))) {
                rec.put("nameLocationClsSection", mapLocation.get(rec.getLong("objLocationClsSection")).getString("name"));
            }
            if (mapWork.containsKey(rec.getLong("objWork"))) {
                rec.put("nameClsWork", mapWork.get(rec.getLong("objWork")).getString("nameClsWork"));
                rec.put("fullNameWork", mapWork.get(rec.getLong("objWork")).getString("fullName"));
            }
            if (mapObject.containsKey(rec.getLong("objObject"))) {
                rec.put("fullNameObject", mapObject.get(rec.getLong("objObject")).getString("fullName"));
                rec.put("nameClsObject", mapObject.get(rec.getLong("objObject")).getString("nameClsObject"));
            }
            if (mapUser.containsKey(rec.getLong("objUser"))) {
                rec.put("fullNameUser", mapUser.get(rec.getLong("objUser")).getString("fullName"));
            }
        }
        //
        return stPlan;

    }















}
