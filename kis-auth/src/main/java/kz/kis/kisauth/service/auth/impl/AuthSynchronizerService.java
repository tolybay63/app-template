package kz.kis.kisauth.service.auth.impl;

import jakarta.servlet.http.*;
import kz.kis.kisauth.exceptions.*;
import kz.kis.kisauth.manager.*;
import kz.kis.kisauth.manager.impl.*;
import kz.kis.kisauth.persistance.constant.*;
import kz.kis.kisauth.persistance.entity.*;
import kz.kis.kisauth.service.*;
import kz.kis.kiscore.model.*;
import kz.kis.kiscore.utils.*;
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
