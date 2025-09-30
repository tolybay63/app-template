package kz.app.appadmin.controller;

import kz.app.appadmin.service.DictDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private DictDao dictDao;


    @GetMapping(value = "/loadDict")
    public Map<Long, String> loadDict(@RequestParam String dictName) throws Exception {
        return dictDao.loadDict(dictName);
    }



}
