package kz.app.appclient.service;


import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.*;
import kz.app.appcore.utils.consts.FD_AttribValType_consts;
import kz.app.appcore.utils.consts.FD_InputType_consts;
import kz.app.appcore.utils.consts.FD_PeriodType_consts;
import kz.app.appcore.utils.consts.FD_PropType_consts;
import kz.app.appdata.service.UtEntityData;
import kz.app.appdbtools.repository.Db;
import kz.app.appincident.service.IncidentDao;
import kz.app.appinspection.service.InspectionDao;
import kz.app.appmeta.service.MetaDao;
import kz.app.appnsi.service.NsiDao;
import kz.app.appobject.service.ObjectDao;
import kz.app.apppersonnal.service.PersonnalDao;
import kz.app.appplan.service.PlanDao;
import kz.app.structure.service.StructureDao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class ClientDao {

    private final Db dbClient;

    private final MetaDao metaService;
    private final ObjectDao objectService;
    private final PlanDao planService;
    private final StructureDao structureService;
    private final NsiDao nsiService;
    private final PersonnalDao personnalService;
    private final InspectionDao inspectionService;
    private final IncidentDao incidentService;

    public ClientDao(@Qualifier("dbClient") Db dbClient, MetaDao metaService, ObjectDao objectService,
                     PlanDao planService, StructureDao structureService, NsiDao nsiService,
                     PersonnalDao personnalService, InspectionDao inspectionService, IncidentDao incidentService) {
        this.dbClient = dbClient;
        this.metaService = metaService;
        this.objectService = objectService;
        this.planService = planService;
        this.structureService = structureService;
        this.nsiService = nsiService;
        this.personnalService = personnalService;
        this.inspectionService = inspectionService;
        this.incidentService = incidentService;
    }


    public List<DbRec> loadClient(long id) throws Exception {

        DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Client", "");
        long cls = UtCnv.toLong(map.get("Cls_Client"));
        map = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");
        String whe = "o.id=:id";
        if (id == 0) {
            whe = "o.cls=:Cls_Client";
            map.put("Cls_Client", cls);
        } else {
            map.put("id", id);
        }
        //
        return dbClient.loadSql("""
                    select o.id, o.cls, v.name,
                        v1.id as idBIN, v1.strVal as BIN,
                        v2.id as idContactPerson, v2.strVal as ContactPerson,
                        v3.id as idContactDetails, v3.strVal as ContactDetails,
                        v4.id as idDescription, v4.multiStrVal as Description
                    from Obj o
                        left join ObjVer v on o.id=v.ownerVer and v.lastVer=1
                        left join DataProp d1 on d1.objorrelobj=o.id and d1.prop=:Prop_BIN  --1168
                        left join DataPropVal v1 on d1.id=v1.dataprop
                        left join DataProp d2 on d2.objorrelobj=o.id and d2.prop=:Prop_ContactPerson    --1169
                        left join DataPropVal v2 on d2.id=v2.dataprop
                        left join DataProp d3 on d3.objorrelobj=o.id and d3.prop=:Prop_ContactDetails   --1170
                        left join DataPropVal v3 on d3.id=v3.dataprop
                        left join DataProp d4 on d4.objorrelobj=o.id and d4.prop=:Prop_Description  --1137
                        left join DataPropVal v4 on d4.id=v4.dataprop
                    where
                """ + whe, map);
    }

    private void validateForDeleteObj(long owner) throws Exception {
        String sql = "select o.cls, v.name from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.id=:id";
        DbRec recOwn = dbClient.loadSqlRec(sql, Map.of("id", owner), false);
        if (recOwn != null) {
            List<String> lstService = new ArrayList<>();
            String name = recOwn.getString("name");
            //Own - Надо проверить - является ли участником отношения?
            List<DbRec> stMem = dbClient.loadSql("""
            select rv.name from RelObjMember m
            left join RelObjVer rv on rv.ownerVer=m.relObj and rv.lastVer=1
            where obj=:obj
            """, Map.of("obj", owner));
            if (!stMem.isEmpty()) {
                throw new XError("Данный объект является участником отношения [{0}]",
                       UtString.join(UtDb.uniqueValues(stMem, "name"), ", "));
            }
            //
            long cls = recOwn.getLong("cls");
            Set<Long> stPV = metaService.getIdsPV(1, cls, "");
            if (!(stPV.size()==1 && stPV.contains(0L))) {
                String whePV = "(" + UtString.join(stPV, ",") + ")";
                //clientdata
                List<DbRec> st = getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("clientdata");
                }
                //objectdata
                st =  objectService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("objectdata");
                }
                //plandata
                st =  planService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("plandata");
                }
                //personnaldata
                st =  personnalService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("personnaldata");
                }
                //nsidata
                st =  nsiService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("nsidata");
                }
                //structuredata
                st =  structureService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("structuredata");
                }
                //inspectiondata
                st =  inspectionService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("inspectiondata");
                }
                //incidentdata
                st =  incidentService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("incidentdata");
                }

                //...
                if (!lstService.isEmpty()) {
                    throw new XError("{0} используется в [{1}]", name, UtString.join(lstService, ", "));
                }
            }
        }
    }

    //todo Метод должен быть во всех data-сервисах
    public List<DbRec> getRefData(int isObj, long owner, String whePV) throws Exception {
        return dbClient.loadSql("""
                    select d.id from DataProp d, DataPropVal v
                    where d.id=v.dataProp and d.isObj=:isObj and v.propVal in
        """ + whePV + " and obj=:owner", Map.of("isObj", isObj, "owner", owner));
    }

    public void deleteOwnerWithProperties(long id) throws Exception {
        validateForDeleteObj(id);
        UtEntityData ue = new UtEntityData(dbClient, "Obj");
        ue.deleteOwnerWithProperties(id);
    }

    public List<DbRec> saveClient(String mode, DbRec params) throws Exception {
        //*** <begin Определяем обязательные свойства
        String[] reqProps = new String[] {"Prop_BIN", "Prop_ContactPerson", "Prop_ContactDetails"};
        //*** end>

        if (params.getString("name").isEmpty()) throw new XError("[name] не указан");
        params.putIfAbsent("fullname", params.get("name"));

        //**** <begin Определяем класс, если с клиента не придет... *****************
        if (params.getLong("cls")==0) {
            DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Client", "");
            params.put("cls", map.getLong("Cls_Client"));
        }
        //**** end>

        Set<String> idFields = new HashSet<String>();
        Set<String> valueFields = new HashSet<String>();
        for (String key : params.keySet()) {
            if (key.startsWith("id"))
                idFields.add("Prop_" + key.substring(2));
            else if (key.startsWith("obj"))
                valueFields.add("Prop_" + key.substring(3));
            else if (key.startsWith("relobj"))
                valueFields.add("Prop_" + key.substring(6));
            else if (key.startsWith("fv"))
                valueFields.add("Prop_" + key.substring(2));
            else if (key.startsWith("mea"))
                valueFields.add("Prop_" + key.substring(3));
            else if (key.startsWith("pv"))
                valueFields.add("Prop_" + key.substring(2));
            else if (!key.isEmpty() && Character.isUpperCase(key.charAt(0))) {
                valueFields.add("Prop_" + key);
            }
        }
        //
        for (String prop : reqProps) {
            if (!valueFields.contains(prop)) {
                throw new Exception("Значение свойства ["+prop+"] обязательно");
            }
        }
        //
        valueFields.addAll(idFields);
        String whePrp = "('" + UtString.join(valueFields, "','") + "')";
        //
        List<DbRec> stProp = metaService.getPropsInfo(whePrp);
        Map<String, DbRec> mapProp = new HashMap<>();
        for (DbRec prop : stProp) {
            mapProp.put(prop.getString("cod"), prop);
        }
        //
        long own;
        UtEntityData ue = new UtEntityData(dbClient, "Obj");
        if (mode.equalsIgnoreCase("ins")) {
            own = ue.insertEntity(params);
            params.put("own", own);
        } else if (mode.equalsIgnoreCase("upd")) {
            own = params.getLong("id");
            ue.updateEntity(params);
            params.put("own", own);
        } else {
            throw new XError("Unknown mode: " + mode);
        }
        //
        ue.saveObjWithProps(mode, params, mapProp);
        //
        return loadClient(own);
    }



}
