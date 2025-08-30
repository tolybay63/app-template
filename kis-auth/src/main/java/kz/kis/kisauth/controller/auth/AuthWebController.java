package kz.kis.kisauth.controller.auth;

import jakarta.servlet.http.*;
import kz.kis.kisauth.manager.*;
import kz.kis.kisauth.manager.impl.*;
import kz.kis.kisauth.persistance.entity.*;
import kz.kis.kisauth.service.*;
import kz.kis.kisauth.service.auth.impl.*;
import kz.kis.kiscore.model.*;
import kz.kis.kiscore.utils.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.logout.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthWebController {
    private final UserService userService;
    private final AuthWebService authWebService;
    private final ContextManager contextManager;
    private final SecurityContextLogoutHandler securityContextLogoutHandler;
    private final AuthAgentService authAgentService;

    @PostMapping("/login")
    public ResponseEntity<DbRec> login(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        // Запоминаем web сессию
        authWebService.login(request, response);

        // Возвращаем на фронт
        return ResponseEntity.ok(authWebService.buildUserResponse(request));
    }

    @PostMapping("/agent/login")
    public ResponseEntity<Void> agentTokens(
            @RequestBody DbRec creds
    ) throws Exception {

        contextManager.createContext(creds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/agent/creds")
    public ResponseEntity<DbRec> agentTokens(
            HttpServletRequest request
    ) throws Exception {

        return ResponseEntity.ok(authAgentService.authAgent(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        authWebService.dropUserFromCache();
        contextManager.deleteContext(request, response);
        securityContextLogoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName
    ) throws Exception {

        return ResponseEntity.ok(authWebService.signUp(username, password, email, firstName, lastName));
    }

    @GetMapping("/getCurrentUser")
    public ResponseEntity<DbRec> getCurrentUser() throws Exception {
        UserEntity usr = userService.getCurrentUser();

        //
        if (usr == null) {
            return ResponseEntity.ok(null);

        } else {
            DbRec usrMap = new DbRec(UtCnv.toMap(
                    "id", usr.getId(),
                    "name", usr.getUsername(),
                    "login", usr.getEmail()
            ));

            //
            return ResponseEntity.ok(usrMap);
        }
    }

}