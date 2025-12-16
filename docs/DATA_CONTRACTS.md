# Data Contracts

This document defines the data contracts, schemas, and data types used by the API-First Integration Hub.

## Data Types

### Primitives

| Type | Description | Example |
|------|-------------|---------|
| `string` | UTF-8 encoded string | `"ACC-12345"` |
| `integer` | 32-bit signed integer | `400` |
| `number` | Decimal number (BigDecimal) | `125000.50` |
| `boolean` | Boolean value | `true` |
| `date` | ISO 8601 date (YYYY-MM-DD) | `"2024-01-01"` |
| `datetime` | ISO 8601 datetime with timezone | `"2024-01-01T12:00:00Z"` |

### Complex Types

- **Objects**: JSON objects with named properties
- **Arrays**: Ordered lists of values
- **Enums**: Fixed set of string values

## Enumerations

### AccountStatus

Represents the operational state of an account.

| Value | Description |
|-------|-------------|
| `ACTIVE` | Account is active and available for transactions |
| `CLOSED` | Account is closed and no longer active |
| `PENDING` | Account is pending activation or approval |
| `SUSPENDED` | Account is suspended or restricted |
| `DORMANT` | Account is in a dormant state |

**Backward Compatibility**: New status values may be added in the future. Consumers should handle unknown values gracefully.

### AccountType

Represents the type of account.

| Value | Description |
|-------|-------------|
| `BROKERAGE` | Individual brokerage account |
| `IRA` | Individual Retirement Account |
| `RETIREMENT_401K` | 401(k) retirement account |
| `TRUST` | Trust account |
| `JOINT` | Joint account |
| `CORPORATE` | Corporate account |
| `CUSTODIAL` | Custodial account |

**Backward Compatibility**: New account types may be added in the future.

### AssetClass

Represents the classification of financial instruments.

| Value | Description |
|-------|-------------|
| `EQUITY` | Equities (stocks) |
| `FIXED_INCOME` | Fixed income (bonds) |
| `CASH` | Cash and cash equivalents |
| `COMMODITY` | Commodities |
| `REIT` | Real estate investment trusts |
| `ETF` | Exchange-traded funds |
| `MUTUAL_FUND` | Mutual funds |
| `OPTION` | Options |
| `FUTURE` | Futures |
| `CRYPTO` | Cryptocurrency |

**Backward Compatibility**: New asset classes may be added in the future.

### ErrorCode

Application-specific error codes.

| Value | HTTP Status | Description |
|-------|-------------|-------------|
| `BAD_REQUEST` | 400 | Invalid request format or parameters |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Authentication required |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `RATE_LIMIT_EXCEEDED` | 429 | Rate limit exceeded |
| `PROVIDER_ERROR` | 503 | External provider error |
| `SERVICE_UNAVAILABLE` | 503 | Service temporarily unavailable |
| `INTERNAL_ERROR` | 500 | Internal server error |

## Data Models

### Account

Account information.

```json
{
  "accountId": "string (required)",
  "clientId": "string (required)",
  "accountType": "AccountType (required)",
  "status": "AccountStatus (required)",
  "displayName": "string (required)",
  "accountNumber": "string (required, masked)",
  "currentValue": "number (required)",
  "currency": "string (required, ISO 4217)",
  "openedDate": "datetime (required)",
  "lastUpdated": "datetime (required)"
}
```

**Field Notes**:
- `accountNumber`: Masked for security (e.g., `****1234`)
- `currentValue`: Decimal with 2 decimal places for currency
- `currency`: ISO 4217 currency code (e.g., `USD`, `EUR`)

### Position

Portfolio position information.

```json
{
  "symbol": "string (required)",
  "instrumentName": "string (required)",
  "assetClass": "AssetClass (required)",
  "quantity": "number (required)",
  "currentPrice": "number (required)",
  "positionValue": "number (required)",
  "costBasis": "number (required)",
  "totalCostBasis": "number (required)",
  "unrealizedGainLoss": "number (required)",
  "unrealizedGainLossPercent": "number (required)",
  "currency": "string (required)"
}
```

