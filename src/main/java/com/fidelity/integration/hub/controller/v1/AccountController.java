package com.fidelity.integration.hub.controller.v1;

import com.fidelity.integration.hub.model.dto.AccountDto;
import com.fidelity.integration.hub.model.enums.AccountStatus;
import com.fidelity.integration.hub.model.enums.AccountType;
import com.fidelity.integration.hub.service.AccountService;
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

import java.util.List;

/**
 * REST controller for account-related operations.
 * Provides endpoints for retrieving account information for clients.
 */
@RestController
@RequestMapping("/api/v1/clients")
@Validated
@Tag(name = "Accounts", description = "Account management and retrieval APIs")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
        summary = "Get accounts for a client",
        description = "Retrieves all accounts associated with a client, with optional filtering by status and type"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved accounts",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AccountDto.class)
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
            description = "Client not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping(value = "/{clientId}/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountDto>> getAccounts(
        @Parameter(description = "Client identifier", required = true, example = "CLIENT-12345")
        @PathVariable
        @Pattern(regexp = "^CLIENT-[A-Z0-9]+$", message = "Client ID must match pattern CLIENT-{ID}")
        String clientId,
        
        @Parameter(description = "Filter by account status", example = "ACTIVE")
        @RequestParam(required = false)
        AccountStatus accountStatus,
        
        @Parameter(description = "Filter by account type", example = "BROKERAGE")
        @RequestParam(required = false)
        AccountType accountType
    ) {
        List<AccountDto> accounts = accountService.getAccountsByClient(clientId, accountStatus, accountType);
        return ResponseEntity.ok(accounts);
    }
}
