package kz.kis.kisauth.manager.impl;

import org.springframework.web.reactive.function.client.*;

public class KeycloakAgentManagerExt extends KeycloakManager {
    public KeycloakAgentManagerExt(WebClient webClient, String username, String password, String keycloakUsername, String keycloakPassword) {
        super(webClient, username, password, keycloakUsername, keycloakPassword);
    }
}
