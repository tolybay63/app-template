package kz.app.appnsi.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtString;
import kz.app.appcore.utils.XError;
import kz.app.appdata.service.UtEntityData;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.structure.service.StructureDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class NsiDao {
    private final Db dbNsi;
    private final MetaDao metaService;
    private final StructureDao structureService;

    public NsiDao(@Qualifier("dbNsi") Db dbNsi, MetaDao metaService, StructureDao structureService) {
        this.dbNsi = dbNsi;
        this.metaService = metaService;
        this.structureService = structureService;
    }

    public List<DbRec> loadSourceCollections(long obj) throws Exception {
        DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Collections", "");
        if (map.isEmpty())
            throw new XError("NotFoundCod@Cls_Collections");
        String whe = "o.id="+obj;
        if (obj == 0)
            whe = "o.cls="+map.get("Cls_Collections");

        map = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");

        //
        return dbNsi.loadSql("""
            select o.id, o.cls, v.name,
                v1.id as idDocumentNumber, v1.strVal as DocumentNumber,
                v2.id as idDocumentApprovalDate, v2.datetimeVal as DocumentApprovalDate,
                v3.id as idDocumentAuthor, v3.strVal as DocumentAuthor,
                v4.id as idDocumentStartDate, v4.datetimeVal as DocumentStartDate,
                v5.id as idDocumentEndDate, v5.datetimeVal as DocumentEndDate
            from Obj o
                left join ObjVer v on o.id=v.ownerver and v.lastver=1
                left join DataProp d1 on d1.isObj=1 and d1.objorrelobj=o.id and d1.prop=:Prop_DocumentNumber   --1082
                left join DataPropVal v1 on d1.id=v1.dataprop
                left join DataProp d2 on d2.isObj=1 and d2.objorrelobj=o.id and d2.prop=:Prop_DocumentApprovalDate --1083
                left join DataPropVal v2 on d2.id=v2.dataprop
                left join DataProp d3 on d3.isObj=1 and d3.objorrelobj=o.id and d3.prop=:Prop_DocumentAuthor   --1086
                left join DataPropVal v3 on d3.id=v3.dataprop
                left join DataProp d4 on d4.isObj=1 and d4.objorrelobj=o.id and d4.prop=:Prop_DocumentStartDate    --1084
                left join DataPropVal v4 on d4.id=v4.dataprop
                left join DataProp d5 on d5.isObj=1 and d5.objorrelobj=o.id and d5.prop=:Prop_DocumentEndDate  --1085
                left join DataPropVal v5 on d5.id=v5.dataprop
            where
            """ + whe + " order by o.id", map);
    }

    public List<DbRec> loadDepartments(String codTyp, String codProp) throws Exception {
        return structureService.loadObjTreeForSelect(codTyp, codProp);
    }

    public DbRec loadDepartmentsWithFile(long obj) throws Exception {

        DbRec map = metaService.getIdFromCodOfEntity("Prop", "Prop_LocationMulti", "");
        map.put("obj", obj);

        List<DbRec> st = dbNsi.loadSql("""
            select v.obj
            from DataProp d
            left join DataPropVal v on d.id=v.dataprop
            where d.isObj=1 and d.objOrRelObj=:obj and d.prop=:Prop_LocationMulti
        """, map);

        Set<Object> ids = UtDb.uniqueValues(st,"obj");

        DbRec mapRez = new DbRec();
        mapRez.put("departments", UtString.join(ids, ","));
        //Files
        List<DbRec> stDbFS = loadAttachedFiles(obj, "Prop_DocumentFiles");
        //
        mapRez.put("files", stDbFS);

        return mapRez;
    }

    List<DbRec> loadAttachedFiles(long obj, String propCod) throws Exception {
        DbRec map = metaService.getIdFromCodOfEntity("Prop", propCod, "");
        if (map.isEmpty())
            throw new XError("NotFoundCod@${propCod}");
        map.put("id", obj);

        List<DbRec> st = dbNsi.loadSql("""
            select d.objorrelobj as obj, v.id as idDPV, v.fileVal as fileVal, null as fileName, v.cmt
            from DataProp d, DataPropVal v
            where d.id=v.dataprop and d.isObj=1 and d.objorrelobj=:id and d.prop=:propCod
        """, map);

        Set<Object> ids = UtDb.uniqueValues(st, "fileVal");
        if (ids.isEmpty()) ids.add(0L);
        String whe = UtString.join(ids, ",");
        List<DbRec> stFS = metaService.getFileName(whe);
        Map<Long, DbRec> mapFS = UtDb.getMapping(stFS);


        for (DbRec r : st) {
            DbRec rr = mapFS.get(r.getLong("fileVal"));
            if (rr != null) {
                r.put("fileName", rr.getString("filename"));
            }
        }
        return st;
    }

    public void saveDepartment(DbRec params) throws Exception {
        DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Location", "");
        if (map.isEmpty())
            throw new XError("NotFoundCod@Cls_Location");
        long cls = map.getLong("Cls_Location");

        map = metaService.getIdFromCodOfEntity("Prop", "Prop_LocationMulti", "");
        if (map.isEmpty())
            throw new XError("NotFoundCod@Prop_LocationMulti");
        map.put("obj", params.getLong("obj"));

        List<DbRec> stOld = dbNsi.loadSql("""
            select v.id, v.obj
            from DataProp d
                left join DataPropVal v on d.id=v.dataprop
            where d.isObj=1 and d.objOrRelObj=:obj and d.prop=:Prop_LocationMulti
        """, map);

        Set<Long> idsOld = UtDb.uniqueValues(stOld, "obj");
        List<Long> idsNew = UtCnv.toListLong(params.get("ids"));

        Set<Long> idsDelVal = new HashSet<>();
        //Deleting
        for (DbRec r : stOld) {
            if (!idsNew.contains(r.getLong("obj"))) {
                idsDelVal.add(r.getLong("id"));
            }
        }
        if (!idsDelVal.isEmpty()) {
            dbNsi.execSql("""
                delete from DataPropVal where id in (
            """ + UtString.join(idsDelVal, ",") +
            """ 
            );
            delete from DataProp
                where id in (
                    select id from DataProp
                    except
                    select dataprop as id from DataPropVal
                )
            """, null);
        }
        //
        //Adding
        DbRec pms = new DbRec();
        pms.put("own", params.getLong("obj"));
        //cls ?
        Map<Long, Long> mapPV = metaService.mapEntityIdFromPV("cls", false);
        DbRec mapProp = metaService.getPropInfo("Prop_LocationMulti");
        //
        UtEntityData ue = new UtEntityData(dbNsi, "Obj");
        for (long obj : idsNew) {
            if (!idsOld.contains(obj)) {
                pms.put("objLocationMulti", obj);
                pms.put("pvLocationMulti", mapPV.get(cls));
                ue.fillProperties("Prop_LocationMulti", pms, mapProp);
            }
        }

    }

    public List<DbRec> saveSourceCollections(String mode, DbRec params) throws Exception {
        //*** <begin Определяем обязательные свойства
        String[] reqProps = new String[] {"Prop_DocumentNumber", "Prop_DocumentApprovalDate", "Prop_DocumentAuthor"};
        //*** end>

        if (params.getString("name").isEmpty()) throw new XError("[name] не указан");
        params.putIfAbsent("fullname", params.get("name"));

        //**** <begin Определяем класс, если с клиента не придет... *****************
        if (params.getLong("cls")==0) {
            DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Collections", "");
            params.put("cls", map.getLong("Cls_Collections"));
        }
        //**** end>
        Set<String> setFields = new HashSet<String>();
        for (String key : params.keySet()) {
            if (key.startsWith("id"))
                setFields.add("Prop_" + key.substring(2));
            else if (key.startsWith("obj"))
                setFields.add("Prop_" + key.substring(3));
            else if (key.startsWith("relobj"))
                setFields.add("Prop_" + key.substring(6));
            else if (key.startsWith("fv"))
                setFields.add("Prop_" + key.substring(2));
            else if (key.startsWith("pv"))
                setFields.add("Prop_" + key.substring(2));
            else if (!Set.of("id", "cls", "name", "fullname", "cmt", "cmtVer").contains(key)) {
                setFields.add("Prop_" + key);
            }
        }
        //
        for (String prop : reqProps) {
            if (!setFields.contains(prop)) {
                throw new Exception("Значение свойства ["+prop+"] обязательно");
            }
        }
        //
        String whePrp = "('" + UtString.join(setFields, "','") + "')";
        List<DbRec> stProp = metaService.getPropsInfo(whePrp);
        Map<String, DbRec> mapProp = new HashMap<>();
        for (DbRec prop : stProp) {
            mapProp.put(prop.getString("cod"), prop);
        }
        //
        long own;
        UtEntityData ue = new UtEntityData(dbNsi, "Obj");
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
        return loadSourceCollections(own);
    }

    public void deleteClientWithProps(long obj) throws Exception {
        validateForDeleteObj(obj);
        UtEntityData ue = new UtEntityData(dbNsi, "Obj");
        ue.deleteObjWithProps(obj);
    }

    private void validateForDeleteObj(long owner) throws Exception {
        String sql = "select o.cls, v.name from Obj o, ObjVer v where o.id=v.ownerVer and v.lastVer=1 and o.id=:id";
        DbRec recOwn = dbNsi.loadSqlRec(sql, Map.of("id", owner), false);
        if (recOwn != null) {
            List<String> lstService = new ArrayList<>();
            String name = recOwn.getString("name");
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
/*
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
*/

                //structuredata
                st =  structureService.getRefData(1, owner, whePV);
                if (!st.isEmpty()) {
                    lstService.add("structuredata");
                }
                //...
                if (!lstService.isEmpty()) {
                    throw new XError("{0} используется в [{0}]", name, UtString.join(lstService, ", "));
                }
            }
        }
    }
    //************************
    public List<DbRec> loadDefects(long obj) throws Exception {
        DbRec map = metaService.getIdFromCodOfEntity("Cls", "Cls_Defects", "");
        if (map.isEmpty())
            throw new XError("NotFoundCod@Cls_Defects");
        String whe = "o.id="+obj;
        if (obj == 0)
            whe = "o.cls="+map.get("Cls_Defects");
        map = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");
        List<DbRec> st = dbNsi.loadSql("""
            select o.id, o.cls, v.name,
                v1.id as idDefectsComponent, v1.propVal as pvDefectsComponent, v1.obj as objDefectsComponent, ov1.name as nameDefectsComponent,
                v2.id as idDefectsCategory, v2.propVal as pvDefectsCategory, null as fvDefectsCategory,
                v3.id as idDefectsIndex, v3.strVal as DefectsIndex,
                v4.id as idDefectsNote, v4.strVal as DefectsNote
            from Obj o
                left join ObjVer v on o.id=v.ownerver and v.lastver=1
                left join DataProp d1 on d1.objorrelobj=o.id and d1.prop=:Prop_DefectsComponent --1072
                left join DataPropVal v1 on d1.id=v1.dataprop
                left join ObjVer ov1 on v1.obj=ov1.ownerver and ov1.lastver=1
                left join DataProp d2 on d2.objorrelobj=o.id and d2.prop=:Prop_DefectsCategory   --1074
                left join DataPropVal v2 on d2.id=v2.dataprop
                left join DataProp d3 on d3.objorrelobj=o.id and d3.prop=:Prop_DefectsIndex --1073
                left join DataPropVal v3 on d3.id=v3.dataprop
                left join DataProp d4 on d4.objorrelobj=o.id and d4.prop=:Prop_DefectsNote   --1075
                left join DataPropVal v4 on d4.id=v4.dataprop
            where
        """+whe, map);

        Map<Long, Long> mapPV = metaService.mapEntityIdFromPV("factorVal", true);
        for (DbRec record : st) {
            record.put("fvDefectsCategory", mapPV.get(record.getLong("pvDefectsCategory")));
        }
        //
        return st;
    }

    public List<DbRec> getObjInfo(String idsObj, String idsCls) throws Exception {
        String whe = idsCls.isEmpty() ? " o.id in "+idsObj : " o.cls in "+idsCls;
        return dbNsi.loadSql("""
                     select o.id, o.cls, v.name, v.fullName from Obj o, ObjVer v where o.id=v.ownerVer and
                """ + whe, null);
    }

    //todo Метод должен быть во всех data-сервисах
    public List<DbRec> getRefData(int isObj, long owner, String whePV) throws Exception {
        return dbNsi.loadSql("""
                    select d.id from DataProp d, DataPropVal v
                    where d.id=v.dataProp and d.isObj=:isObj and v.propVal in
        """ + whePV + " and obj=:owner", Map.of("isObj", isObj, "owner", owner));
    }

}
