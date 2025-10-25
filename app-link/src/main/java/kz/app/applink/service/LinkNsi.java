package kz.app.applink.service;

import kz.app.appcore.model.DbRec;
import kz.app.appdbtools.repository.Db;
import kz.app.structure.service.StructureDao;

import java.util.List;

public class LinkNsi extends LinkDao {

    private final StructureDao structureService;

    public LinkNsi(Db dbLink, StructureDao structureService) {
        super(dbLink);
        this.structureService = structureService;
    }


    public List<DbRec> loadDepartments(String codTyp, String codProp) throws Exception {
        return structureService.loadObjTreeForSelect(codTyp, codProp);
    }


}
