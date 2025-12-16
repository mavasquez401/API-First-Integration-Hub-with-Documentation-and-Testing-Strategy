# API Documentation

This document provides comprehensive API documentation for the API-First Integration Hub.

## Base URL

- **Local**: `http://localhost:8080`
- **Production**: `https://api.fidelity.com` (example)

## API Versioning

All endpoints are versioned using URI versioning:

- Base path: `/api/v1`

See [VERSIONING.md](VERSIONING.md) for versioning policy.

## Authentication

All endpoints (except `/api/v1/health`) require OAuth2 JWT authentication.

**Authorization Header**:
```
Authorization: Bearer <jwt-token>
```

See [SECURITY.md](SECURITY.md) for security details.

## Content Types

- **Request**: `application/json`
- **Response**: `application/json`
- **Error Response**: `application/problem+json` (RFC7807)

## Correlation IDs

All requests should include a correlation ID header for tracing:

```
X-Correlation-ID: <uuid>
```

The service will generate one if not provided. The correlation ID is included in all responses and error messages.

## Endpoints

### Health Check

#### GET /api/v1/health

Check service health and dependencies.

**Authentication**: Not required

**Response**:
```json
{
  "status": "UP",
  "service": "api-first-integration-hub",
  "version": "1.0.0",
  "checks": {
    "oms": "UP",
    "marketData": "UP"
  }
}
```

---

### Accounts

#### GET /api/v1/clients/{clientId}/accounts

Get all accounts for a client.

**Path Parameters**:
- `clientId` (string, required): Client identifier (format: `CLIENT-{ID}`)

**Query Parameters**:
- `accountStatus` (enum, optional): Filter by status (`ACTIVE`, `CLOSED`, `PENDING`, `SUSPENDED`, `DORMANT`)
- `accountType` (enum, optional): Filter by type (`BROKERAGE`, `IRA`, `RETIREMENT_401K`, `TRUST`, `JOINT`, `CORPORATE`, `CUSTODIAL`)

**Response**: `200 OK`
```json
[
  {
    "accountId": "ACC-12345",
    "clientId": "CLIENT-98765",
    "accountType": "BROKERAGE",
    "status": "ACTIVE",
    "displayName": "My Investment Account",
    "accountNumber": "****1234",
    "currentValue": 125000.50,
    "currency": "USD",
    "openedDate": "2020-01-15T00:00:00Z",
    "lastUpdated": "2024-01-01T12:00:00Z"
  }
]
```

**Error Responses**:
- `400 Bad Request` - Invalid client ID format
- `401 Unauthorized` - Missing or invalid authentication
- `404 Not Found` - Client not found

**Example**:
```bash
curl -X GET \
  'http://localhost:8080/api/v1/clients/CLIENT-98765/accounts?accountStatus=ACTIVE' \
  -H 'Authorization: Bearer <token>' \
  -H 'X-Correlation-ID: abc-123-def-456'
```

---

### Portfolios

#### GET /api/v1/accounts/{accountId}/portfolio

Get portfolio information including positions and valuations.

**Path Parameters**:
- `accountId` (string, required): Account identifier (format: `ACC-{ID}`)

**Response**: `200 OK`
```json
{
  "accountId": "ACC-12345",
  "totalValue": 125000.50,
  "totalCostBasis": 100000.00,
  "totalUnrealizedGainLoss": 25000.50,
  "totalUnrealizedGainLossPercent": 25.00,
  "currency": "USD",
  "positions": [
    {
      "symbol": "AAPL",
      "instrumentName": "Apple Inc.",
      "assetClass": "EQUITY",
      "quantity": 100.0,
      "currentPrice": 175.25,
      "positionValue": 17525.00,
      "costBasis": 150.00,
      "totalCostBasis": 15000.00,
      "unrealizedGainLoss": 2525.00,
      "unrealizedGainLossPercent": 16.83,
      "currency": "USD"
    }
  ],
  "asOfDate": "2024-01-01T12:00:00Z"
}
```

**Error Responses**:
- `400 Bad Request` - Invalid account ID format
- `401 Unauthorized` - Missing or invalid authentication
- `404 Not Found` - Account not found

**Example**:
```bash
curl -X GET \
  'http://localhost:8080/api/v1/accounts/ACC-12345/portfolio' \
  -H 'Authorization: Bearer <token>' \
  -H 'X-Correlation-ID: abc-123-def-456'
```

---

### Reference Data

#### GET /api/v1/reference/instruments/{symbol}

Get instrument reference data by symbol.

**Path Parameters**:
- `symbol` (string, required): Instrument symbol/ticker (uppercase alphanumeric with dots/hyphens)

**Response**: `200 OK`
```json
{
  "symbol": "AAPL",
  "name": "Apple Inc.",
  "assetClass": "EQUITY",
  "exchange": "NASDAQ",
  "currentPrice": 175.25,
  "currency": "USD",
  "securityId": "037833100",
  "sector": "Technology",
  "industry": "Consumer Electronics",
  "lastUpdated": "2024-01-01"
}
```

**Error Responses**:
- `400 Bad Request` - Invalid symbol format
- `401 Unauthorized` - Missing or invalid authentication
- `404 Not Found` - Instrument not found

**Example**:
```bash
curl -X GET \
  'http://localhost:8080/api/v1/reference/instruments/AAPL' \
  -H 'Authorization: Bearer <token>' \
  -H 'X-Correlation-ID: abc-123-def-456'
```

---

## Error Responses

All errors follow RFC7807 Problem Details format:

```json
{
  "type": "https://api.fidelity.com/problems/validation-error",
  "title": "Bad Request",
  "status": 400,
  "detail": "Account status filter is invalid",
  "instance": "/api/v1/clients/CLIENT-123/accounts",
  "correlationId": "abc-123-def-456",
  "errorCode": "VALIDATION_ERROR",
  "timestamp": "2024-01-01T12:00:00Z",
  "violations": [
    {
      "field": "accountStatus",
      "message": "must be one of [ACTIVE, CLOSED, PENDING, SUSPENDED, DORMANT]",
      "rejectedValue": "INVALID_STATUS"
    }
  ]
}
```

See [ERROR_HANDLING.md](ERROR_HANDLING.md) for detailed error information.

## OpenAPI Documentation

Interactive API documentation is available via Swagger UI:

- **Local**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api-docs

## Rate Limiting

Rate limiting may be applied in production. Rate limit headers are included in responses:

```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1609459200
```

## Pagination

Currently, endpoints return all results. Pagination will be added in future versions.

## Data Formats

### Dates and Times

- Dates: ISO 8601 format (`YYYY-MM-DD`)
- Timestamps: ISO 8601 format with timezone (`YYYY-MM-DDTHH:mm:ssZ`)

### Numbers

- Decimals: Standard decimal format (e.g., `125000.50`)
- Currency: 2 decimal places for currency values

### Enumerations

See [DATA_CONTRACTS.md](DATA_CONTRACTS.md) for complete enum definitions.

## Example Requests

See the Postman collection (`postman/IntegrationHub.postman_collection.json`) for complete examples.

## Support

For API support, see [CONSUMER_PROVIDER_RESPONSIBILITIES.md](CONSUMER_PROVIDER_RESPONSIBILITIES.md).
