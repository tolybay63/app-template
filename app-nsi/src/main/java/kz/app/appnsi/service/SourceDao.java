package kz.app.appnsi.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.structure.service.StructureDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SourceDao {
    private final Db dbNsi;
/*
    private final MetaDao metaService;
    private final StructureDao structureService;
*/

    public SourceDao(@Qualifier("dbNsi") Db dbNsi/*, MetaDao metaService, StructureDao structureService*/) {
        this.dbNsi = dbNsi;
/*
        this.metaService = metaService;
        this.structureService = structureService;
*/
    }

/*
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
*/

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
