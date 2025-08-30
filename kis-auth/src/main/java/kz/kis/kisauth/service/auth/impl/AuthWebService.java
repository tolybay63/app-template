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
public class AuthWebService extends AuthService {

    private final UserService userService;
    private final PermissionService permissionService;
    private final GrpService grpService;
    private final KeycloakWebClientManagerExt keycloakManager;
    private final ContextManager contextManager;

    public AuthWebService(
            Map<String, UserEntity> userCache,
            UsrAttributeService usrAttributeService,
            UserService userService,
            PermissionService permissionService,
            GrpService grpService,
            KeycloakWebClientManagerExt keycloakManager,
            ContextManager contextManager
    ) {
        //
        super(userCache, usrAttributeService, userService, grpService, keycloakManager, contextManager);

        //
        this.userService = userService;
        this.permissionService = permissionService;
        this.grpService = grpService;
        this.keycloakManager = keycloakManager;
        this.contextManager = contextManager;
    }

    @Transactional
    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {

        contextManager.createContext(authInKeycloak(request));

        // Расшифровывем токен доступа
        DbRec jwtClaims = contextManager.jwtClaims(contextManager.decodeJwt());

        // Ищем пользователя в базе данных SC
        String login = jwtClaims.getString("preferred_username");
        UserEntity user = userService.findByUsername(login);

        // Если пользователь не найден в бд, то создаем его.
        // ID SC кладем в keycloak и получаем обновленный токен с ID SC
        if (user == null) {
            // Создаем пользователя в базе SC
            long id = userService.save(login, jwtClaims.getString("email"));
            grpService.addUserInGroup(DefaultUserInfo.DEFAULT_GRP, id);

            // Устанавливаем SC ID в keycloak
            keycloakManager.setUserID(UtCnv.toString(jwtClaims.getString("sub")), id);

            // Обновляем кэш прав доступа
            permissionService.refreshPermissionsCache(Collections.singletonList(id));

            // Перелогиниваемся в keycloak и получаем токен с ID SC
            contextManager.createContext(authInKeycloak(request));

        } else {
            // Если пользователь существует и в keycloak и в SC, но в keycloak нету его ID SC
            // То кладем ID SC в keycloak
            if (!jwtClaims.containsKey(CookieNames.USERID) || jwtClaims.getLong(CookieNames.USERID) != user.getId()) {
                // Кладем ID SC в keycloak
                keycloakManager.setUserID(UtCnv.toString(jwtClaims.getString("sub")), user.getId());

                // Перелогиниваемся в keycloak и получаем токен с ID SC
                contextManager.createContext(authInKeycloak(request));
            }
        }
    }
}