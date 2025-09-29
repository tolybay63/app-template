package kz.app.appadmin.controller;

import kz.app.appadmin.service.PermissionDao;
import kz.app.appcore.model.DbRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// DAO

@RestController
@RequestMapping("/permis")
public class PermisController {

    @Autowired
    private PermissionDao permissionDao;


    @GetMapping(value = "/loadPermissions")
    public List<DbRec> find(
    ) throws Exception {
        return permissionDao.loadPermissions();
    }



}
