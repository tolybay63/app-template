package kz.kis.kisauth.keycloak;

import kz.kis.kisauth.service.auth.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KeycloakAuthenticationManagerTest {
    @Autowired
    private IAuthService IAuthService;

    private String token;

    @BeforeEach
    public void setUp() {
        IAuthService.login("smart_catalog", "Grpn404tfgNbp09we21");
    }

    @Test
    public void authenticate() {
        System.out.println(token);
    }

    @Test
    public void createUser() throws Exception {
        IAuthService.signUp("user_7", "user", "user_7@nail.com", "user_7", "user_7");
    }
}