package kz.app.appinspection.controller;


import kz.app.appcore.model.DbRec;
import kz.app.appinspection.service.InspectionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/plan")
public class InspectionController {

    @Autowired
    private InspectionDao inspectionDao;

/*
    @GetMapping(value = "/loadInspection")
    public List<DbRec> find(
            @RequestParam("params") DbRec params
    ) throws Exception {
        return inspectionDao.loadInspection(params);
    }
*/



}
