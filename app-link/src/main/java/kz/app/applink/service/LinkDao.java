package kz.app.applink.service;

import kz.app.appclient.service.ClientDao;
import kz.app.appcore.model.DbRec;
import kz.app.appcore.utils.UtDb;
import kz.app.appcore.utils.UtString;
import kz.app.appcore.utils.XError;
import kz.app.appdbtools.repository.Db;
import kz.app.appincident.service.IncidentDao;
import kz.app.appinspection.service.InspectionDao;
import kz.app.appmeta.service.MetaDao;
import kz.app.appnsi.service.NsiDao;
import kz.app.appobject.service.ObjectDao;
import kz.app.apppersonnal.service.PersonnalDao;
import kz.app.appplan.service.PlanDao;
import kz.app.structure.service.StructureDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class LinkDao {
    private final Db dbLink;
    private final MetaDao metaService;
    private final NsiDao nsiService;
    private final ObjectDao objectService;
    private final ClientDao clientService;
    private final PlanDao planService;
    private final StructureDao structureService;
    private final PersonnalDao personnalService;
    private final InspectionDao inspectionService;
    private final IncidentDao incidentService;

    public LinkDao(@Qualifier("dbLink") Db dbLink, MetaDao metaService, NsiDao nsiService,
                   ObjectDao objectService, ClientDao clientService, PlanDao planService,
                   StructureDao structureService, PersonnalDao personnalService,
                   InspectionDao inspectionService, IncidentDao incidentService) {
        this.dbLink = dbLink;
        this.metaService = metaService;
        this.nsiService = nsiService;
        this.objectService = objectService;
        this.clientService = clientService;
        this.planService = planService;
        this.structureService = structureService;
        this.personnalService = personnalService;
        this.inspectionService = inspectionService;
        this.incidentService = incidentService;
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

        return st;
    }

    public void deleteOwnerWithProperties(long id) throws Exception {
        List<String> lstService = new ArrayList<>();
        DbRec recOwn = objectService.getObjInfo(id);
        if (recOwn != null) {
            String name = recOwn.getString("name");
            long cls = recOwn.getLong("cls");
            Set<Long> stPV = metaService.getIdsPV(1, cls, "");
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
