package kz.app.apptempstorage.config;

import kz.app.appdbtools.config.DbToolsConfig;
import kz.app.appdbtools.repository.Db;
import kz.app.apptempstorage.PostgreMessageDataStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DbToolsConfig.class)
public class TemporaryStorageConfig {

    private final Db db;

    public TemporaryStorageConfig(Db db) {
        this.db = db;
    }

    @Bean
    public PostgreMessageDataStorage postgreTemporaryStorage() {
        return new PostgreMessageDataStorage(db);
    }
}
