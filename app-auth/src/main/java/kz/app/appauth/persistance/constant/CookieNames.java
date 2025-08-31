package kz.app.appauth.persistance.constant;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieNames {
    public static final String SESSION_TYPE = "session_type";
    public static final String AGENT_AUTH_TOKEN = "agent_access_token";
    public static final String AGENT_REFRESH_TOKEN = "agent_refresh_token";
    public static final String AUTH_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String SYNCHRONIZER_TOKEN = "synchronizer_token";
    public static final String SEED = "seed";
    public static final String USERID = "user_id";

    public static String extractFromCookie(HttpServletRequest request, String cookieName) {

        throw new RuntimeException("not implemented");
    }
}