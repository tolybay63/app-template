package kz.kis.kisauth.exceptions;

import lombok.*;
import org.springframework.http.*;
import org.springframework.security.core.*;

@Getter
public class AuthException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public AuthException(HttpStatusCode statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }
}
