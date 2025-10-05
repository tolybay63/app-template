package kz.app.appclient.controller;


import kz.app.appclient.service.ClientDao;
import kz.app.appcore.model.DbRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// DAO

@RestController
@RequestMapping("/service")
public class ClientController {

    @Autowired
    private ClientDao clientDao;


    @GetMapping(value = "/client/loadClient")
    public List<DbRec> loadClient(@RequestParam long id) throws Exception {
        return clientDao.loadClient(id);
    }

}
