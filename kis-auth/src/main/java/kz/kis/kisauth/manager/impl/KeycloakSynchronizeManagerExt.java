package kz.kis.kisauth.manager.impl;

import org.springframework.web.reactive.function.client.*;

public class KeycloakSynchronizeManagerExt extends KeycloakManager {
    public KeycloakSynchronizeManagerExt(WebClient webClient, String username, String password, String keycloakUsername, String keycloakPassword) {
        super(webClient, username, password, keycloakUsername, keycloakPassword);
    }
}
