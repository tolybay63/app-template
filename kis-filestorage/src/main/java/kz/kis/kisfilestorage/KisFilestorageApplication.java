package kz.kis.kisfilestorage;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {
        "kz.kis.kisfilestorage",
        "kz.kis.kiscore",
        "kz.kis.kisdbtools",
        "kz.kis.kisfile",
        "kz.kis.kissearchtools",
        "kz.kis.kisauth",
        "kz.kis.kisstorage",
})
public class KisFilestorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(KisFilestorageApplication.class, args);
    }

}
