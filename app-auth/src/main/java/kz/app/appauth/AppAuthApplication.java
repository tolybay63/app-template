package kz.app.appauth;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {
        "kz.app.appauth",
        "kz.app.appcore",
        "kz.app.appdbtools",
        "kz.app.annotations"
})
public class AppAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppAuthApplication.class, args);
    }

}