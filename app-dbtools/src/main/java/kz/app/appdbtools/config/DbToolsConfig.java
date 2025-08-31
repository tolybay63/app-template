package kz.app.appdbtools.config;

import kz.app.appcore.utils.*;
import kz.app.appdbtools.repository.*;
import kz.app.appdbtools.repository.impl.*;
import kz.app.appdbtools.repository.*;
import kz.app.appdbtools.repository.impl.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.jdbc.*;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.*;

import javax.sql.*;

@Configuration
@ComponentScan(basePackages = "kz.app.appdbtools")
@PropertySource("classpath:application.properties")
public class DbToolsConfig {

    private static final Logger log = LoggerFactory.getLogger("config");

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;

    @Bean
    public SqlParamInterceptor sqlParamInterceptor() {
        return new LoggingParamInterceptor();
    }

/*
    @Bean
    public Db db(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new JdbcTemplateDbImpl(jdbcTemplate, namedParameterJdbcTemplate);
    }
*/

    @Bean
    public Db db(DataSource dataSource, SqlParamInterceptor sqlParamInterceptor) {
        return new JdbcDbImpl(dataSource, sqlParamInterceptor);
    }

    @Bean
    public DataSource dbToolsDataSource() {
        log.info("=========================");
        log.info("DbToolsConfig.dbToolsDataSource");
        log.info("url: " + dbUrl);
        log.info("username: " + dbUsername);
        log.info("password: " + mask(dbPassword));
        log.info("");

        return DataSourceBuilder.create()
                .url(dbUrl)
                .username(dbUsername)
                .password(dbPassword)
                .driverClassName(dbDriver)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dbToolsDataSource) {
        return new JdbcTemplate(dbToolsDataSource);
    }

    private String mask(String token) {
        return token.substring(0, 3) + UtString.repeat("*", token.length() - 3);
    }

}
