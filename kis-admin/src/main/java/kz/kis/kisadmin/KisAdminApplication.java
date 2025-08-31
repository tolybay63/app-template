package kz.kis.kisadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {
        "kz.kis.kiscore",
        "kz.kis.kisdbtools",
        "kz.kis.kisstorage",
        "kz.kis.kisadmin",
})
public class KisAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(KisAdminApplication.class, args);
    }

}
