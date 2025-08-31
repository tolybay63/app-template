package kz.app.appauth.configuration.webClient;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientFactory {
    public static WebClient createWebClient(String url, String username, String password) {
        return WebClient.builder().baseUrl(url)
                .defaultHeaders(
                it -> {
                    it.setBasicAuth(username, password);
                }
        ).build();
    }
}