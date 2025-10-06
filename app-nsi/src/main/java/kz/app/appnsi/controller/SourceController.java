package kz.app.appnsi.controller;

import kz.app.appcore.model.DbRec;
import kz.app.appnsi.service.NsiDao;
import kz.app.appnsi.service.SourceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/source")
public class SourceController {

    @Autowired
    private NsiDao nsiDao;

    @GetMapping(value = "/loadSourceCollections")
    public List<DbRec> loadSourceCollections(@RequestParam long obj) throws Exception {
        return nsiDao.loadSourceCollections(obj);
    }

    @GetMapping(value = "/loadDepartments")
    public List<DbRec> loadDepartments(@RequestParam String codTyp, String codProp) throws Exception {
        return nsiDao.loadDepartments(codTyp, codProp);
    }

    @GetMapping(value = "/loadDepartmentsWithFile")
    public DbRec loadDepartmentsWithFile(@RequestParam long obj) throws Exception {
        return nsiDao.loadDepartmentsWithFile(obj);
    }

    @PostMapping(value = "/saveDepartment")
    public void saveDepartment(@RequestBody DbRec rec) throws Exception {
        nsiDao.saveDepartment(rec);
    }

    @PostMapping(value = "/saveSourceCollections")
    public List<DbRec> saveSourceCollections(@RequestBody DbRec rec) throws Exception {
        String mode = rec.getString("mode");
        rec.remove("mode");
        return nsiDao.saveSourceCollections(mode, rec);
    }

    @GetMapping(value = "/deleteClientWithProps")
    public void deleteClientWithProps(@RequestParam long obj) throws Exception {
        nsiDao.deleteClientWithProps(obj);
    }



}