**Field Notes**:
- `quantity`: Decimal number (can have fractional shares)
- `currentPrice`: Market price at time of calculation
- `positionValue`: Calculated as `quantity * currentPrice`
- `unrealizedGainLossPercent`: Percentage (e.g., `16.83` for 16.83%)

### Portfolio

Portfolio information with positions.

```json
{
  "accountId": "string (required)",
  "totalValue": "number (required)",
  "totalCostBasis": "number (required)",
  "totalUnrealizedGainLoss": "number (required)",
  "totalUnrealizedGainLossPercent": "number (required)",
  "currency": "string (required)",
  "positions": "array<Position> (required)",
  "asOfDate": "datetime (required)"
}
```

**Field Notes**:
- `positions`: Array of position objects
- `asOfDate`: Timestamp when portfolio snapshot was taken

### Instrument

Instrument reference data.

```json
{
  "symbol": "string (required)",
  "name": "string (required)",
  "assetClass": "AssetClass (required)",
  "exchange": "string (required)",
  "currentPrice": "number (required)",
  "currency": "string (required)",
  "securityId": "string (optional, CUSIP/ISIN)",
  "sector": "string (optional)",
  "industry": "string (optional)",
  "lastUpdated": "date (required)"
}
```

**Field Notes**:
- `symbol`: Ticker symbol (uppercase)
- `securityId`: CUSIP for US securities, ISIN for international
- `sector` and `industry`: Classification information

## Identifier Formats

### Account ID

**Format**: `ACC-{ALPHANUMERIC}`

**Examples**:
- `ACC-12345`
- `ACC-ABC123`

**Validation**: Must match regex `^ACC-[A-Z0-9]+$`

### Client ID

**Format**: `CLIENT-{ALPHANUMERIC}`

**Examples**:
- `CLIENT-98765`
- `CLIENT-XYZ789`

**Validation**: Must match regex `^CLIENT-[A-Z0-9]+$`

### Instrument Symbol

**Format**: Uppercase alphanumeric with dots/hyphens

**Examples**:
- `AAPL`
- `BRK.B`
- `BRK-A`

**Validation**: Must match regex `^[A-Z0-9.-]+$`

## Date and Time Formats

### Date Format

**ISO 8601 Date**: `YYYY-MM-DD`

**Examples**:
- `2024-01-01`
- `2024-12-31`

### DateTime Format

**ISO 8601 DateTime with Timezone**: `YYYY-MM-DDTHH:mm:ssZ`

**Examples**:
- `2024-01-01T12:00:00Z` (UTC)
- `2024-01-01T12:00:00+00:00` (UTC with offset)

**Timezone**: All timestamps are in UTC (Z suffix).

## Number Formats

### Decimal Numbers

- **Format**: Standard decimal notation
- **Precision**: Varies by field
- **Currency**: 2 decimal places (e.g., `125000.50`)
- **Percentages**: 2-4 decimal places (e.g., `16.83` for 16.83%)

### Currency

- **Format**: ISO 4217 currency codes
- **Examples**: `USD`, `EUR`, `GBP`, `JPY`
- **Default**: `USD`

## Backward Compatibility

### Adding Fields

- **Optional Fields**: Can be added without version change
- **Required Fields**: Require new API version

### Removing Fields

- **Any Field Removal**: Requires new API version

### Changing Field Types

- **Type Changes**: Require new API version
- **Example**: `string` → `integer` is breaking

### Enum Values

- **Adding Values**: Safe (non-breaking)
- **Removing Values**: Breaking (requires new version)

## Validation Rules

### Required Fields

Fields marked as `required` must be present in requests/responses.

### Format Validation

- IDs: Must match specified regex patterns
- Dates: Must be valid ISO 8601 format
- Numbers: Must be valid decimal numbers

### Range Validation

- **Quantities**: Must be >= 0
- **Prices**: Must be > 0
- **Percentages**: Typically -100 to 100 (for gains/losses)

## Examples

### Complete Account Example

```json
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
```

### Complete Portfolio Example

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

## JSON Schema

OpenAPI/Swagger schema is available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

The OpenAPI specification includes complete JSON schemas for all data models.
