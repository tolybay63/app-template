package kz.app.appauth.manager;

import jakarta.servlet.http.*;
import kz.app.appcore.model.*;
import org.springframework.security.oauth2.jwt.*;

public interface ContextManager {

    void init();

    void createContext(DbRec credentials);

    void deleteContext(HttpServletRequest request, HttpServletResponse response);

    String getAttribute(String key);

    void removeAttribute(String key);

    void setAttribute(String key, String value);

    Jwt decodeJwt(String accessToken);

    Jwt decodeJwt();

    DbRec jwtClaims(Jwt jwt);

    String getSessionId();
}