package eu.tmach.trading212.controller;

import eu.tmach.trading212.dto.PagedResponse;
import eu.tmach.trading212.dto.TransactionDto;
import eu.tmach.trading212.dto.filter.TransactionFilter;
import eu.tmach.trading212.exception.ValidationErrorResponse;
import eu.tmach.trading212.service.TransactionService;
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
@RequestMapping("/api/transactions")
@Tag(name = "Transakce", description = "Správa a vyhledávání v historii obchodních transakcí")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(summary = "Získá seznam transakcí s filtrací")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Úspěšné načtení"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Neplatné parametry filtru",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PagedResponse<TransactionDto>> getTransactions(
            @ParameterObject @ModelAttribute @Valid TransactionFilter filter) {
        return ResponseEntity.ok(transactionService.getTransactions(filter));
    }
}
