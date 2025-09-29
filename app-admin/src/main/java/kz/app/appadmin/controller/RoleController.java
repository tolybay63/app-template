package kz.app.appadmin.controller;

import kz.app.appadmin.service.RoleDao;
import kz.app.appcore.model.DbRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// DAO

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleDao roleDao;


    @GetMapping(value = "/loadRoles")
    public List<DbRec> find(
    ) throws Exception {
        return roleDao.loadRoles();
    }

    @GetMapping(value = "/insertRole")
    public DbRec find(
            @RequestParam("rec") DbRec rec
    ) throws Exception {
        return roleDao.insertRole(rec);
    }

/*
    @GetMapping(value = "/updateRole")
    public DbRec find(
            @RequestParam("rec") DbRec rec
    ) throws Exception {
        return roleDao.updateRole(rec);
    }
*/


    @GetMapping(value = "/deleteRole")
    public void find(
            @RequestParam("id") long id
    ) throws Exception {
        roleDao.deleteRole(id);
    }


}
