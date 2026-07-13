package eu.tmach.tax212.controller;

import eu.tmach.tax212.dto.DividendDto;
import eu.tmach.tax212.dto.PagedResponse;
import eu.tmach.tax212.dto.filter.DividendFilter;
import eu.tmach.tax212.exception.ValidationErrorResponse;
import eu.tmach.tax212.service.DividendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dividends")
@Tag(name = "Dividendy", description = "Vyhledávání ve výplatách dividend a jejich historie")
public class DividendController {
    private final DividendService dividendService;

    @Operation(summary = "Získá seznam dividend s filtrací")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Úspěšné načtení"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Neplatné parametry filtru",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PagedResponse<DividendDto>> getDividends(
            @ParameterObject @ModelAttribute @Valid DividendFilter filter) {
        return ResponseEntity.ok(dividendService.getDividends(filter));
    }
}
