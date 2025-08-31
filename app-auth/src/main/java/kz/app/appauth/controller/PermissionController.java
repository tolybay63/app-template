package kz.app.appauth.controller;

import kz.app.appauth.manager.*;
import kz.app.appauth.service.*;
import kz.app.appauth.utils.*;
import kz.app.appcore.model.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static kz.app.appauth.persistance.constant.PermissionPath.*;

@RestController
@RequestMapping("/permission")
@AllArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final UserService userService;
    private final PermissionManager permissionManager;
    private final PermissionDict permissionDict;

    /**
     * Проверка указанного права текущего пользователя на объект
     */
    @GetMapping("/check")
    public ResponseEntity<?> check(
            @RequestParam(value = "permissionType") Long permissionType,
            @RequestParam(value = "dir", required = false) Long dir,
            @RequestParam(value = "file", required = false) Long file
    ) throws Exception {
        Long usr = userService.getCurrentUsrId();

        String objectId = getFieldId(dir, file);

        boolean can = permissionManager.check(usr, objectId, permissionType);

        //
        if (!can) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(permissionDict.getResponseMessageFoPermissionCheck(objectId, permissionType));

        } else {
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Чтение всех прав текущего пользователя на объект
     */
    @GetMapping("/can")
    public ResponseEntity<Map> can(
            @RequestParam(value = "dir", required = false) Long dir,
            @RequestParam(value = "file", required = false) Long file
    ) throws Exception {
        Long usr = userService.getCurrentUsrId();
        String objectId = getFieldId(dir, file);

        //
        return ResponseEntity.ok(permissionManager.getPermissionsByUsr(usr, objectId));
    }

    @GetMapping("/getPermissionsByUsr")
    public ResponseEntity<List<DbRec>> getPermissionsByUsr(
            @RequestParam(value = "dir", required = false) Long dir,
            @RequestParam(value = "file", required = false) Long file,
            @RequestParam(value = "usr", required = false) Long usr
    ) throws Exception {
        return ResponseEntity.ok(permissionService.getPermissionsByUsr(usr, file, dir));
    }

    @GetMapping("/getPermissionsByGrp")
    public ResponseEntity<List<DbRec>> getPermissionsByGrp(
            @RequestParam(value = "dir", required = false) Long dir,
            @RequestParam(value = "file", required = false) Long file,
            @RequestParam(value = "grp", required = false) Long grp
    ) throws Exception {
        return ResponseEntity.ok(permissionService.getPermissionsByGrp(grp, file, dir));
    }

    @GetMapping("/getPermissionByParent")
    public ResponseEntity<List<DbRec>> getPermissionByParent(
            @RequestParam(value = "dir", required = false) Long dir,
            @RequestParam(value = "file", required = false) Long file,
            @RequestParam(value = "usr", required = false) Long usr,
            @RequestParam(value = "grp", required = false) Long grp
    ) throws Exception {
        return ResponseEntity.ok(permissionService.getPermissionsByParent(dir, file, usr, grp));
    }

    @PostMapping("/deleteAllPermissionsByUsr")
    public ResponseEntity<Void> deleteAllPermissionsByUsr(
            @RequestParam(value = "dir", required = false) Long dir,
            @RequestParam(value = "file", required = false) Long file,
            @RequestParam(value = "usr") Long usr
    ) throws Exception {
        permissionService.deleteAllPermissionsByUsr(usr, dir, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deleteAllPermissionsByGrp")
    public ResponseEntity<Void> deleteAllPermissionsByGrp(
            @RequestParam(value = "dir", required = false) Long dir,
            @RequestParam(value = "file", required = false) Long file,
            @RequestParam(value = "grp") Long grp
    ) throws Exception {
        permissionService.deleteAllPermissionsByGrp(grp, dir, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/set")
    public ResponseEntity<Map<Long, Boolean>> setPermissions(
            @RequestParam(value = "usr", required = false) Long usr,
            @RequestParam(value = "grp", required = false) Long grp,
            @RequestParam(value = "file", required = false) Long file,
            @RequestParam(value = "directory", required = false) Long directory,
            @RequestBody Map<Long, Boolean> permissions
    ) throws Exception {
        return ResponseEntity.ok(permissionService.setPermissions(usr, grp, file, directory, permissions));
    }

    @GetMapping("/listByItem")
    public ResponseEntity<List<Map<Long, Boolean>>> getPermissionsByItem(
            @RequestParam(value = "file", required = false) Long file,
            @RequestParam(value = "directory", required = false) Long directory
    ) throws Exception {
        return ResponseEntity.ok(permissionService.getPermissionsByItem(file, directory));
    }
}