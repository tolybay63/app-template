package kz.app.appauth.service;

import kz.app.appauth.manager.impl.*;
import lombok.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@AllArgsConstructor
public class UserSynchronizer {

    private static final Logger log = LoggerFactory.getLogger(UserSynchronizer.class);

    private final KeycloakWebClientManagerExt keycloakAuthenticationManager;

    private final UserService userService;

    private final GrpService grpService;

    // @Scheduled(fixedDelay = 5000)
    public void synchronize() {
        int pageSize = 100;
        int offset = 0;

        while (true) {
            List<DbRec> keycloakUsers = keycloakAuthenticationManager.findAllUsers(offset, pageSize);

            //
            log.info("findAllUsers, offset: {}", offset);

            if (keycloakUsers.isEmpty()) {
                break;
            }

            //
            keycloakUsers.forEach(keycloakUser -> {
                log.info("keycloakUser: {}, id: {}", keycloakUser.get("username"), keycloakUser.get("id"));
                try {
                    Map attributes = UtCnv.toMap(keycloakUser.get("attributes"));
                    String userId = UtCnv.toString(keycloakUser.get("id"));
                    List usrGroups = keycloakAuthenticationManager.findUserGroups(userId);
                    checkOrCreateUsr(keycloakUser, attributes);
                    checkOrCreateUsrGrp(attributes, usrGroups);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }

            });

            //
            offset = offset + pageSize;
        }
    }

    private void checkOrCreateUsrGrp(Map attributes, List<Map> usrGroups) throws Exception {
        // Любые атрибуты в keycloak - это список
        long userId = UtCnv.toLong(((List) attributes.get("user_id")).get(0));

        // Текущие группы у нас
        Map<Long, Boolean> usrGrps = grpService.getGrpsByUsr(userId);

        // Синхронизируем наши группы с группами keycloak
        usrGroups.forEach(group -> {
            String grpName = UtCnv.toString(group.get("name"));
            try {
                long grpId = grpService.findGrp(grpName);

                if (grpId == 0L) {
                    grpId = grpService.createGrp(grpName);
                    saveUsrGrp(usrGrps, grpId, userId);

                } else {
                    if (usrGrps.containsKey(grpId)) {
                        if (!grpService.grpContainsUsr(grpId, userId)) {
                            // todo разрешить (ипередалать) после #190
                            // grpService.deleteUsrFromGrp(grpId, userId);
                        }
                    } else {
                        saveUsrGrp(usrGrps, grpId, userId);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void checkOrCreateUsr(Map user, Map keycloakAttributes) throws Exception {
        // Отсутствие keycloakAttributes.user_id говорит о том,
        // что юзер в кейклоаке не имеет связи с юзером в БД
        if (!keycloakAttributes.containsKey("user_id")) {
            String username = UtCnv.toString(user.get("username"));
            String email = UtCnv.toString(user.get("email"));
            String keycloakId = UtCnv.toString(user.get("id"));
            long userId = save(username, email, keycloakId);
            // Любые атрибуты в keycloak - это список
            keycloakAttributes.put("user_id", List.of(userId));
        }
    }

    private void saveUsrGrp(Map<Long, Boolean> usrGrps, Long grpId, Long userId) throws Exception {
        usrGrps.put(grpId, true);
        grpService.addUserInGroup(grpId, userId);
    }

    //@Transactional
    private Long save(String username, String email, String keycloakId) throws Exception {
        Long userId = userService.findUserId(username);
        if (userId == 0L) {
            userId = userService.save(username, email);
        }
        keycloakAuthenticationManager.setUserID(keycloakId, userId);

        return userId;
    }
}