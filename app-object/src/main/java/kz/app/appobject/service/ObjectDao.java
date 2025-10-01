package kz.app.appobject.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.appnsi.service.NsiDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class ObjectDao {

    private final Db dbObject;
    private final MetaDao metaService;
    private final NsiDao nsiService;

    public ObjectDao(@Qualifier("dbObject") Db dbObject, MetaDao metaService, NsiDao nsiService) {
        this.dbObject = dbObject;
        this.metaService = metaService;
        this.nsiService = nsiService;
    }


    public List<DbRec> loadObjectServed(long id) throws Exception {
        String idsCls = metaService.getIdsCls("Typ_Object");

        DbRec map = metaService.getIdFromCodOfEntity("Prop", "", "Prop_%");
        String whe = "o.id=" + id;
        if (id == 0)
            whe = "o.cls in " + idsCls;

        List<DbRec> st = dbObject.loadSql("""
            select o.id, o.cls, v.name, v.fullName,
            v1.id as idObjectType, v1.propVal as pvObjectType, v1.obj as objObjectType, null as nameObjectType,
            v2.id as idStartKm, v2.numberVal as StartKm,
            v3.id as idFinishKm, v3.numberVal as FinishKm,
            v4.id as idStartPicket, v4.numberVal as StartPicket,
            v5.id as idFinishPicket, v5.numberVal as FinishPicket,
            v6.id as idPeriodicityReplacement, v6.numberVal as PeriodicityReplacement,
            v7.id as idSide, v7.propVal as pvSide, null as fvSide, null as nameSide,
            v8.id as idSpecs, v8.strVal as Specs,
            v9.id as idLocationDetails, v9.strVal as LocationDetails,
            v10.id as idNumber, v10.strVal as Number,
            v11.id as idInstallationDate, v11.dateTimeVal as InstallationDate,
            v12.id as idCreatedAt, v12.dateTimeVal as CreatedAt,
            v13.id as idUpdatedAt, v13.dateTimeVal as UpdatedAt,
            v14.id as idDescription, v14.multiStrVal as Description,
            v15.id as idSection, v15.propVal as pvSection, v15.obj as objSection, ov15.name as nameSection
        from Obj o
            left join ObjVer v on o.id=v.ownerver and v.lastver=1
            left join DataProp d1 on d1.objorrelobj=o.id and d1.prop=:Prop_ObjectType
            left join DataPropVal v1 on d1.id=v1.dataprop
            left join DataProp d2 on d2.objorrelobj=o.id and d2.prop=:Prop_StartKm
            left join DataPropVal v2 on d2.id=v2.dataprop
            left join DataProp d3 on d3.objorrelobj=o.id and d3.prop=:Prop_FinishKm
            left join DataPropVal v3 on d3.id=v3.dataprop
            left join DataProp d4 on d4.objorrelobj=o.id and d4.prop=:Prop_StartPicket
            left join DataPropVal v4 on d4.id=v4.dataprop
            left join DataProp d5 on d5.objorrelobj=o.id and d5.prop=:Prop_FinishPicket
            left join DataPropVal v5 on d5.id=v5.dataprop
            left join DataProp d6 on d6.objorrelobj=o.id and d6.prop=:Prop_PeriodicityReplacement
            left join DataPropVal v6 on d6.id=v6.dataprop
            left join DataProp d7 on d7.objorrelobj=o.id and d7.prop=:Prop_Side
            left join DataPropVal v7 on d7.id=v7.dataprop
            left join DataProp d8 on d8.objorrelobj=o.id and d8.prop=:Prop_Specs
            left join DataPropVal v8 on d8.id=v8.dataprop
            left join DataProp d9 on d9.objorrelobj=o.id and d9.prop=:Prop_LocationDetails
            left join DataPropVal v9 on d9.id=v9.dataprop
            left join DataProp d10 on d10.objorrelobj=o.id and d10.prop=:Prop_Number
            left join DataPropVal v10 on d10.id=v10.dataprop
            left join DataProp d11 on d11.objorrelobj=o.id and d11.prop=:Prop_InstallationDate
            left join DataPropVal v11 on d11.id=v11.dataprop
            left join DataProp d12 on d12.objorrelobj=o.id and d12.prop=:Prop_CreatedAt
            left join DataPropVal v12 on d12.id=v12.dataprop
            left join DataProp d13 on d13.objorrelobj=o.id and d13.prop=:Prop_UpdatedAt
            left join DataPropVal v13 on d13.id=v13.dataprop
            left join DataProp d14 on d14.objorrelobj=o.id and d14.prop=:Prop_Description
            left join DataPropVal v14 on d14.id=v14.dataprop
            left join DataProp d15 on d15.objorrelobj=o.id and d15.prop=:Prop_Section
            left join DataPropVal v15 on d15.id=v15.dataprop
            left join ObjVer ov15 on ov15.ownerVer=v15.obj and ov15.lastVer=1
        where
        """+whe, map);

        //... Пересечение
        //nameObjectType
        String idsObjectType = UtDb.getWhereIds(st, "objObjectType");
        List<DbRec> stObjectType = nsiService.getObjInfo(idsObjectType, "");
        Map<Long, DbRec> mapObjectType = UtDb.getMapping(stObjectType);
        //nameObjectType
        String pvsSide = UtDb.getWhereIds(st, "pvSide");
        List<DbRec> stSide = metaService.getFactorValsInfo(pvsSide);
        Map<Long, DbRec> mapSide = UtDb.getMapping(stSide);

        for (DbRec rec : st) {
            if (mapObjectType.containsKey(rec.getLong("objObjectType"))) {
                rec.put("nameObjectType", mapObjectType.get(rec.getLong("objObjectType")).getString("name"));
            }
            if (mapSide.containsKey(rec.getLong("pvSide"))) {
                rec.put("fvSide", mapSide.get(rec.getLong("pvSide")).getString("factorVal"));
                rec.put("nameSide", mapSide.get(rec.getLong("pvSide")).getString("name"));
            }
        }
        //
        return st;
    }

    /**
     *
     * @param idsObj (id1, id2,...)
     * @param idsCls (id1, id2,...)
     * @return [{id:1, cls:1, name: 'n1', fullName: 'fn1'}, {id:2, cls:1, name: 'n2', fullName: 'fn2'}]
     * @throws Exception Exception
     */
    public List<DbRec> getObjInfo(String idsObj, String idsCls) throws Exception {
        String whe = idsCls.isEmpty() ? " o.id in "+idsObj : " o.cls in "+idsCls;
        return dbObject.loadSql("""
             select o.id, o.cls, v.name, v.fullName from Obj o, ObjVer v where o.id=v.ownerVer and
        """ + whe, null);
    }

    /**
     *
     * @param params Map, keys:
     *               1. ids - ids Owner,
     *               2. codProp - prop of ObjRef
     * @return [{id:1, cls:1, name: 'n1', fullName: 'fn1'}, {id:2, cls:1, name: 'n2', fullName: 'fn2'}]
     * @throws Exception Exception
     */
    public List<DbRec> getObjInfoFromData(DbRec params) throws Exception {
        return dbObject.loadSql("""
            select o.id, v.obj, ov.name, ov.fullName
            from Obj o
                left join DataProp d on d.objorrelobj=o.id and prop=:codProp
                left join DataPropVal v on d.id=v.dataProp
                left join ObjVer ov on v.obj=ov.ownerVer and ov.lastVer=1
            where o.id in
        """ + params.getString("ids"), params);
    }




}
