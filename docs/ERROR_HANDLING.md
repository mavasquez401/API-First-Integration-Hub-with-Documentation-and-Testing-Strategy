# Error Handling

This document describes the error handling strategy and error response format for the API-First Integration Hub.

## Error Response Format

All errors follow **RFC7807 Problem Details for HTTP APIs** format. This ensures consistent, machine-readable error responses.

### Standard Error Response Structure

```json
{
  "type": "https://api.fidelity.com/problems/validation-error",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
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
  ],
  "metadata": null
}
```

### Error Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `type` | string (URI) | URI identifying the problem type |
| `title` | string | Human-readable summary of the problem type |
| `status` | integer | HTTP status code |
| `detail` | string | Human-readable explanation specific to this occurrence |
| `instance` | string (URI) | URI identifying the specific occurrence of the problem |
| `correlationId` | string (UUID) | Request correlation ID for tracing |
| `errorCode` | string | Application-specific error code |
| `timestamp` | string (ISO 8601) | When the error occurred |
| `violations` | array | Field-level validation errors (if applicable) |
| `metadata` | object | Additional error metadata (if applicable) |

## Error Codes

### Application Error Codes

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `BAD_REQUEST` | 400 | Invalid request format or parameters |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Authentication required |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `RATE_LIMIT_EXCEEDED` | 429 | Rate limit exceeded |
| `PROVIDER_ERROR` | 503 | External provider error |
| `SERVICE_UNAVAILABLE` | 503 | Service temporarily unavailable |
| `INTERNAL_ERROR` | 500 | Internal server error |

## HTTP Status Codes

### Client Errors (4xx)

| Status | Usage |
|--------|-------|
| `400 Bad Request` | Invalid request format, validation errors |
| `401 Unauthorized` | Missing or invalid authentication token |
| `403 Forbidden` | Valid authentication but insufficient permissions |
| `404 Not Found` | Requested resource does not exist |
| `429 Too Many Requests` | Rate limit exceeded |

### Server Errors (5xx)

| Status | Usage |
|--------|-------|
| `500 Internal Server Error` | Unexpected server error |
| `503 Service Unavailable` | Service or dependency unavailable |

## Common Error Scenarios

### Validation Error

**Request**: Invalid client ID format
```http
GET /api/v1/clients/invalid-format/accounts
```

**Response**: `400 Bad Request`
```json
{
  "type": "https://api.fidelity.com/problems/validation-error",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/api/v1/clients/invalid-format/accounts",
  "correlationId": "abc-123-def-456",
  "errorCode": "VALIDATION_ERROR",
  "timestamp": "2024-01-01T12:00:00Z",
  "violations": [
    {
      "field": "clientId",
      "message": "Client ID must match pattern CLIENT-{ID}",
      "rejectedValue": "invalid-format"
    }
  ]
}
```

### Resource Not Found

**Request**: Client that doesn't exist
```http
GET /api/v1/clients/CLIENT-NOTFOUND/accounts
```

**Response**: `404 Not Found`
```json
{
  "type": "https://api.fidelity.com/problems/resource-not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "Client not found: CLIENT-NOTFOUND",
  "instance": "/api/v1/clients/CLIENT-NOTFOUND/accounts",
  "correlationId": "abc-123-def-456",
  "errorCode": "NOT_FOUND",
  "timestamp": "2024-01-01T12:00:00Z",
  "violations": null
}
```

### Unauthorized

**Request**: Missing authentication token
```http
GET /api/v1/clients/CLIENT-123/accounts
```

**Response**: `401 Unauthorized`
```json
{
  "type": "https://api.fidelity.com/problems/unauthorized",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Authentication required",
  "instance": "/api/v1/clients/CLIENT-123/accounts",
  "correlationId": "abc-123-def-456",
  "errorCode": "UNAUTHORIZED",
  "timestamp": "2024-01-01T12:00:00Z",
  "violations": null
}
```

### Provider Error

**Request**: External provider (OMS/MarketData) error
```http
GET /api/v1/accounts/ACC-12345/portfolio
```

**Response**: `503 Service Unavailable`
```json
{
  "type": "https://api.fidelity.com/problems/provider-error",
  "title": "Service Unavailable",
  "status": 503,
  "detail": "External provider error: OMS service unavailable",
  "instance": "/api/v1/accounts/ACC-12345/portfolio",
  "correlationId": "abc-123-def-456",
  "errorCode": "PROVIDER_ERROR",
  "timestamp": "2024-01-01T12:00:00Z",
  "violations": null
}
```

## Error Handling Best Practices

### For API Consumers

1. **Check HTTP Status Code**: Always check the status code first
2. **Parse Error Response**: Use the structured error response for details
3. **Use Correlation ID**: Include correlation ID in support requests
4. **Handle Violations**: Check `violations` array for field-level errors
5. **Retry Logic**: Implement retry logic for `503` errors (with exponential backoff)
6. **Log Errors**: Log full error responses for debugging

### For API Providers

1. **Consistent Format**: Always use RFC7807 format
2. **Meaningful Messages**: Provide clear, actionable error messages
3. **Include Correlation ID**: Always include correlation ID for tracing
4. **Field-Level Errors**: Use `violations` array for validation errors
5. **Security**: Don't expose sensitive information in error messages
6. **Logging**: Log errors with full context (including correlation ID)

## Correlation IDs

Every error response includes a `correlationId` field. This ID:

- Is generated automatically if not provided in `X-Correlation-ID` header
- Is included in all log entries for the request
- Enables end-to-end request tracing
- Should be included in support requests

**Example**:
```http
Request:
GET /api/v1/clients/CLIENT-123/accounts
X-Correlation-ID: abc-123-def-456

Response:
{
  ...
  "correlationId": "abc-123-def-456",
  ...
}
```

## Error Recovery

### Retryable Errors

- `503 Service Unavailable` - Retry with exponential backoff
- `429 Too Many Requests` - Retry after rate limit reset

### Non-Retryable Errors

- `400 Bad Request` - Fix request and resubmit
- `401 Unauthorized` - Refresh authentication token
- `403 Forbidden` - Check permissions
- `404 Not Found` - Verify resource exists
- `500 Internal Server Error` - Contact support

## Monitoring and Alerting

Error responses are logged and monitored:

- Error rate thresholds trigger alerts
- Provider errors are tracked separately
- Correlation IDs enable request tracing
- Error trends are analyzed for improvements

## Support

For error-related support, include:
- Correlation ID
- Full error response
- Request details (endpoint, parameters)
- Timestamp

See [CONSUMER_PROVIDER_RESPONSIBILITIES.md](CONSUMER_PROVIDER_RESPONSIBILITIES.md) for more information.
