package eu.tmach.tax212.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {
    private int page;
    private int totalPages;
    private int pageSize;
    private int numberOfItems;
    private long totalItems;
    private List<String> sort;
    private List<T> items;

    /**
     * Statická tovární metoda pro snadnou konverzi ze Spring Page.
     */
    public static <T> PagedResponse<T> from(Page<T> page) {
        return PagedResponse.<T>builder()
                .page(page.getNumber())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .numberOfItems(page.getNumberOfElements())
                .totalItems(page.getTotalElements())
                .sort(page.getSort().stream()
                        .map(order -> order.getProperty() + "." + order.getDirection().name().toLowerCase())
                        .toList())
                .items(page.getContent())
                .build();
    }

    public <R> PagedResponse<R> map(Function<? super T, R> converter) { // Změna: z '? extends R' na 'R'
        List<R> mappedItems = this.items.stream()
                .map(converter)
                .toList();

        return PagedResponse.<R>builder()
                .page(this.page)
                .totalPages(this.totalPages)
                .pageSize(this.pageSize)
                .numberOfItems(this.numberOfItems)
                .totalItems(this.totalItems)
                .sort(this.sort)
                .items(mappedItems)
                .build();
    }
}