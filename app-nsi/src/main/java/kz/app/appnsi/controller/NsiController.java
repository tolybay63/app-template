package kz.app.appnsi.controller;

import kz.app.appcore.model.DbRec;
import kz.app.appnsi.service.NsiDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nsi")
public class NsiController {

    @Autowired
    private NsiDao nsiDao;

    @GetMapping(value = "/loadDefects")
    public List<DbRec> loadDefects(@RequestParam long id) throws Exception {
        return nsiDao.loadDefects(id);
    }


}
