package eu.tmach.trading212.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Detail konkrétního nevalidního pole")
@Builder
public record FieldErrorDetail(
        String field,
        String message
) {}