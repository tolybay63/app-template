package kz.app.grpc;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

@SpringBootApplication
public class GrpcServerApp {

    public static void main(String[] args) {
        SpringApplication.run(GrpcServerApp.class, args);
    }

}