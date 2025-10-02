package kz.app.appadmin.controller;

import kz.app.appadmin.service.PermissionDao;
import kz.app.appcore.model.DbRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

// DAO

@RestController
@RequestMapping("/permis")
public class PermisController {

    @Autowired
    private PermissionDao permissionDao;


    @GetMapping(value = "/getLeaf")
    public Set<String> getLeaf(@RequestParam String id) throws Exception {
        return permissionDao.getLeaf(id);
    }

    @GetMapping(value = "/loadPermissions")
    public List<DbRec> loadPermissions() throws Exception {
        return permissionDao.loadPermissions();
    }

    @GetMapping(value = "/delete")
    public void delete(@RequestParam String id) throws Exception {
        permissionDao.delete(id);
    }

    @PostMapping(value = "/insert")
    public DbRec insert(@RequestBody DbRec rec) throws Exception {
        return permissionDao.insert(rec);
    }

    @PostMapping(value = "/update")
    public DbRec update(@RequestBody DbRec rec) throws Exception {
        return permissionDao.update(rec);
    }


}
