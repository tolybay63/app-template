package kz.app.appauth.exceptions;

import lombok.*;
import net.minidev.json.*;
import org.springframework.http.*;
import org.springframework.security.access.*;

import java.util.*;

@Getter
public class PermissionException extends RuntimeException {
    HttpStatusCode httpStatusCode;

    public PermissionException(HttpStatusCode status, String message) {
        super(new JSONObject(UtCnv.toMap("status", status.value(), "message", message)).toJSONString());
        this.httpStatusCode = status;
    }
}