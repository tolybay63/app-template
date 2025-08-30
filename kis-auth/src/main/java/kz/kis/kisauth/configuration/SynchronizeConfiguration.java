package kz.kis.kisauth.configuration;

import kz.kis.kisauth.configuration.webClient.*;
import kz.kis.kisauth.manager.impl.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class SynchronizeConfiguration {

    @Value("${keycloak.url}")
    private String url;

    @Value("${keycloak.sync.id}")
    private String username;

    @Value("${keycloak.sync.secret}")
    private String password;

    @Value("${keycloak.sync.username}")
    private String keycloakUsername;

    @Value("${keycloak.sync.password}")
    private String keycloakPassword;

    @Bean
    public KeycloakSynchronizeManagerExt keycloakSynchronizeManager() {
        return new KeycloakSynchronizeManagerExt(WebClientFactory.createWebClient(url, username, password), username, password, keycloakUsername, keycloakPassword);
    }
}