package kz.app.appmain.contoller;

import kz.app.appcore.model.DbRec;
import kz.app.appmain.service.DictDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dict")
public class DictController {

    private static final Logger logger = LoggerFactory.getLogger(DictController.class);

    @Autowired
    private DictDao dictDao;


    @GetMapping(value = "/load")
    public List<DbRec> find(
            @RequestParam("dict") String dict
    ) throws Exception {
        return dictDao.loadDict(dict);
    }
}
