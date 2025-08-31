package kz.app.appauth.service.auth.impl;

import jakarta.servlet.http.*;
import kz.app.appauth.exceptions.*;
import kz.app.appauth.manager.*;
import kz.app.appauth.manager.impl.*;
import kz.app.appauth.persistance.constant.*;
import kz.app.appauth.persistance.entity.*;
import kz.app.appauth.service.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class AuthSynchronizerService extends AuthService {

    private final ContextManager contextManager;

    public AuthSynchronizerService(
            Map<String, UserEntity> userCache,
            UsrAttributeService usrAttributeService,
            UserService userService,
            GrpService grpService,
            KeycloakSynchronizeManagerExt keycloakManager,
            ContextManager contextManager
    ) {

        super(userCache, usrAttributeService, userService, grpService, keycloakManager, contextManager);
        this.contextManager = contextManager;
    }

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {

        contextManager.createContext(authInKeycloak(request));
    }

    public DbRec recCredentialsSession() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return null;
        }

        Jwt jwt = (Jwt) auth.getPrincipal();

        return new DbRec(UtCnv.toMap("token", jwt.getTokenValue()));
    }
}
