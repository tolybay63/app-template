package kz.app.appadmin.controller;

import kz.app.grpc.HelloGrpcClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final HelloGrpcClient client;

    public HelloController(HelloGrpcClient client) {
        this.client = client;
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam String name) {
        return client.sendHello(name);
    }
}
