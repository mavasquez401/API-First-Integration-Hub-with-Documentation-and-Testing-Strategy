package com.fidelity.integration.hub.controller.v1;

import com.fidelity.integration.hub.model.dto.InstrumentDto;
import com.fidelity.integration.hub.service.ReferenceDataService;
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
 * REST controller for reference data operations.
 * Provides endpoints for retrieving instrument metadata and reference information.
 */
@RestController
@RequestMapping("/api/v1/reference")
@Validated
@Tag(name = "Reference Data", description = "Instrument and reference data APIs")
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    public ReferenceDataController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @Operation(
        summary = "Get instrument reference data",
        description = "Retrieves metadata and reference information for a financial instrument by symbol"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved instrument data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InstrumentDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - invalid symbol format",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - missing or invalid authentication token",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Instrument not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping(value = "/instruments/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InstrumentDto> getInstrument(
        @Parameter(description = "Instrument symbol/ticker", required = true, example = "AAPL")
        @PathVariable
        @Pattern(regexp = "^[A-Z0-9.-]+$", message = "Symbol must contain only uppercase letters, numbers, dots, or hyphens")
        String symbol
    ) {
        InstrumentDto instrument = referenceDataService.getInstrumentBySymbol(symbol);
        return ResponseEntity.ok(instrument);
    }
}
