package eu.tmach.tax212.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Detail konkrétního nevalidního pole")
@Builder
public record FieldErrorDetail(
        String field,
        String message
) {
}