# Security

This document describes the security assumptions, authentication, and authorization for the API-First Integration Hub.

## Authentication

### OAuth2 JWT Resource Server

The API uses **OAuth2 JWT Bearer Token** authentication:

1. Clients obtain a JWT token from an OAuth2 authorization server
2. Clients include the token in the `Authorization` header
3. The API validates the token and extracts claims (user, scopes, roles)

**Authorization Header Format**:
```
Authorization: Bearer <jwt-token>
```

### JWT Token Validation

The API validates JWT tokens by:
1. Verifying token signature using JWK Set URI
2. Checking token expiration
3. Validating issuer (iss claim)
4. Extracting scopes and roles for authorization

**Configuration** (in `application.yml`):
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.fidelity.com/realms/integration
          jwk-set-uri: https://auth.fidelity.com/realms/integration/protocol/openid-connect/certs
```

### Public Endpoints

The following endpoints do not require authentication:
- `GET /api/v1/health`
- `GET /swagger-ui.html` (development only)
- `GET /api-docs` (development only)

All other endpoints require valid authentication.

## Authorization

### Role-Based Access Control (RBAC)

Authorization is based on roles and scopes extracted from the JWT token:

- **Scopes**: Fine-grained permissions (e.g., `accounts:read`, `portfolios:read`)
- **Roles**: Coarse-grained permissions (e.g., `ROLE_CLIENT`, `ROLE_ADMIN`)

**Future Enhancement**: Endpoints will be secured with method-level security:
```java
@PreAuthorize("hasAuthority('SCOPE_accounts:read')")
@GetMapping("/clients/{clientId}/accounts")
public ResponseEntity<List<AccountDto>> getAccounts(...) {
    // ...
}
```

### Current Security Model

Currently, all authenticated users have access to all endpoints. In production:
1. Implement role/scope-based authorization
2. Add method-level security annotations
3. Implement resource-level access control (e.g., users can only access their own accounts)

## Security Headers

The API includes security headers in responses:

- **Referrer-Policy**: `strict-origin-when-cross-origin`
- **Content-Security-Policy**: `default-src 'self'`
- **X-Content-Type-Options**: `nosniff` (via Spring Security defaults)

## Data Protection

### Sensitive Data

The following data is considered sensitive and is handled appropriately:

- **Account Numbers**: Masked in responses (e.g., `****1234`)
- **PII (Personally Identifiable Information)**: Handled per data privacy regulations
- **Financial Data**: Protected by encryption in transit (TLS) and at rest

### Data Masking

Account numbers and other sensitive identifiers are masked in API responses:
- Full account number: `1234567890` → `****7890`
- Client SSN: Not exposed via API

## Transport Security

### TLS/HTTPS

**Production Requirement**: All API communication must use HTTPS (TLS 1.2 or higher).

**Development**: Local development may use HTTP, but production must enforce HTTPS.

### Certificate Validation

- Server certificates must be valid and trusted
- Certificate pinning may be implemented for mobile clients
- mTLS (mutual TLS) may be required for certain endpoints

## API Keys

Currently, the API uses OAuth2 JWT tokens only. API keys are not supported.

**Future Consideration**: API keys may be added for service-to-service communication.

## Rate Limiting

Rate limiting is planned but not currently implemented:

- **Planned**: Per-client rate limits
- **Planned**: Per-endpoint rate limits
- **Headers**: Rate limit information will be included in response headers:
  ```
  X-RateLimit-Limit: 1000
  X-RateLimit-Remaining: 999
  X-RateLimit-Reset: 1609459200
  ```

## Security Best Practices

### For API Consumers

1. **Secure Token Storage**: Store JWT tokens securely (not in localStorage for web apps)
2. **Token Refresh**: Implement token refresh logic before expiration
3. **HTTPS Only**: Always use HTTPS in production
4. **Validate Certificates**: Don't disable certificate validation
5. **Error Handling**: Don't log tokens in error messages
6. **Correlation IDs**: Use correlation IDs for secure request tracing

### For API Providers

1. **Input Validation**: Validate all input parameters
2. **Output Sanitization**: Sanitize output to prevent injection attacks
3. **Error Messages**: Don't expose sensitive information in error messages
4. **Logging**: Don't log sensitive data (tokens, PII)
5. **Dependencies**: Keep dependencies updated (security patches)
6. **Security Scanning**: Regular security scans (OWASP ZAP, dependency scanning)

## OWASP Top 10 Considerations

The API is designed to address OWASP Top 10 security risks:

1. **A01: Broken Access Control** - OAuth2 + RBAC
2. **A02: Cryptographic Failures** - TLS + data masking
3. **A03: Injection** - Input validation + parameterized queries
4. **A04: Insecure Design** - Security-first design
5. **A05: Security Misconfiguration** - Secure defaults
6. **A06: Vulnerable Components** - Dependency management
7. **A07: Authentication Failures** - OAuth2 JWT
8. **A08: Software and Data Integrity** - Dependency verification
9. **A09: Logging Failures** - Structured logging with correlation IDs
10. **A10: SSRF** - Input validation + URL whitelisting

## Security Testing

### Testing Approaches

1. **Security Unit Tests**: Test authentication/authorization logic
2. **Security Integration Tests**: Test with mock tokens
3. **Postman Tests**: Test authentication flows
4. **Security Scanning**: OWASP ZAP, dependency scanning

### Security Test Examples

See test files:
- `AccountControllerTest` - Tests authentication requirements
- `SecurityConfig` - Security configuration tests

## Incident Response

### Security Incident Handling

1. **Identify**: Detect security incidents through monitoring
2. **Contain**: Isolate affected systems
3. **Eradicate**: Remove threat
4. **Recover**: Restore normal operations
5. **Learn**: Post-incident review

### Reporting Security Issues

Report security vulnerabilities to: security@fidelity.com (example)

**Include**:
- Description of vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if available)

## Compliance

### Regulatory Considerations

- **GDPR**: Data privacy and right to erasure
- **PCI DSS**: If handling payment card data
- **SOC 2**: Security controls and monitoring
- **HIPAA**: If handling healthcare data

### Data Retention

- Logs: Retained per compliance requirements
- Audit trails: Maintained for security events
- Data deletion: Implemented per data retention policies

## Future Enhancements

- [ ] Implement role/scope-based authorization
- [ ] Add rate limiting
- [ ] Add API key support for service-to-service
- [ ] Implement mTLS for certain endpoints
- [ ] Add security event logging and monitoring
- [ ] Regular security audits and penetration testing
