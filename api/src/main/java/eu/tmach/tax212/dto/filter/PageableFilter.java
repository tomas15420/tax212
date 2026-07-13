package eu.tmach.tax212.dto.filter;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class PageableFilter {
    @Min(value = 0, message = "Číslo stránky nesmí být záporné")
    @Schema(description = "Číslo stránky (od 0)", defaultValue = "0", example = "0")
    @Builder.Default
    private Integer page = 0;

    @Min(value = 1, message = "Velikost stránky musí být alespoň 1")
    @Max(value = 100, message = "Na jednu stránku lze načíst maximálně 100 záznamů")
    @Schema(description = "Počet prvků na stránku (1-100)", defaultValue = "20", example = "50")
    @Builder.Default
    protected Integer size = 20;

    @ArraySchema(
            schema = @Schema(
                    type = "string",
                    example = "id.asc",
                    description = "Formát: pole.směr"
            )
    )
    protected List<@Valid SortOrder> sort;

    protected abstract String getDefaultSortField();

    public Pageable toPageable() {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort != null && !sort.isEmpty()) {
            for (SortOrder s : sort) {
                if (s.getField() != null) {
                    Sort.Direction direction = "desc".equalsIgnoreCase(s.getDir())
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;
                    orders.add(new Sort.Order(direction, s.getField()));
                }
            }
        }

        if (orders.isEmpty()) {
            orders.add(Sort.Order.desc(getDefaultSortField()));
        }

        return PageRequest.of(this.page, this.size, Sort.by(orders));
    }
}