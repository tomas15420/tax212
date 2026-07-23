package eu.tmach.tax212.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PagedResponse<T>(
        int page,
        int totalPages,
        int pageSize,
        int numberOfItems,
        long totalItems,
        List<String> sort,
        List<T> items
) {
    public static <T> PagedResponse<T> from(Page<T> page) {
        List<String> sort = page.getSort().stream()
                .map(order -> order.getProperty() + "." + order.getDirection().name().toLowerCase())
                .toList();

        return new PagedResponse<>(
                page.getNumber(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                sort,
                page.getContent()
        );
    }

    public static <T, R> PagedResponse<R> from(Page<T> page, Function<? super T, R> converter) {
        return PagedResponse.from(page.map(converter));
    }
}