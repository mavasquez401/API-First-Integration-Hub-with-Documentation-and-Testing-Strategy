# Consumer and Provider Responsibilities

This document outlines the responsibilities of API consumers (clients) and the API provider (Integration Hub) to ensure reliable integration.

## API Provider Responsibilities

### Service Availability

- **SLA**: Maintain service availability as defined in SLA (e.g., 99.9% uptime)
- **Maintenance Windows**: Provide advance notice of planned maintenance
- **Status Page**: Maintain status page for service health and incidents
- **Monitoring**: Monitor service health and dependencies

### API Contracts

- **Stability**: Maintain backward compatibility within API versions
- **Documentation**: Provide comprehensive, up-to-date API documentation
- **Change Management**: Follow versioning policy for breaking changes
- **Deprecation**: Provide advance notice (6+ months) before deprecating versions

### Error Handling

- **Consistent Format**: Return errors in RFC7807 Problem Details format
- **Meaningful Messages**: Provide clear, actionable error messages
- **Correlation IDs**: Include correlation IDs in all responses for tracing
- **Status Codes**: Use appropriate HTTP status codes

### Security

- **Authentication**: Support OAuth2 JWT authentication
- **Authorization**: Implement role/scope-based access control
- **Data Protection**: Protect sensitive data (encryption, masking)
- **Security Updates**: Apply security patches promptly

### Performance

- **Response Times**: Meet response time SLAs
- **Rate Limiting**: Implement fair rate limiting with clear limits
- **Caching**: Use appropriate caching headers when applicable
- **Pagination**: Provide pagination for large datasets (future)

### Support

- **Documentation**: Maintain API documentation and examples
- **Support Channels**: Provide support channels for issues
- **Incident Response**: Respond to incidents within SLA
- **Communication**: Communicate service changes and incidents

## API Consumer Responsibilities

### Authentication and Authorization

- **Token Management**: Securely store and manage JWT tokens
- **Token Refresh**: Implement token refresh before expiration
- **Scope Requests**: Request appropriate scopes for operations
- **Token Rotation**: Rotate tokens periodically for security

### Error Handling

- **Status Codes**: Handle all HTTP status codes appropriately
- **Error Parsing**: Parse RFC7807 error responses correctly
- **Retry Logic**: Implement retry logic for transient errors (503, 429)
- **Exponential Backoff**: Use exponential backoff for retries
- **Correlation IDs**: Include correlation IDs in support requests

### Request Management

- **Input Validation**: Validate input before sending requests
- **Idempotency**: Make requests idempotent when possible
- **Timeouts**: Implement appropriate request timeouts
- **Connection Pooling**: Use connection pooling for efficiency

### Rate Limiting

- **Rate Limit Awareness**: Respect rate limits (check response headers)
- **Backoff Strategy**: Implement backoff when rate limited (429)
- **Request Batching**: Batch requests when possible to reduce API calls
- **Caching**: Cache responses when appropriate

### Data Handling

- **Data Validation**: Validate API responses before use
- **Error Scenarios**: Handle missing/null fields gracefully
- **Data Types**: Use correct data types (dates, numbers, enums)
- **Version Handling**: Handle enum values that may be added in future

### Monitoring and Logging

- **Logging**: Log requests, responses, and errors
- **Monitoring**: Monitor API usage and error rates
- **Alerting**: Set up alerts for API errors
- **Metrics**: Track API performance metrics

### Version Management

