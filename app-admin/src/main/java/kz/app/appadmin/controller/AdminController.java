package kz.app.appadmin.controller;

import kz.app.appadmin.service.AdminDao;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

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
}
