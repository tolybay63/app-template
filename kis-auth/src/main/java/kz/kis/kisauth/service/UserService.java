package kz.kis.kisauth.service;

import com.nimbusds.jwt.*;
import jakarta.servlet.http.*;
import kz.kis.kisauth.manager.*;
import kz.kis.kisauth.manager.impl.*;
import kz.kis.kisauth.persistance.constant.*;
import kz.kis.kisauth.persistance.entity.*;
import kz.kis.kiscore.model.*;
import kz.kis.kiscore.utils.*;
import kz.kis.kisdbtools.repository.*;
import kz.kis.kisfile.constants.*;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.*;

import java.util.*;

import static kz.kis.kisauth.persistance.constant.CookieNames.*;

@Service
@AllArgsConstructor
public class UserService {
    private final Db db;
    private final Map<String, UserEntity> usersCache;
    private final Map<Long, Object> userPermissionsCache;
    private final PermissionManager permissionManager;
    private final HttpServletRequest request;
    private final KeycloakWebClientManagerExt keycloakManager;
    private final ContextManager contextManager;

    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            UserEntity user = findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException(username);
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsernameById(Long id) throws Exception {

        return findById(id).getUsername();
    }

    public UserEntity findById(Long id) throws Exception {

        return findUserByParams(new DbRec(UtCnv.toMap("id", id)));
    }

    public UserEntity findByUsername(String username) throws Exception {

        return findUserByParams(new DbRec(UtCnv.toMap("name", username)));
    }

    public Long save(String username, String email) throws Exception {

        DbRec params = new DbRec();
        params.put("name", username);
        params.put("email", email);

        return db.insertRec("Usr", params);
    }

    public void update(long id, String username, String email) throws Exception {

        DbRec params = new DbRec();
        params.put("id", id);
        params.put("name", username);
        params.put("email", email);

        db.updateRec("Usr", params);
    }

    //
    private UserEntity findUserByParams(DbRec params) throws Exception {

        List<DbRec> result = db.loadList("Usr", params);
        if (result.isEmpty()) {
            return null;
        }

        DbRec row = result.getFirst();

        UserEntity user = new UserEntity();
        user.setId((Long) row.get("id"));
        user.setEmail((String) row.get("email"));
        user.setUsername((String) row.get("name"));

        return user;
    }

    /**
     * Ищет в кэше или в БД текущего пользователя
     *
     * @return UserEntity
     * @throws Exception в случае ошибок в JdbcTemplate
     */
    public UserEntity getCurrentUser() throws Exception {
        // Из контекста получаем текущую авторизацию, в которой лежит JWT токен
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Если auth = null тогда авторизация не была выполнена, вместо пользователя возвращаем null
        if (auth == null) {
            return null;
        }

        // Из текущей авторизации получаем JWT токен
        Jwt jwt = (Jwt) auth.getPrincipal();

        // Claims это расшифровка JWT токена
        // Там можно получить сведения о текущей сессии пользователя, его id и username
        DbRec claims = new DbRec(jwt.getClaims());

        // По ID из токена получаем текущего пользователя из кэша
        String keycloakId = UtCnv.toString(claims.get("sub"));
        UserEntity user = usersCache.get(keycloakId);

        // Если в кэше не нашли, то ищем в БД и помещаем в кэш
        if (user == null) {
            user = findByUsername(UtCnv.toString(claims.get("preferred_username")));
            usersCache.put(UtCnv.toString(claims.get("sub")), user);
            userPermissionsCache.putAll(permissionManager.getUserPermissions(List.of(user.getId())));
        }

        // Возвращаем найденного пользователя
        return user;
    }

    public void resetPassword(long id, String password) throws Exception {

        UserEntity user = findById(id);
        if (user == null) {
            return;
        }

        DbRec userData = keycloakManager.getUserByName(user.getUsername(), id);

        keycloakManager.resetPassword(password, userData.getString("id"));
    }

    public Long getCurrentUsrId() throws Exception {

        return getCurrentUser().getId();
    }

    public Long getCurrentUsrIdFromAuth() {

        try {
            String accessToken = contextManager.getAttribute(AUTH_TOKEN);

            return getCurrentUsrIdFromAuth(accessToken);
        } catch (Exception e) {
            return -1L;
        }
    }

    public Long getCurrentUsrIdFromAuth(String accessToken) {

        try {
            JWT jwt = JWTParser.parse(accessToken);

            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            return UtCnv.toLong(claims.getClaim(USERID));
        } catch (Exception e) {
            return -1L;
        }
    }

    public List<DbRec> getUserList() throws Exception {

        List<DbRec> list = db.loadList("Usr", null);
        fillUsrGrp(list, "id");
        return list;
    }

    public Long findUserId(String username) throws Exception {

        List<DbRec> result = db.loadList("Usr", UtCnv.toMap("name", username));
        if (result.isEmpty()) {
            return 0L;
        } else {
            Map<String, Object> row = result.getFirst();
            return UtCnv.toLong(row.get("id"));
        }
    }

    /**
     * Заполняет поле UsrGrp для каждого элемента в списке usrs
     *
     * @param usrs     обрабатываемый список
     * @param keyField в каком поле лежит Usr.id
     */
    public void fillUsrGrp(List<DbRec> usrs, String keyField) throws Exception {

        if (usrs.isEmpty()) {
            return;
        }

        Set<Object> ids = UtDb.uniqueValues(usrs, keyField);
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);

        //
        List<DbRec> data = db.loadSql(sqlUsrGrp(), params);
        Map<Object, List<DbRec>> byUsr = UtDb.collectGroupBy_records(data, "usr");

        //
        for (DbRec file : usrs) {
            Object keyFieldValue = file.get(keyField);
            file.put("usrGrp", byUsr.get(keyFieldValue));
        }
    }

    private String sqlUsrGrp() {

        return """
                select
                    Grp.*,
                    UsrGrp.usr
                from
                    UsrGrp
                    left join Grp on (
                        UsrGrp.grp = Grp.id
                    )
                where
                    UsrGrp.usr in (:{ids})
                """;
    }
}