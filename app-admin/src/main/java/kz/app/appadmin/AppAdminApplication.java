package kz.app.appadmin;

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
        "kz.app.appadmin",
        "kz.app.grpc",
        "kz.app.appmeta",
        "kz.app.apppersonnal",
})
public class AppAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppAdminApplication.class, args);
    }

}
