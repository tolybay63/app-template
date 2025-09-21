package kz.app.appnsi.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NsiDao {
    private final Db dbNsi;
    private final MetaDao metaService;

    public NsiDao(@Qualifier("dbNsi") Db dbNsi, MetaDao metaService) {
        this.dbNsi = dbNsi;
        this.metaService = metaService;
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

    public List<DbRec> getObjInfo(String idsObj, String idsCls) throws Exception {
        String whe = idsCls.isEmpty() ? " o.id in "+idsObj : " o.cls in "+idsCls;
        return dbNsi.loadSql("""
                     select o.id, v.name, v.fullName from Obj o, ObjVer v where o.id=v.ownerVer and
                """ + whe, null);
    }


}
