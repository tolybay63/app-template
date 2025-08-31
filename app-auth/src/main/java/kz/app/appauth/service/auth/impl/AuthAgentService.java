package kz.app.appauth.service.auth.impl;

import jakarta.servlet.http.*;
import kz.app.appauth.exceptions.*;
import kz.app.appauth.manager.*;
import kz.app.appauth.manager.impl.*;
import kz.app.appauth.persistance.constant.*;
import kz.app.appauth.persistance.entity.*;
import kz.app.appauth.service.*;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Service
public class AuthAgentService extends AuthService {

    public AuthAgentService(
            Map<String, UserEntity> userCache,
            UsrAttributeService usrAttributeService,
            UserService userService,
            GrpService grpService,
            KeycloakAgentManagerExt keycloakManager,
            ContextManager contextManager
    ) {
        //
        super(userCache, usrAttributeService, userService, grpService, keycloakManager, contextManager);
    }

    public DbRec authAgent(HttpServletRequest request) throws Exception {

        DbRec credentials = authInKeycloak(request);

        return new DbRec(UtCnv.toMap(CookieNames.AUTH_TOKEN, credentials.getString(CookieNames.AUTH_TOKEN),
                CookieNames.REFRESH_TOKEN, credentials.getString(CookieNames.REFRESH_TOKEN))
        );
    }
}
