package kz.app.appauth.manager.impl;

import org.springframework.web.reactive.function.client.*;

public class KeycloakWebClientManagerExt extends KeycloakManager {
    public KeycloakWebClientManagerExt(WebClient webClient, String username, String password, String keycloakUsername, String keycloakPassword) {
        super(webClient, username, password, keycloakUsername, keycloakPassword);
    }
}