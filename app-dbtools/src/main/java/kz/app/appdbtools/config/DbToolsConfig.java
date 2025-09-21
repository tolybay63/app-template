package kz.app.appdbtools.config;

import kz.app.appdbtools.repository.Db;
import kz.app.appdbtools.repository.SqlParamInterceptor;
import kz.app.appdbtools.repository.impl.JdbcDbImpl;
import kz.app.appdbtools.repository.impl.LoggingParamInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import util.UtMask;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "kz.app.appdbtools")
//@PropertySource("classpath:application.properties")
public class DbToolsConfig {

    private static final Logger log = LoggerFactory.getLogger(DbToolsConfig.class);

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
    @Primary
    public Db db(DataSource dataSource, SqlParamInterceptor sqlParamInterceptor) {
        return new JdbcDbImpl(dataSource, sqlParamInterceptor);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("=========================");
        log.info("DbToolsConfig.dataSource");
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

/*
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dbToolsDataSource) {
        return new JdbcTemplate(dbToolsDataSource);
    }
*/


}
