package kz.app.appmain.contoller;

import kz.app.appmain.service.SendKafkaFunc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
