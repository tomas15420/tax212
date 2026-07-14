package eu.tmach.tax212.config.converters;

import eu.tmach.tax212.dto.filter.SortOrder;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSortOrderConverter implements Converter<String, SortOrder> {

    @Override
    public SortOrder convert(@NonNull String source) {
        if (source.isBlank()) {
            return null;
        }

        if (source.contains(".")) {
            int lastDotIndex = source.lastIndexOf(".");
            String field = source.substring(0, lastDotIndex);
            String direction = source.substring(lastDotIndex + 1);
            return new SortOrder(field, direction.toLowerCase());
        }

        return new SortOrder(source, "asc");
    }
}