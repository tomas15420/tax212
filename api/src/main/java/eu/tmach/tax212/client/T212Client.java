package eu.tmach.tax212.client;

import eu.tmach.tax212.dto.trading212.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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
                    .onStatus(HttpStatusCode::isError, this::handleApiError)
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

        log.info("Synchronizace úspěšně dokončena. Celkem staženo {} záznamů.", allOrders.size());
        return allOrders;
    }

    public T212AccountSummary getAccountSummary() {
        log.info("Získávám stav účtu z T212 API");

        return t212RestClient.get()
                .uri("/equity/account/summary")
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .body(T212AccountSummary.class);
    }

    public List<T212DividendItem> fetchAllDividends(String stopReference) {
        List<T212DividendItem> allDividends = new ArrayList<>();
        String currentPath = "/equity/history/dividends?limit=50";
        boolean stopReached = false;

        while (currentPath != null) {
            log.info("Stahuji dividendy z T212: {}", currentPath);

            String finalPath = currentPath;
            T212DividendPage page = t212RestClient.get()
                    .uri(finalPath)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, this::handleApiError)
                    .body(T212DividendPage.class);

            if (page != null && page.items() != null) {
                int initialSize = allDividends.size();
                for (var item : page.items()) {
                    if (stopReference != null && stopReference.equals(item.reference())) {
                        log.info("Dosáhl jsem stopReference: {}. Končím stahování.", stopReference);
                        stopReached = true;
                        break;
                    }
                    allDividends.add(item);
                }

                log.info("Přidáno {} dividend (Celkem: {})", allDividends.size() - initialSize, allDividends.size());

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

        log.info("Synchronizace dividend dokončena. Celkem {} záznamů.", allDividends.size());
        return allDividends;
    }

    private void handleApiError(HttpRequest request, ClientHttpResponse response) throws IOException {
        HttpStatusCode status = response.getStatusCode();

        throw switch (status.value()) {
            case 401 -> new ResponseStatusException(status, "401: Neplatný API klíč.");
            case 403 -> new ResponseStatusException(status, "403: Chybějící scope pro API klíč.");
            case 408 -> new ResponseStatusException(status, "408: Timeout při volání T212.");
            case 429 -> new ResponseStatusException(status, "429: Rate limit překročen.");
            case 400 -> new ResponseStatusException(status, "400: Špatný požadavek.");
            default -> new ResponseStatusException(status, "Neočekávaná chyba API: " + status);
        };
    }
}
