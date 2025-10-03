package kz.app.appobject.controller;

import kz.app.appcore.model.DbRec;
import kz.app.appobject.service.ObjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/object")
public class ObjectController {

    @Autowired
    private ObjectDao objectDao;


    @GetMapping(value = "/loadObjectServed")
    public List<DbRec> loadObjectServed(@RequestParam long id) throws Exception {
        return objectDao.loadObjectServed(id);
    }

}
