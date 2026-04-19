package eu.tmach.trading212.dto.trading212;

import java.util.List;

public record T212DividendPage(
        List<T212DividendItem> items,
        String nextPagePath
) {
}