package kz.app.appmain.contoller;

import kz.app.appcore.model.*;
import kz.app.appmain.service.*;
import kz.app.appmain.service.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
