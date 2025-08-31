package kz.app.appauth.keycloak;

import kz.app.appauth.service.auth.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@SpringBootTest
public class KeycloakAuthenticationManagerTest {

    @Autowired
    private IAuthService authService;

    private String token;

    @BeforeEach
    public void setUp() {
        authService.login("smart_catalog", "Grpn404tfgNbp09we21");
    }

    @Test
    public void authenticate() {
        System.out.println(token);
    }

    @Test
    public void createUser() throws Exception {
        authService.signUp("user_7", "user", "user_7@nail.com", "user_7", "user_7");
    }

}