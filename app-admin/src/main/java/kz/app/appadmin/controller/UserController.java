package kz.app.appadmin.controller;

import kz.app.appadmin.service.UserDao;
import kz.app.appcore.model.DbRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// DAO

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;


    @GetMapping(value = "/loadGroup")
    public List<DbRec> loadGroup() throws Exception {
        return userDao.loadGroup();
    }

    @GetMapping(value = "/loadUsers")
    public List<DbRec> loadUsers(@RequestParam long group) throws Exception {
        return userDao.loadUsers(group);
    }

    @GetMapping(value = "/loadGroupForSelect")
    public List<DbRec> loadGroupForSelect(@RequestParam long group) throws Exception {
        return userDao.loadGroupForSelect(group);
    }

    @PostMapping(value = "/insertGroup")
    public DbRec insertGroup(@RequestBody DbRec rec) throws Exception {
        return userDao.insertGroup(rec);
    }

    @PostMapping(value = "/updateGroup")
    public DbRec updateGroup(@RequestBody DbRec rec) throws Exception {
        return userDao.updateGroup(rec);
    }

    @GetMapping(value = "/deleteGroup")
    public void deleteGroup(@RequestParam long group) throws Exception {
        userDao.deleteGroup(group);
    }

    @GetMapping(value = "/newRec")
    public DbRec newRec(@RequestParam long group) throws Exception {
        return userDao.newRec(group);
    }

    @GetMapping(value = "/deleteUser")
    public void deleteUser(@RequestParam long user) throws Exception {
        userDao.deleteUser(user);
    }

    @PostMapping(value = "/insertUser")
    public DbRec insertUser(@RequestBody DbRec rec) throws Exception {
        return userDao.insertUser(rec);
    }

    @PostMapping(value = "/updateUser")
    public DbRec updateUser(@RequestBody DbRec rec) throws Exception {
        return userDao.updateUser(rec);
    }


}
