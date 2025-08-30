package kz.kis.kisauth.configuration;

import kz.kis.kisauth.configuration.webClient.*;
import kz.kis.kisauth.manager.*;
import kz.kis.kisauth.manager.impl.*;
import kz.kis.kisauth.persistance.entity.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.servlet.config.annotation.*;

import java.util.*;
import java.util.concurrent.*;

@Configuration
@EnableWebSecurity
public class WebConfiguration implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger("config");

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${application.cors.origins}")
    private String corsOrigins;

    @Value("${keycloak.url}")
    private String url;

    @Value("${keycloak.client.id}")
    private String username;

    @Value("${keycloak.client.secret}")
    private String password;

    @Value("${keycloak.client.username}")
    private String keycloakUsername;

    @Value("${keycloak.client.password}")
    private String keycloakPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsOrigins.split(","))  // Указываем допустимые источники (например, IP-адрес или localhost)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешаем нужные методы
                .allowedHeaders("*") // Разрешаем все заголовки (если нужно)
                .allowCredentials(true) // Разрешаем отправку кук в кросс-доменных запросах
                .maxAge(3600); // Время кеширования CORS (например, 1 час)
    }

    @Bean
    public KeycloakWebClientManagerExt keycloakAuthenticationManager() {
        log.info("=========================");
        log.info("WebConfiguration.keycloakAuthenticationManager");
        log.info("keycloak.url: " + url);
        log.info("");

        return new KeycloakWebClientManagerExt(WebClientFactory.createWebClient(url, username, password), username, password, keycloakUsername, keycloakPassword);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        log.info("=========================");
        log.info("WebConfiguration.jwtDecoder");
        log.info("jwkSetUri: " + jwkSetUri);
        log.info("");

        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }


    @Bean
    public Map<String, UserEntity> usersCache() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<Long, Object> userPermissionsCache() {
        return new ConcurrentHashMap<>();
    }
}