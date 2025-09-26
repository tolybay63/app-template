package kz.app.appincident.controller;


import kz.app.appincident.service.IncidentDao;
import kz.app.appcore.model.DbRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// DAO

@RestController
@RequestMapping("/incident")
public class IncidentController {

    @Autowired
    private IncidentDao incidentDao;


    @GetMapping(value = "/loadEvent")
    public List<DbRec> find(
            @RequestParam("id") long id
    ) throws Exception {
        return incidentDao.loadEvent(id);
    }

}
