package kz.app.appauth.configuration;

import kz.app.appauth.configuration.webClient.*;
import kz.app.appauth.manager.impl.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class AgentConfiguration {

    @Value("${keycloak.url}")
    private String url;

    @Value("${keycloak.agent.id}")
    private String username;

    @Value("${keycloak.agent.secret}")
    private String password;

    @Value("${keycloak.agent.username}")
    private String keycloakUsername;

    @Value("${keycloak.agent.password}")
    private String keycloakPassword;

    @Bean
    public KeycloakAgentManagerExt keycloakAgentManager() {
        return new KeycloakAgentManagerExt(WebClientFactory.createWebClient(url, username, password), username, password, keycloakUsername, keycloakPassword);
    }
}