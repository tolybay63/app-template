package kz.app.appplan;

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
        "kz.app.appplan",
        "kz.app.structure",
        "kz.app.appnsi",
        "kz.app.appobject",
        "kz.app.apppersonnal",
})

public class AppPlanApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppPlanApplication.class, args);
    }

}
