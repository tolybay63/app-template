package kz.kis.kisauth.manager.impl;

import jakarta.servlet.http.*;
import kz.kis.kisauth.manager.*;
import kz.kis.kisauth.persistance.constant.*;
import kz.kis.kiscore.model.*;
import kz.kis.kiscore.utils.*;
import kz.kis.kisdbtools.repository.*;
import org.springframework.core.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.util.*;
import org.springframework.web.reactive.function.*;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.*;

import java.util.*;

abstract class KeycloakManager implements IKeycloakManager {

    private final WebClient webClient;

    private final String username;

    private final String password;

    private final String keycloakUsername;

    private final String keycloakPassword;

    public KeycloakManager(WebClient webClient, String username, String password, String keycloakUsername, String keycloakPassword) {

        this.webClient = webClient;
        this.username = username;
        this.password = password;
        this.keycloakUsername = keycloakUsername;
        this.keycloakPassword = keycloakPassword;
    }

    @Override
    public void resetPassword(String password, String id) {

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);

        webClient.put()
                .uri(it -> it.path("/admin/realms/master/users/" + id + "/reset-password").build())
                .header("Authorization", "Bearer " + getAdminToken())
                .bodyValue(credentials)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

    }

    @Override
    public DbRec getUserByName(String username, Long id) {

        List<DbRec> result = webClient.get()
                .uri(it -> it.path("/admin/realms/master/users").queryParam("username", username).build())
                .header("Authorization", "Bearer " + getAdminToken())
                .retrieve()
                .bodyToMono(new  ParameterizedTypeReference<List<DbRec>>() {})
                .block();

        if (result == null || result.isEmpty()) {
            return null;
        }

        for  (DbRec user : result) {
            Map<String, Object> attr = UtCnv.toMap(user.get("attributes"));

            if (attr.containsKey("user_id")) {
                List<String> ids = UtCnv.toList(attr.get("user_id"));
                if (id == UtCnv.toLong(ids.getFirst())) {
                    return user;
                }
            }
        }

        return null;
    }

    @Override
    public void createUser(Long usrId, String username, String password, String email, String firstName, String lastName) {

        Map<String, Object> credentials = new HashMap<>();

        Map<String, Object> body = new HashMap<>();

        if (password != null && !password.isEmpty()) {
            credentials.put("type", "password");
            credentials.put("value", password);
            credentials.put("temporary", false);

            body.put("credentials", List.of(credentials));
        }

        body.put("username", username);
        body.put("enabled", true);
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("email", email);
        body.put("attributes", UtCnv.toMap("user_id", List.of(usrId)));

        webClient.post()
                .uri(it -> it.path("/admin/realms/master/users").build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + getAdminToken())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void setUserID(String keycloakUserId, Long userId) {

        DbRec userData = getUserData(keycloakUserId);

        Map<String, Object> attr;
        if (userData.containsKey("attributes")) {
            attr = UtCnv.toMap(userData.get("attributes"));
        } else {
            attr = new HashMap<>();
        }

        attr.put("user_id", List.of(userId));

        webClient.put()
                .uri(it -> it.path("/admin/realms/master/users/" + keycloakUserId).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + getAdminToken())
                .bodyValue(UtCnv.toMap("attributes", attr))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void logout() {

        webClient.post()
                .uri(it -> it.path("/realms/master/protocol/openid-connect/logout")
                        .build()
                )
                .header("Authorization", "Bearer " + getUserToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public DbRec authenticate(String username, String password) {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        body.add("scope", "openid");

        return webClient.post()
                .uri(it -> it.path("/realms/master/protocol/openid-connect/token").build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body)
                )
                .retrieve()
                .bodyToMono(DbRec.class)
                .block();
    }

    @Override
    public DbRec refreshToken(String refreshToken) {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", username);
        body.add("client_secret", password);
        body.add("refresh_token", refreshToken);

        try {
            return webClient.post()
                    .uri(it -> it.path("/realms/master/protocol/openid-connect/token").build())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromMultipartData(body)
                    )
                    .retrieve()
                    .bodyToMono(DbRec.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public DbRec getUserData(String keycloakUserId) {

        return webClient.get()
                .uri(it -> it.path("/admin/realms/master/users/" + keycloakUserId).build())
                .header("Authorization", "Bearer " + getAdminToken())
                .retrieve()
                .bodyToMono(DbRec.class)
                .block();
    }

    @Override
    public List<DbRec> findAllUsers(int offset, int size) {

        return webClient.get()
                .uri(it -> it.path("/admin/realms/master/users").queryParam("first", offset).queryParam("max", size).build())
                .header("Authorization", "Bearer " + getAdminToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DbRec>>() {
                })
                .block();
    }

    @Override
    public List<DbRec> findUserGroups(String userId) {

        return webClient.get()
                .uri(it -> it.path("/admin/realms/master/users/" + userId + "/groups").build())
                .header("Authorization", "Bearer " + getAdminToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DbRec>>() {})
                .block();
    }

    @Override
    public void deleteUser(String userId) {

        webClient.delete()
                .uri(it -> it.path("/admin/realms/master/users/" + userId).build())
                .header("Authorization", "Bearer " + getAdminToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public Boolean isUserAD(DbRec userData) {

        Map<String, Object> attr = UtCnv.toMap(userData.get("attributes"));

        return attr.containsKey("LDAP_ID");
    }

    @Override
    public void addIgnoreLDAP(String userName) {

        DbRec federation = getLdapFederation();

        if (federation == null) {
            return;
        }

        DbRec config = new DbRec(UtCnv.toMap(federation.get("config")));

        String filter = UtCnv.toList(config.get("customUserSearchFilter")).getFirst();

        String newFilter = buildLdapFilter(filter, userName);

        config.put("customUserSearchFilter", List.of(newFilter));

        federation.put("config", config);

        setLdapFederation(federation);
    }

    @Override
    public void deleteFromIgnoreLDAP(DbRec userData) {

        DbRec federation = getLdapFederation();

        if (federation == null) {
            return;
        }

        DbRec config = new DbRec(UtCnv.toMap(federation.get("config")));

        String filter = UtCnv.toList(config.get("customUserSearchFilter")).getFirst();

        String newFilter = removeFromLdapFilter(filter, UtCnv.toString(userData.get("username")));

        config.put("customUserSearchFilter", List.of(newFilter));

        federation.put("config", config);

        setLdapFederation(federation);
    }

    private DbRec getLdapFederation() {

        List<DbRec> userFederation = webClient.get()
                .uri(it->it.path("/admin/realms/master/components").queryParam("providerType", "org.keycloak.storage.UserStorageProvider").queryParam("name", "LDAP_AD").build())
                .header("Authorization", "Bearer " + getAdminToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DbRec>>() {
                })
                .block();

        if (userFederation == null || userFederation.isEmpty()){
            return null;
        }

        return new DbRec(userFederation.getFirst());
    }

    private void setLdapFederation(DbRec federation) {

        webClient.put()
                .uri(it -> it.path("/admin/realms/master/components/" + UtCnv.toString(federation.get("id"))).build())
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(federation)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    // Обновляем фильтр ldap в keycloak
    private String buildLdapFilter(String filter, String userName) {

        return "(" + filter.substring(1, filter.length() - 1) + "(" + "!" + "(" + "sAMAccountName" + "=" + userName + ")" + ")" + ")";
    }

    private String removeFromLdapFilter(String filter, String userName) {

        String filterUser = "(!(sAMAccountName=" + userName + "))";

        return filter.replace(filterUser, "");
    }

    private String getAdminToken() {

        DbRec credentials = authenticate(keycloakUsername, keycloakPassword);

        return UtCnv.toString(credentials.get(CookieNames.AUTH_TOKEN));
    }

    private String getUserToken() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getCredentials();

        return jwt.getTokenValue();
    }
}