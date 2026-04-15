package eu.tmach.trading212.dto.trading212;

import java.util.List;

public record T212OrderPage(
        List<T212OrderWrapper> items,
        String nextPagePath
) {
}