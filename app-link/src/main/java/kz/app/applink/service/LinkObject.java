package kz.app.applink.service;

import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtString;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.appnsi.service.NsiDao;
import kz.app.appobject.service.ObjectDao;
import kz.app.appplan.service.PlanDao;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class LinkObject extends LinkDao {

    private final ObjectDao objectService;
    private final NsiDao nsiService;
    private final PlanDao planService;

    public LinkObject(Db dbLink, /*MetaDao metaService, */ObjectDao objectService, NsiDao nsiService, PlanDao planService) {
        super(dbLink);
        this.objectService = objectService;
        this.nsiService = nsiService;
        this.planService = planService;
    }


    public List<DbRec> loadObjectServed(long id) throws Exception {
        List<DbRec> st = objectService.loadObjectServed(id);

        //... Пересечение
        //nameObjectType
        String idsObjectType = UtDb.getWhereIds(st, "objObjectType");
        List<DbRec> stObjectType = nsiService.getObjInfo(idsObjectType, "");
        Map<Long, DbRec> mapObjectType = UtDb.getMapping(stObjectType);

        //fvSide, nameSide
        String pvsSide = UtDb.getWhereIds(st, "pvSide");
        List<DbRec> stSide = getFactorValsInfo(pvsSide);
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

        return st;
    }


    public void deleteOwnerWithProperties(long id) throws Exception {
        List<String> lstService = new ArrayList<>();
        DbRec recOwn = objectService.getObjInfo(id);
        if (recOwn != null) {
            String name = recOwn.getString("name");
            long cls = recOwn.getLong("cls");
            Set<Long> stPV = getIdsPV(1, cls, "");
            if (!(stPV.size() == 1 && stPV.contains(0L))) {
                String whePV = "(" + UtString.join(stPV, ",") + ")";
                //objectdata
                List<DbRec> st = objectService.getRefData(1, id, whePV);
                if (!st.isEmpty()) {
                    lstService.add("objectdata");
                }
                //plandata
                st = planService.getRefData(1, id, whePV);
                if (!st.isEmpty()) {
                    lstService.add("plandata");
                }
                if (!lstService.isEmpty()) {
                    throw new XError("{0} используется в [{1}]", name, UtString.join(lstService, ", "));
                }
            }
            //
            objectService.deleteOwnerWithProperties(id);
        } else
            throw new XError("Запись не найдена");
    }


}
