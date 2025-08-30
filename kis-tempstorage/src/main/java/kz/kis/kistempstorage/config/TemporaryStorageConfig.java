package kz.kis.kistempstorage.config;

import kz.kis.kisdbtools.config.DbToolsConfig;
import kz.kis.kisdbtools.repository.Db;
import kz.kis.kistempstorage.PostgreMessageDataStorage;
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
