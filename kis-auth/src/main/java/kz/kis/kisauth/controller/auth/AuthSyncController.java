package kz.kis.kisauth.controller.auth;

import jakarta.servlet.http.*;
import kz.kis.kisauth.manager.*;
import kz.kis.kisauth.persistance.constant.*;
import kz.kis.kisauth.service.auth.impl.*;
import kz.kis.kiscore.model.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync/auth")
@AllArgsConstructor
public class AuthSyncController {

    private final AuthSynchronizerService authSynchronizerService;

    @PostMapping("/login")
    public ResponseEntity<DbRec> login(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        String accessToken = null;

        try {
            if (accessToken == null) {
                throw new Exception("accessToken is null");
            }
            // authSynchronizerService.createContext(jwtDecoder.decode(accessToken));
        } catch (Exception e) {
            authSynchronizerService.login(request, response);
        }

        return ResponseEntity.ok(authSynchronizerService.recCredentialsSession());
    }
}