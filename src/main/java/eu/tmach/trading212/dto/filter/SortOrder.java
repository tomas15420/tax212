package eu.tmach.trading212.dto.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SortOrder {

    @NotBlank(message = "Pole pro řazení nesmí být prázdné")
    @Schema(description = "Název pole", example = "ticker")
    private String field;

    @Pattern(regexp = "^(?i)(asc|desc)$", message = "Směr musí být 'asc' nebo 'desc'")
    @Schema(description = "Směr řazení", example = "asc", allowableValues = {"asc", "desc"})
    @Builder.Default
    private String dir = "asc";
}