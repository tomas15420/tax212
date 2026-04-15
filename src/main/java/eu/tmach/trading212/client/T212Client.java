package eu.tmach.trading212.client;

import eu.tmach.trading212.dto.T212OrderPage;
import eu.tmach.trading212.dto.T212OrderWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class T212Client {

    private final RestClient t212RestClient;

    public List<T212OrderWrapper> fetchAllOrders(String stopT212Id) {
        List<T212OrderWrapper> allOrders = new ArrayList<>();

        String currentPath = "/equity/history/orders?limit=50";
        boolean stopReached = false;

        while (currentPath != null) {
            log.info("Stahuji data z T212: {}", currentPath);

            String finalPath = currentPath;
            T212OrderPage page = t212RestClient.get()
                    .uri(finalPath)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        int code = response.getStatusCode().value();
                        switch (code) {
                            case 400 -> throw new RuntimeException("400: Špatné parametry filtrování.");
                            case 401 -> throw new RuntimeException("401: Neplatný API klíč.");
                            case 403 -> throw new RuntimeException("403: Chybějící scope (history:orders).");
                            case 408 -> throw new RuntimeException("408: Timeout při volání T212.");
                            case 429 -> log.error("429: Rate limit překročen (i přes ochranu v interceptoru)!");
                            default -> throw new RuntimeException("Chyba API: " + code);
                        }
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (page != null && page.items() != null) {
                int initialSize = allOrders.size();
                for (var item : page.items()) {
                    if (item.fill() == null) {
                        log.warn("Objednávka {} nemá vyplněný fill (status) {}, přeskočena", item.order().id(), item.order().status());
                        continue;
                    }
                    if (stopT212Id != null && stopT212Id.equals(item.fill().id())) {
                        log.info("Dosáhl jsem stopT212Id: {}. Končím stahování", stopT212Id);
                        stopReached = true;
                        break;
                    }
                    allOrders.add(item);
                }
                log.info("Staženo {} záznamů (Celkem nasbíráno: {})", allOrders.size() - initialSize, allOrders.size());

                // Přechod na další stránku
                String next = page.nextPagePath();
                if (next != null && !stopReached) {
                    currentPath = next.replace("/api/v0", "");
                } else {
                    currentPath = null;
                }
            } else {
                currentPath = null;
            }
        }

        log.info("Synchronizace úspěšně dokončena. Celkem staženo {} objednávek.", allOrders.size());
        return allOrders;
    }
}
