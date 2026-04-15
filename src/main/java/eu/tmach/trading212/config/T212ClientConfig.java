package eu.tmach.trading212.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Slf4j
@Configuration
public class T212ClientConfig {
    @Value("${trading212.api.url}")
    private String baseUrl;
    @Value("${trading212.api.username}")
    private String username;
    @Value("${trading212.api.password}")
    private String password;

    @Bean
    public RestClient t212RestClient() {
        String authString = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Basic " + encodedAuth)
                .requestInterceptor(((request, body, execution) -> {
                    ClientHttpResponse response = execution.execute(request, body);

                    String remaining = response.getHeaders().getFirst("x-ratelimit-remaining");
                    String reset = response.getHeaders().getFirst("x-ratelimit-reset");
                    String used = response.getHeaders().getFirst("x-ratelimit-used");

                    log.info("Rate Limit: Použito {}, Zbývá {}, Reset v {}", used, remaining, reset);

                    if (remaining != null && Integer.parseInt(remaining) <= 0 && reset != null) {
                        long resetTime = Long.parseLong(reset);
                        long currentTime = System.currentTimeMillis() / 1000;

                        long waitSeconds = resetTime - currentTime + 1;

                        if (waitSeconds > 0) {
                            try {
                                Thread.sleep(waitSeconds * 1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    return response;

                }))
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
