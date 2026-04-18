package eu.tmach.trading212.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.security.DenyAll;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(description = "Unifikovaný formát chybové odpovědi")
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    @Schema(description = "HTTP stavový kód")
    private int status;

    @Schema(description = "Detailní popis chyby")
    private String message;

    @Schema(description = "Relativní cesta k endpointu, který chybu vyvolal")
    private String path;

    @Schema(description = "Časový razítko vzniku chyby")
    private LocalDateTime timestamp;
}