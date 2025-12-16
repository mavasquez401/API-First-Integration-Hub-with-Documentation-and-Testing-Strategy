package com.fidelity.integration.hub.controller.v1;

import com.fidelity.integration.hub.model.dto.AccountDto;
import com.fidelity.integration.hub.model.enums.AccountStatus;
import com.fidelity.integration.hub.model.enums.AccountType;
import com.fidelity.integration.hub.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller slice test for AccountController.
 * Tests the web layer in isolation, focusing on:
 * - Request validation
 * - HTTP status codes
 * - Response format
 * - Error handling (Problem Details format)
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    @WithMockUser
    void getAccounts_Success_Returns200() throws Exception {
        // Given
        String clientId = "CLIENT-98765";
        AccountDto accountDto = new AccountDto(
            "ACC-12345",
            clientId,
            AccountType.BROKERAGE,
            AccountStatus.ACTIVE,
            "My Investment Account",
            "****1234",
            new BigDecimal("125000.50"),
            "USD",
            Instant.now(),
            Instant.now()
        );
        when(accountService.getAccountsByClient(eq(clientId), any(), any()))
            .thenReturn(List.of(accountDto));

        // When/Then
        mockMvc.perform(get("/api/v1/clients/{clientId}/accounts", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].accountId").value("ACC-12345"))
            .andExpect(jsonPath("$[0].clientId").value(clientId))
            .andExpect(header().exists("X-Correlation-ID"));
    }

    @Test
    @WithMockUser
    void getAccounts_InvalidClientIdFormat_Returns400() throws Exception {
        // Given
        String invalidClientId = "invalid-format";

        // When/Then
        mockMvc.perform(get("/api/v1/clients/{clientId}/accounts", invalidClientId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void getAccounts_WithStatusFilter_Success() throws Exception {
        // Given
        String clientId = "CLIENT-98765";
        when(accountService.getAccountsByClient(eq(clientId), eq(AccountStatus.ACTIVE), any()))
            .thenReturn(List.of());

        // When/Then
        mockMvc.perform(get("/api/v1/clients/{clientId}/accounts", clientId)
                .param("accountStatus", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isOk());
    }
}
