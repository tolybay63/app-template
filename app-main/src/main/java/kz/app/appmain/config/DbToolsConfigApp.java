package kz.app.appmain.config;

import kz.app.appdbtools.repository.*;
import kz.app.appdbtools.repository.impl.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.jdbc.*;
import org.springframework.context.annotation.*;
import util.*;

import javax.sql.*;

@Configuration
public class DbToolsConfigApp {

    private static final Logger log = LoggerFactory.getLogger(DbToolsConfigApp.class);

    @Value("${app.datasource.url}")
    private String dbUrl;

    @Value("${app.datasource.username}")
    private String dbUsername;

    @Value("${app.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;


    @Bean(name = "appDb")
    public Db db(@Qualifier("appDataSource") DataSource dataSource, SqlParamInterceptor sqlParamInterceptor) {
        return new JdbcDbImpl(dataSource, sqlParamInterceptor);
    }

    @Bean(name = "appDataSource")
    public DataSource dataSource() {
        log.info("=========================");
        log.info("DbToolsConfigApp.dataSource");
        log.info("url: " + dbUrl);
        log.info("username: " + dbUsername);
        log.info("password: " + UtMask.mask(dbPassword));
        log.info("");

        return DataSourceBuilder.create()
                                .url(dbUrl)
                                .username(dbUsername)
                                .password(dbPassword)
                                .driverClassName(dbDriver)
                                .build();
    }

}
