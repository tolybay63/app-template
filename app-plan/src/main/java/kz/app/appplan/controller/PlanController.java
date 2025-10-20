package kz.app.appplan.controller;


import kz.app.appcore.model.DbRec;
import kz.app.appplan.service.PlanDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanDao planDao;

/*
    @GetMapping(value = "/loadPlan")
    public List<DbRec> find(
            @RequestParam("params") DbRec params
    ) throws Exception {
        return planDao.loadPlan(params);
    }

*/


}
