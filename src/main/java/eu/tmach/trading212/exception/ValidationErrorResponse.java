package eu.tmach.trading212.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Schema(description = "Detailní informace o chybách ve validaci polí")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ValidationErrorResponse extends ApiErrorResponse {

    @Schema(description = "Seznam konkrétních chyb na úrovni polí")
    private List<FieldErrorDetail> errors;
}