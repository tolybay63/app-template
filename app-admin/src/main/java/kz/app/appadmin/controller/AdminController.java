package kz.app.appadmin.controller;

import kz.app.appadmin.service.AdminDao;
import kz.app.appcore.model.DbRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// DAO

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminDao adminDao;


    @GetMapping(value = "/loadGroup")
    public List<DbRec> find(
    ) throws Exception {
        return adminDao.loadGroup();
    }

    @GetMapping(value = "/loadUsers")
    public List<DbRec> find(
            @RequestParam("idGroup") long idGroup
    ) throws Exception {
        return adminDao.loadUsers(idGroup);
    }

}
