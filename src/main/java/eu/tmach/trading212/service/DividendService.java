package eu.tmach.trading212.service;

import eu.tmach.trading212.client.T212Client;
import eu.tmach.trading212.dto.trading212.T212DividendItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DividendService {
    private final T212Client client;

    public List<T212DividendItem> showDividends() {
        return client.fetchAllDividends(null);
    }
}