- **Version Pinning**: Pin to specific API version (don't use "latest")
- **Deprecation Monitoring**: Monitor deprecation headers
- **Migration Planning**: Plan migrations when versions are deprecated
- **Testing**: Test new versions in non-production first

### Security

- **HTTPS**: Always use HTTPS in production
- **Token Security**: Never log or expose tokens
- **Certificate Validation**: Don't disable certificate validation
- **Input Sanitization**: Sanitize data before logging

## Service Level Agreements (SLAs)

### Availability

- **Target**: 99.9% uptime (subject to actual SLA agreement)
- **Maintenance**: Excluded from uptime calculations
- **Incidents**: Reported via status page

### Response Times

| Endpoint | Target Response Time |
|----------|---------------------|
| Health Check | < 100ms |
| Account Lookup | < 500ms |
| Portfolio Aggregation | < 2000ms |
| Reference Data | < 500ms |

**Note**: Response times may vary based on:
- External provider response times
- Data volume
- System load

### Rate Limits

**Planned** (not yet implemented):
- Per-client rate limits
- Per-endpoint rate limits
- Rate limit headers in responses

## Retry Strategy

### Retryable Errors

- **503 Service Unavailable**: Retry with exponential backoff
- **429 Too Many Requests**: Retry after rate limit reset (check `Retry-After` header)

### Non-Retryable Errors

- **400 Bad Request**: Fix request and resubmit (don't retry)
- **401 Unauthorized**: Refresh token (don't retry with same token)
- **403 Forbidden**: Check permissions (don't retry)
- **404 Not Found**: Verify resource exists (don't retry)
- **500 Internal Server Error**: Contact support (don't retry)

### Retry Best Practices

1. **Exponential Backoff**: Start with 1 second, double each retry
2. **Max Retries**: Limit to 3-5 retries
3. **Jitter**: Add random jitter to prevent thundering herd
4. **Timeout**: Set maximum retry time window

**Example Retry Implementation**:
```java
int maxRetries = 3;
long baseDelayMs = 1000;
for (int attempt = 0; attempt < maxRetries; attempt++) {
    try {
        return apiCall();
    } catch (ServiceUnavailableException e) {
        if (attempt == maxRetries - 1) throw e;
        long delay = baseDelayMs * (1 << attempt); // Exponential backoff
        Thread.sleep(delay + randomJitter());
    }
}
```

## Idempotency

### Idempotent Operations

GET requests are naturally idempotent. For future POST/PUT/DELETE operations:

- **Idempotency Keys**: Use idempotency keys for non-idempotent operations
- **Request Deduplication**: Provider may deduplicate requests with same idempotency key
- **Idempotency Header**: `Idempotency-Key: <uuid>`

### Best Practices

1. **Generate Keys**: Generate unique idempotency keys per request
2. **Key Format**: Use UUIDs for idempotency keys
3. **Key Scope**: Scope keys to operation and resource
4. **Key Storage**: Store keys to detect duplicates

## Timeouts

### Recommended Timeouts

| Operation Type | Recommended Timeout |
|----------------|-------------------|
| Health Check | 5 seconds |
| Simple Lookups | 10 seconds |
| Aggregations | 30 seconds |
| Long-Running | 60+ seconds |

### Timeout Handling

1. **Set Timeouts**: Always set request timeouts
2. **Timeout Errors**: Handle timeout errors appropriately
3. **Retry Consideration**: Consider retrying timeout errors
4. **User Experience**: Provide user feedback for long operations

## Pagination (Future)

When pagination is implemented:

### Cursor-Based Pagination

```json
{
  "data": [...],
  "pagination": {
    "nextCursor": "eyJpZCI6MTIzfQ",
    "hasMore": true
  }
}
```

### Consumer Responsibilities

- **Follow Cursors**: Use next cursor for subsequent requests
- **Handle Empty**: Handle empty results gracefully
- **Limit Pages**: Implement page limits to prevent infinite loops

## Data Consistency

### Eventual Consistency

Some data may be eventually consistent:
- Market data prices (updated frequently)
- Aggregated portfolio values

### Handling Staleness

1. **Cache Headers**: Respect cache headers from provider
2. **Refresh Logic**: Implement refresh logic for stale data
3. **User Communication**: Communicate data freshness to users

## Incident Response

### Consumer Responsibilities

1. **Monitor Status**: Monitor provider status page
2. **Check Logs**: Check logs for correlation IDs
3. **Report Issues**: Report issues with correlation IDs
4. **Follow Updates**: Follow provider incident updates

### Provider Responsibilities

1. **Status Updates**: Provide regular status updates
2. **Root Cause**: Communicate root cause analysis
3. **Resolution**: Communicate resolution and timeline
4. **Post-Mortem**: Share post-mortem for major incidents

## Support

### Support Channels

- **Documentation**: API documentation and guides
- **Status Page**: Service status and incidents
- **Support Email**: support@fidelity.com (example)
- **Developer Portal**: Developer portal with examples

### Support Information

When requesting support, include:
- **Correlation ID**: From error response
- **Request Details**: Endpoint, parameters, timestamp
- **Error Response**: Full error response
- **Reproduction Steps**: Steps to reproduce issue

## Compliance and Legal

### Data Privacy

- **PII Handling**: Handle PII according to regulations (GDPR, etc.)
- **Data Retention**: Follow data retention policies
- **Data Deletion**: Implement data deletion when required

### Terms of Service

- **Acceptable Use**: Follow acceptable use policies
- **Rate Limits**: Respect rate limits
- **Data Usage**: Use data according to terms

## Best Practices Summary

### For Consumers

✅ **Do**:
- Implement proper error handling and retries
- Use correlation IDs for tracing
- Monitor deprecation headers
- Cache responses when appropriate
- Implement timeout and connection pooling

❌ **Don't**:
- Ignore error responses
- Retry non-retryable errors
- Hardcode API versions
- Log sensitive data (tokens, PII)
- Disable certificate validation

### For Providers

✅ **Do**:
- Maintain API stability and documentation
- Provide advance notice for changes
- Include correlation IDs in responses
- Monitor service health
- Communicate incidents promptly

❌ **Don't**:
- Make breaking changes without versioning
- Expose sensitive data in errors
- Skip deprecation process
- Ignore consumer feedback
- Change contracts without notice
