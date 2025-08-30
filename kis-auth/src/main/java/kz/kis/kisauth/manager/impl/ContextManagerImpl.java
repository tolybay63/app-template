package kz.kis.kisauth.manager.impl;

import jakarta.servlet.http.*;
import kz.kis.kisauth.manager.*;
import kz.kis.kisauth.persistance.constant.*;
import kz.kis.kiscore.model.*;
import kz.kis.kiscore.utils.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.authentication.logout.*;
import org.springframework.stereotype.*;

import java.net.http.*;

@Service
@AllArgsConstructor
public class ContextManagerImpl implements ContextManager {

    private final JwtDecoder jwtDecoder;

    private final HttpSession session;

    private final KeycloakWebClientManagerExt keycloakWebClientManagerExt;

    private final KeycloakAgentManagerExt keycloakAgentManagerExt;

    private final KeycloakSynchronizeManagerExt keycloakSynchronizeManagerExt;

    @Override
    public void init() {

        String accessToken = getAttribute(CookieNames.AUTH_TOKEN);
        String refreshToken = getAttribute(CookieNames.REFRESH_TOKEN);

        try {

            Jwt jwt = decodeJwt(accessToken);

            authenticate(jwt);
        } catch (Exception e) {
            String sessionType = getAttribute(CookieNames.SESSION_TYPE);

            DbRec credentials;
            if (sessionType.equals(UtCnv.toString(SessionTypes.WEB_SESSION))) {
                credentials = keycloakWebClientManagerExt.refreshToken(refreshToken);
            } else if (sessionType.equals(UtCnv.toString(SessionTypes.AGENT_SESSION))) {
                credentials = keycloakAgentManagerExt.refreshToken(refreshToken);
            } else if (sessionType.equals(UtCnv.toString(SessionTypes.SYNCHRONIZER_SESSION))) {
                credentials = keycloakSynchronizeManagerExt.refreshToken(refreshToken);
            } else {
                throw new RuntimeException("Invalid session type: " + sessionType);
            }

            accessToken = credentials.getString(CookieNames.AUTH_TOKEN);
            refreshToken = credentials.getString(CookieNames.REFRESH_TOKEN);

            Jwt jwt = decodeJwt(accessToken);
            authenticate(jwt);

            setAttribute(CookieNames.AUTH_TOKEN, accessToken);
            setAttribute(CookieNames.REFRESH_TOKEN, refreshToken);
        }
    }

    @Override
    public void createContext(DbRec credentials) {

        String accessToken = credentials.getString(CookieNames.AUTH_TOKEN);

        Jwt jwt = decodeJwt(accessToken);

        if (!jwtClaims(jwt).containsKey(CookieNames.USERID)) {
            throw new RuntimeException("User id not found");
        }

        authenticate(jwt);

        if (credentials.containsKey(CookieNames.SESSION_TYPE)) {
            setAttribute(CookieNames.SESSION_TYPE, credentials.getString(CookieNames.SESSION_TYPE));
        } else {
            setAttribute(CookieNames.SESSION_TYPE, UtCnv.toString(SessionTypes.WEB_SESSION));
        }

        setAttribute(CookieNames.AUTH_TOKEN, accessToken);
        setAttribute(CookieNames.REFRESH_TOKEN, credentials.getString(CookieNames.REFRESH_TOKEN));
    }

    @Override
    public void deleteContext(HttpServletRequest request, HttpServletResponse response) {

        keycloakWebClientManagerExt.logout();

        this.session.invalidate();
        this.session.removeAttribute(CookieNames.AUTH_TOKEN);
        this.session.removeAttribute(CookieNames.REFRESH_TOKEN);
    }

    @Override
    public String getAttribute(String key) {

        return UtCnv.toString(this.session.getAttribute(key));
    }

    @Override
    public void removeAttribute(String key) {

        this.session.removeAttribute(key);
    }

    @Override
    public void setAttribute(String key, String value) {

        this.session.setAttribute(key, value);
    }

    @Override
    public Jwt decodeJwt(String accessToken) {

        return jwtDecoder.decode(accessToken);
    }

    @Override
    public Jwt decodeJwt() {

        return decodeJwt(getAttribute(CookieNames.AUTH_TOKEN));
    }

    @Override
    public DbRec jwtClaims(Jwt jwt) {

        return new DbRec(jwt.getClaims());
    }

    @Override
    public String getSessionId() {

        return this.session.getId();
    }

    private void authenticate(Jwt jwt) {

        Authentication authentication = new JwtAuthenticationToken(jwt);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}