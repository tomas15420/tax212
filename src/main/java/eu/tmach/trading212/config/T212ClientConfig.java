package eu.tmach.trading212.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
public class T212ClientConfig {
    @Value("${trading212.api.url}")
    private String baseUrl;
    @Value("${trading212.api.username}")
    private String username;
    @Value("${trading212.api.password}")
    private String password;

    private final Map<String, Long> endpointNextAllowedTime = new ConcurrentHashMap<>();

    @Bean
    public RestClient t212RestClient() {
        String authString = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Basic " + encodedAuth)
                .requestInterceptor(((request, body, execution) -> {
                    String path = request.getURI().getPath();

                    long now = System.currentTimeMillis();
                    long waitMillis = endpointNextAllowedTime.getOrDefault(path, 0L) - now;
                    if (waitMillis > 0) {
                        log.info("Rate limit ochrana: čekám {} ms před dalším požadavkem", waitMillis);
                        try {
                            Thread.sleep(waitMillis);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    ClientHttpResponse response = execution.execute(request, body);

                    String remaining = response.getHeaders().getFirst("x-ratelimit-remaining");
                    String reset = response.getHeaders().getFirst("x-ratelimit-reset");
                    String used = response.getHeaders().getFirst("x-ratelimit-used");

                    log.info("Rate Limit: Použito {}, Zbývá {}, Reset v {}", used, remaining, reset);

                    if (remaining != null && Integer.parseInt(remaining) <= 0 && reset != null) {
                        long resetTimestampSeconds = Long.parseLong(reset) + 1;
                        endpointNextAllowedTime.put(path, resetTimestampSeconds * 1000);
                    } else {
                        endpointNextAllowedTime.remove(path);
                    }
                    return response;

                }))
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
