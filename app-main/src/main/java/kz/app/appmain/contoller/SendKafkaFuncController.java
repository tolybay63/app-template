package kz.app.appmain.contoller;

import kz.app.appmain.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/send")
public class SendKafkaFuncController {

    @Autowired
    private SendKafkaFunc sendKafkaFunc;

    @GetMapping(value = "/func")
    public void find(
            @RequestParam("task") String task
    ) throws Exception {
        sendKafkaFunc.send(task);
    }

}
