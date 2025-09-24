package kz.app.appadmin.config;

import kz.app.appdbtools.repository.Db;
import kz.app.appdbtools.repository.SqlParamInterceptor;
import kz.app.appdbtools.repository.impl.JdbcDbImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.UtMask;

import javax.sql.DataSource;

@Configuration
public class DbToolsConfigAdmin {

    private static final Logger log = LoggerFactory.getLogger(DbToolsConfigAdmin.class);

    @Value("${admin.datasource.url}")
    private String dbUrl;

    @Value("${admin.datasource.username}")
    private String dbUsername;

    @Value("${admin.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;


    @Bean(name = "dbAdmin")
    public Db db(@Qualifier("dataSourceAdmin") DataSource dataSource, SqlParamInterceptor sqlParamInterceptor) {
        return new JdbcDbImpl(dataSource, sqlParamInterceptor);
    }

    @Bean(name = "dataSourceAdmin")
    public DataSource dataSource() {
        log.info("=========================");
        log.info("DbToolsConfigAdmin.dataSource");
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
