package kz.app.appauth.service.auth.impl;

import jakarta.servlet.http.*;
import kz.app.appauth.exceptions.*;
import kz.app.appauth.manager.*;
import kz.app.appauth.persistance.constant.*;
import kz.app.appauth.persistance.entity.*;
import kz.app.appauth.service.*;
import kz.app.appauth.service.auth.*;
import kz.app.appcore.model.*;
import kz.app.appcore.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.security.*;
import java.util.*;

@Slf4j
@AllArgsConstructor
abstract class AuthService implements IAuthService {

    private final Map<String, UserEntity> userCache;
    private final UsrAttributeService usrAttributeService;
    private final UserService userService;
    private final GrpService grpService;
    private final IKeycloakManager keycloakManager;
    private final ContextManager contextManager;

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {

        throw new Exception("Not implemented");
    }

    @Override
    public void login(String login, String password) {

        DbRec credentials = authInKeycloak(login, password);

        contextManager.createContext(credentials);
    }

    @Override
    public DbRec authInKeycloak(String login, String password) {

        return keycloakManager.authenticate(login, password);
    }

    @Transactional
    @Override
    public long signUp(
            String username,
            String password,
            String email,
            String firstName,
            String lastName
    ) throws Exception {

        long usrId = userService.save(username, email);

        grpService.addUserInGroup(DefaultUserInfo.DEFAULT_GRP, usrId);
        usrAttributeService.setAttrOwnUser(usrId, true);

        try {
            keycloakManager.createUser(usrId, username, password, email, firstName, lastName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Не удалось создать пользователя keycloack: " + e.getMessage());
        }

        return usrId;
    }

    @Override
    public void dropUserFromCache() throws Exception {

        Long userId = userService.getCurrentUsrId();

        Set<String> keys = userCache.keySet();

        for (String key : keys) {
            UserEntity user = userCache.get(key);
            if (Objects.equals(userId, user.getId())) {
                userCache.remove(key);
                break;
            }
        }
    }

    public DbRec recCredentials(HttpServletRequest request) {

        String cred = request.getHeader("authorization");
        String data = new String(UtString.decodeBase64(cred.split(" ")[1]));

        //
        String[] datas = data.split(":");
        if (datas.length != 2) {
            throw new AuthException(HttpStatusCode.valueOf(401), "Имя пользователя и пароль не указаны");
        }

        //
        String login = datas[0];
        String password = datas[1];
        if (login.equals("null")) {
            throw new AuthException(HttpStatusCode.valueOf(401), "Имя пользователя и пароль не указаны");
        }

        return new DbRec(UtCnv.toMap("login", login, "password", password));
    }

    public DbRec buildUserResponse(HttpServletRequest request) throws Exception {
        //
        DbRec recCredentials = recCredentials(request);
        String login = recCredentials.getString("login");

        //
        UserEntity usr = userService.findByUsername(login);

        return new DbRec(UtCnv.toMap(
                "id", usr.getId(),
                "name", usr.getUsername(),
                "login", usr.getEmail()
        ));
    }

    @Override
    public DbRec authInKeycloak(HttpServletRequest request) throws Exception {

        // Получаем Логин и Пароль
        DbRec recCredentials = recCredentials(request);
        String login = recCredentials.getString("login");
        String password = recCredentials.getString("password");

        // Авторизуемся в keycloak
        DbRec credentials;
        try {
            credentials = authInKeycloak(login, password);
        } catch (Exception e) {
            throw new AuthException(HttpStatusCode.valueOf(401), "Имя пользователя и пароль указаны не верно");
        }

        return credentials;
    }
}