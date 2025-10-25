package kz.app.applink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {
        "kz.app.appcore",
        "kz.app.appdbtools",
        "kz.app.appmeta",
        "kz.app.common",
        "kz.app.applink",
        "kz.app.appnsi",
        "kz.app.appobject",
        "kz.app.appclient",
        "kz.app.appplan",
        "kz.app.apppersonnal",
        "kz.app.structure",
        "kz.app.appinspection",
        "kz.app.appincident",

})

public class AppLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppLinkApplication.class, args);
    }

}
