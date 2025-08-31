package kz.app.appauth.configuration.filters.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.app.appauth.exceptions.*;
import kz.app.appauth.manager.*;
import kz.app.appauth.persistance.constant.*;
import kz.app.appauth.service.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Service
public class JwtTokenFilter extends OncePerRequestFilter {

    private final ContextManager contextManager;

    public JwtTokenFilter(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (isLogout(request)) {
            doFilter(request, response, filterChain);
            return;
        }

        if (isLogin(request)) {
            doFilter(request, response, filterChain);
            return;
        }

        try {
            contextManager.init();
        } catch (Exception e) {
            throw new AuthException(HttpStatusCode.valueOf(401), "Ошибка авторизации, код: 1");
        }

        filterChain.doFilter(request, response);
    }

    private Boolean isLogin(HttpServletRequest request) {

        return Objects.equals(request.getServletPath(), "/sync/auth/login") || Objects.equals(request.getServletPath(), "/auth/login") || Objects.equals(request.getServletPath(), "/auth/agent/login");
    }

    private Boolean isLogout(HttpServletRequest request) {

        return Objects.equals(request.getServletPath(), "/auth/logout");
    }
}