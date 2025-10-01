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

    /**
     * Get-запрос: /role/loadRoles
     * @return List: Список ролей
     * @throws Exception error
     */
    @GetMapping(value = "/loadRoles")
    public List<DbRec> find(
    ) throws Exception {
        return roleDao.loadRoles();
    }

    /**
     * Get-запрос: /role/loadRole
     * @param role: id роли
     * @return record Role {id:id, name:name,...}
     * @throws Exception error
     */
    @GetMapping(value = "/loadRole")
    public DbRec loadRole(@RequestParam("role") long role) throws Exception {
        return roleDao.loadRole(role);
    }

    /**
     * Post запрос /role/insertRole
     * @param rec: атрибуты роли
     * @return record Role {id:id, name:name,...}
     * @throws Exception error
     */
    @PostMapping(value = "/insertRole")
    public DbRec insertRole(@RequestBody DbRec rec) throws Exception {
        return roleDao.insertRole(rec);
    }

    /**
     * Post запрос /role/updateRole
     * @param rec: атрибуты роли
     * @return record Role {id:id, name:name,...}
     * @throws Exception error
     */
    @PostMapping(value = "/updateRole")
    public DbRec updateRole(@RequestBody DbRec rec) throws Exception {
        return roleDao.updateRole(rec);
    }

    /**
     * Get запрос /role/deleteRole
     * @param role: id роли
     * @throws Exception error
     */
    @GetMapping(value = "/deleteRole")
    public void deleteRole(@RequestParam long role) throws Exception {
        roleDao.deleteRole(role);
    }

    /**
     * Get запрос /role/getRolePermissions
     * @param role: id роли
     * @return String: permission1, permission2,...
     * @throws Exception error
     */
    @GetMapping(value = "/getRolePermissions")
    public String getRolePermissions(@RequestParam long role) throws Exception {
        return roleDao.getRolePermissions(role);
    }

    /**
     * Get запрос /role/loadRolePermissions
     * @param role: id роли
     * @return List: Список привилегии для роли role
     * @throws Exception error
     */
    @GetMapping(value = "/loadRolePermissions")
    public List<DbRec> loadRolePermissions(@RequestParam long role) throws Exception {
        return roleDao.loadRolePermissions(role);
    }

    /**
     * Get запрос /role/loadRolePermissionsForUpd
     * @param role: id роли
     * @return List: Список привилегии для редактирования
     * @throws Exception error
     */
    @GetMapping(value = "/loadRolePermissionsForUpd")
    public List<DbRec> loadRolePermissionsForUpd(@RequestParam long role) throws Exception {
        return roleDao.loadRolePermissionsForUpd(role);
    }


}
