package kz.app.appadmin.controller;

import kz.app.appadmin.service.AdminDao;
import kz.app.appcore.model.DbRec;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// DAO

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminDao adminDao;


    @GetMapping(value = "/load")
    public String loadA() throws Exception {
        return adminDao.loadA();
    }

    @GetMapping(value = "/loadUsers")
    public List<DbRec> find(
            @RequestParam("idGroup") long idGroup
    ) throws Exception {
        return adminDao.loadUsers(idGroup);
    }

}
