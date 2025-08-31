package kz.app.appauth.controller;

import kz.app.appauth.persistance.entity.*;
import kz.app.appauth.service.*;
import kz.app.appauth.service.auth.*;
import kz.app.appauth.service.auth.impl.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/usr")
@AllArgsConstructor
public class UsrController {

    private final UserService userService;
    private final UserSynchronizer userSynchronizer;
    private final UsrAttributeService usrAttributeService;
    private final AuthWebService IAuthService;

    @GetMapping("/setOwnUsr")
    public ResponseEntity<Void> setOwnUser(
            @RequestParam("usr") Long usr,
            @RequestParam("ownUsr") Boolean ownUser
    ) throws Exception {
        usrAttributeService.setAttrOwnUserAndCheckKeycloak(usr, ownUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(
            @RequestParam("password") String password,
            @RequestParam("usr") Long usr
    ) throws Exception {
        userService.resetPassword(usr, password);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getId")
    public ResponseEntity<Long> getUserIdByUserName(@RequestParam("userName") String username) throws Exception {
        UserEntity user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(user.getId());
    }

    @GetMapping("/list")
    public ResponseEntity<List<DbRec>> getList() throws Exception {
        List<DbRec> list = userService.getUserList();

        return ResponseEntity.ok(list);
    }

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody Map<String, Object> rec) throws Exception {
        String username = UtCnv.toString(rec.get("name"));
        String email = UtCnv.toString(rec.get("email"));
        String password = UtCnv.toString(rec.get("password"));

        long id = IAuthService.signUp(username, password, email, "", "");

        return ResponseEntity.ok(id);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody Map<String, Object> rec) throws Exception {
        long id = UtCnv.toLong(rec.get("id"));
        String username = UtCnv.toString(rec.get("name"));
        String email = UtCnv.toString(rec.get("email"));

        // todo: обновление на кейклоаке не сделано!!!
        userService.update(id, username, email);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/sync")
    public ResponseEntity<List<DbRec>> sync() throws Exception {
        userSynchronizer.synchronize();

        List<DbRec> list = userService.getUserList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/info")
    public ResponseEntity<UserEntity> getInfo() throws Exception {
        UserEntity user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(user);
    }
}
