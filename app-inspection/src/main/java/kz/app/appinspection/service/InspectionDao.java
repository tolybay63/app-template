package kz.app.appinspection.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.appmeta.service.MetaDao;
import kz.app.appnsi.service.NsiDao;
import kz.app.apppersonnal.service.PersonnalDao;
import kz.app.structure.service.StructureDao;
import kz.app.object.service.ObjectDao;
import org.springframework.stereotype.Component;

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

    public InspectionDao(Db dbInspection, MetaDao metaService, StructureDao structureService, NsiDao nsiService,
                        ObjectDao objectService, PersonnalDao personnalService) {
        this.dbInspection = dbInspection;
        this.metaService = metaService;
        this.structureService = structureService;
        this.nsiService = nsiService;
        this.objectService = objectService;
        this.personnalService = personnalService;
    }


    public List<DbRec> loadInspection(DbRec params) throws Exception {
        List<DbRec> st = metaService.getCls("Typ_WorkPlan");
        //
        return st;

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
