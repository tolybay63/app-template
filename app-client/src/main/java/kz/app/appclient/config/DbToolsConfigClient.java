package kz.app.appclient.config;

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
public class DbToolsConfigClient {

    private static final Logger log = LoggerFactory.getLogger(DbToolsConfigClient.class);

    @Value("${client.datasource.url}")
    private String dbUrl;

    @Value("${client.datasource.username}")
    private String dbUsername;

    @Value("${client.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;


    @Bean(name = "dbClient")
    public Db db(@Qualifier("dataSourceClient") DataSource dataSource, SqlParamInterceptor sqlParamInterceptor) {
        return new JdbcDbImpl(dataSource, sqlParamInterceptor);
    }

    @Bean(name = "dataSourceClient")
    public DataSource dataSource() {
        log.info("=========================");
        log.info("DbToolsConfigClient.dataSource");
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
