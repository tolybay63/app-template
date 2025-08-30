package kz.kis.kisauth.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@SpringBootTest
public class UserSynchronizerTest {

    @Autowired
    private UserSynchronizer userSynchronizer;

    @Test
    public void synchronizeTest() {
        userSynchronizer.synchronize();
    }
}
