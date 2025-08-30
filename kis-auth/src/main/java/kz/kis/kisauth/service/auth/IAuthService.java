package kz.kis.kisauth.service.auth;

import jakarta.servlet.http.*;
import kz.kis.kiscore.model.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.*;

@Service
public interface IAuthService {

    void login(HttpServletRequest request, HttpServletResponse response) throws Exception;

    void login(String login, String password);

    long signUp(String username, String password, String email, String firstName, String lastName) throws Exception;

    void dropUserFromCache() throws Exception;

    DbRec authInKeycloak(String login, String password);

    DbRec authInKeycloak(HttpServletRequest request) throws Exception;
}