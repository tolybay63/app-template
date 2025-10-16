package kz.app.applink.controller;

import kz.app.appcore.model.DbRec;
import kz.app.applink.service.LinkDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service")
public class LinkController {

    @Autowired
    private LinkDao linkDao;

    @GetMapping(value = "/loadObjectServed")
    public List<DbRec> loadObjectServed(@RequestParam long obj) throws Exception {
        return linkDao.loadObjectServed(obj);
    }

}
