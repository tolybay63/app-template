package kz.app.appauth.service;

import kz.app.appauth.dao.*;
import kz.app.appauth.manager.*;
import kz.app.appauth.manager.impl.*;
import kz.app.appauth.persistance.entity.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Service
@AllArgsConstructor
public class UsrAttributeService {

    private final UsrAttributeDAO usrAttributeDAO;
    private final KeycloakWebClientManagerExt keycloakClient;
    private final UserService userService;

    public void setAttrOwnUser(Long usr, boolean ownUser) throws Exception {

        usrAttributeDAO.setAttrOwnUser(usr, ownUser);
    }

    @Transactional
    public void setAttrOwnUserAndCheckKeycloak(Long usr, boolean ownUser) throws Exception {

        UserEntity user = userService.findById(usr);

        DbRec userData = keycloakClient.getUserByName(user.getUsername(), user.getId());

        String keycloakId = UtCnv.toString(userData.get("id"));
        if (ownUser) {
            // Проверить есть этот пользователь в АД
            if (keycloakClient.isUserAD(userData)) {

                // Добавляем в игнор для keycloak,
                // Keycloak после этого сам удалит польщователя из своей базы
                keycloakClient.addIgnoreLDAP(user.getUsername());

                // Создать нового простого пользователя
                keycloakClient.createUser(usr, UtCnv.toString(userData.get("username")), "", UtCnv.toString(userData.get("email")), UtCnv.toString(userData.get("firstName")), UtCnv.toString(userData.get("lastName")));
            }
        } else {
            // Проверить простой ли это пользователь?
            if (!keycloakClient.isUserAD(userData)) {

                // Если пользователь простой, тогда удалить его из keycloak и ожидать его из АД
                keycloakClient.deleteUser(keycloakId);

                // Снимаем из игнора LDAP
                keycloakClient.deleteFromIgnoreLDAP(userData);

                // Задаем user_id
            }
        }

        setAttrOwnUser(usr, ownUser);
    }
}