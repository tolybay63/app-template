package kz.app.appadmin.controller;

import kz.app.appadmin.service.RoleDao;
import kz.app.appcore.model.DbRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleDao roleDao;

/*    @GetMapping(value = "/loadRoles")
    public List<DbRec> loadRoles() throws Exception {
        return roleDao.loadRoles();
    }*/

    @GetMapping(value = "/loadRoles")
    public List<DbRec> find(
    ) throws Exception {
        return roleDao.loadRoles();
    }

    @GetMapping(value = "/loadRole")
    public DbRec loadRole(@RequestParam("role") long role) throws Exception {
        return roleDao.loadRole(role);
    }
    


    @PostMapping(value = "/insertRole")
    public DbRec insertRole(@RequestParam DbRec rec) throws Exception {
        return roleDao.insertRole(rec);
    }


    @PostMapping(value = "/updateRole")
    public DbRec updateRole(@RequestParam DbRec rec) throws Exception {
        return roleDao.updateRole(rec);
    }


    @GetMapping(value = "/deleteRole")
    public void find(
            @RequestParam("id") long id
    ) throws Exception {
        roleDao.deleteRole(id);
    }



    @GetMapping(value = "/getRolePermissions")
    public String getRolePermissions(@RequestParam long role) throws Exception {
        return roleDao.getRolePermissions(role);
    }

}
