package kz.app.appauth.configuration.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import kz.app.appauth.exceptions.*;
import kz.app.appauth.manager.*;
import kz.app.appauth.manager.impl.*;
import kz.app.appauth.persistance.entity.*;
import kz.app.appauth.service.*;
import kz.app.appcore.utils.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

import java.io.*;
import java.util.*;

import static kz.app.appauth.persistance.constant.CookieNames.*;
import static kz.app.appauth.persistance.constant.CookieNames.SEED;
import static kz.app.appauth.persistance.constant.PermissionPath.*;

@Component
@AllArgsConstructor
public class PermissionFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final PermissionManager permissionManager;
    private final PermissionService permissionService;
    private final ContextManager contextManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        UserEntity user;

        try {
            user = userService.getCurrentUser();
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Обновляем кэш прав доступа если надо
        try {
            if (!request.getSession().getId().equals(contextManager.getSessionId())) {
                permissionService.refreshPermissionsCache(Collections.singletonList(userService.getCurrentUsrId()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Получает пакет параметров для проверки прав и одобряет вызов url
        // Есл url не явно не указан, то будет ошибка
        Map<String, Object> permissionParams = null;
        try {
            permissionParams = getPermissionParams(request);
        } catch (Exception e) {
            SecurityExceptionHandler.handle(response, 500, e);
            return;
        }

        // Если null то проверять нечего
        if (permissionParams == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, Object> permissionRules;
        if (permissionParams.containsKey("objectId")) {
            String objectId = UtCnv.toString(permissionParams.get("objectId"));
            long permissionType = UtCnv.toLong(permissionParams.get("permissionType"));

            if (objectId.isEmpty() || permissionType == 0) {
                filterChain.doFilter(request, response);
                return;
            }

            permissionRules = UtCnv.toMap(objectId, permissionType);
        } else {
            permissionRules = permissionParams;
        }

        try {
            if (permissionManager.check(user.getId(), permissionRules)) {
                filterChain.doFilter(request, response);
            }
        } catch (PermissionException e) {
            SecurityExceptionHandler.handle(response, e.getHttpStatusCode().value(), e);
        } catch (Exception e) {
            SecurityExceptionHandler.handle(response, 500, e);
        }
    }
}