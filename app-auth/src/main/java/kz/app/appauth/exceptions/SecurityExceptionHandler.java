package kz.app.appauth.exceptions;

import jakarta.servlet.http.*;
import kz.app.appcore.utils.*;
import net.minidev.json.*;

public class SecurityExceptionHandler {

    public static void handle(HttpServletResponse response, int statusCode, Exception exception) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(new JSONObject(UtCnv.toMap("status", statusCode, "message", exception.getMessage())).toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
