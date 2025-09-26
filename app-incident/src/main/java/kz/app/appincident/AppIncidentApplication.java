package kz.app.appincident;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {
        "kz.app.appcore",
        "kz.app.appdbtools",
        //"kz.app.appstorage",
        "kz.app.appincident",
        "kz.app.appmeta",

})

public class AppIncidentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppIncidentApplication.class, args);
    }

}
