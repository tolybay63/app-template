package kz.app.grpc;

import kz.app.grpc.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final HelloClient client;

    public HelloController(HelloClient client) {
        this.client = client;
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam String name) {
        return client.sendHello(name);
    }
}
