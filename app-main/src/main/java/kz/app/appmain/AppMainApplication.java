package kz.app.appmain;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {
        "kz.app.appmain",
        "kz.app.appcore",
        "kz.app.appdbtools",
        "kz.app.appfile",
        "kz.app.appsearchtools",
        "kz.app.appauth",
        "kz.app.appstorage",
})
public class AppMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppMainApplication.class, args);
    }

}
