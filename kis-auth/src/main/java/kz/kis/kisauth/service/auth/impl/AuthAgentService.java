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
