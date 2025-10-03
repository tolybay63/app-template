package kz.app.appnsi;

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
        "kz.app.structure",
        "kz.app.appnsi",
})

public class AppNsiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppNsiApplication.class, args);
    }

}
