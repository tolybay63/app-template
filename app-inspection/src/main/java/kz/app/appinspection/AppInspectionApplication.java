package kz.app.appinspection;

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
        "kz.app.appmeta",
        "kz.app.appinspection",
        "kz.app.structure",
        "kz.app.appnsi",
        "kz.app.appobject",
        "kz.app.apppersonnal",
        "kz.app.appplan",
})

public class AppInspectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppInspectionApplication.class, args);
    }

}
