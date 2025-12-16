package com.fidelity.integration.hub.controller.v1;

import com.fidelity.integration.hub.model.dto.PortfolioDto;
import com.fidelity.integration.hub.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for portfolio-related operations.
 * Provides endpoints for retrieving portfolio information including positions and valuations.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@Validated
@Tag(name = "Portfolios", description = "Portfolio and position aggregation APIs")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Operation(
        summary = "Get portfolio for an account",
        description = "Retrieves portfolio information including positions, valuations, and market data enrichment for a specific account"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved portfolio",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PortfolioDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - invalid parameters",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - missing or invalid authentication token",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping(value = "/{accountId}/portfolio", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PortfolioDto> getPortfolio(
        @Parameter(description = "Account identifier", required = true, example = "ACC-12345")
        @PathVariable
        @Pattern(regexp = "^ACC-[A-Z0-9]+$", message = "Account ID must match pattern ACC-{ID}")
        String accountId
    ) {
        PortfolioDto portfolio = portfolioService.getPortfolioByAccount(accountId);
        return ResponseEntity.ok(portfolio);
    }
}
