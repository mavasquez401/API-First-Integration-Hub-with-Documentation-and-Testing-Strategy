# API Versioning Strategy

This document describes the API versioning strategy for the API-First Integration Hub.

## Versioning Approach

The API uses **URI Versioning** (also known as URL versioning):

```
/api/v1/clients/{clientId}/accounts
/api/v2/clients/{clientId}/accounts  (future)
```

## Current Version

**Current API Version**: `v1`

All endpoints are prefixed with `/api/v1`.

## Versioning Principles

### When to Version

New API versions are created when:

1. **Breaking Changes**: Changes that break backward compatibility
   - Removing endpoints
   - Removing required fields
   - Changing field types (e.g., string → integer)
   - Changing required fields to optional (may break consumers)

2. **Non-Breaking Changes**: These do NOT require a new version:
   - Adding new endpoints
   - Adding optional fields
   - Adding new enum values
   - Fixing bugs that don't change behavior

### Backward Compatibility

The API maintains backward compatibility within a version:

- **Field Addition**: New optional fields can be added
- **Endpoint Addition**: New endpoints can be added
- **Behavior Changes**: Bug fixes that don't change expected behavior

## Version Lifecycle

### Version Support Policy

| Phase | Duration | Support Level |
|-------|----------|---------------|
| **Current** | Active | Full support, active development |
| **Deprecated** | 6 months | Bug fixes only, no new features |
| **Sunset** | 3 months | Security fixes only |
| **Retired** | - | No support, returns 410 Gone |

### Deprecation Process

1. **Announcement**: Deprecated versions are announced 6 months before sunset
2. **Deprecation Header**: Deprecated endpoints include:
   ```
   Deprecation: true
   Sunset: Sat, 01 Jul 2024 00:00:00 GMT
   Link: <https://api.fidelity.com/api/v2>; rel="successor-version"
   ```
3. **Documentation**: Deprecation is documented in API docs
4. **Communication**: Consumers are notified via email/portal

### Example Timeline

```
v1 Launch:       2024-01-01
v2 Launch:       2024-06-01
v1 Deprecation:  2024-06-01 (announced)
v1 Sunset:       2024-12-01
v1 Retirement:   2025-03-01
```

## Version Numbering

### Semantic Versioning (for API versions)

API versions use major version numbers (`v1`, `v2`, `v3`):

- **Major Version**: Breaking changes only
- **Minor/Patch**: Not used in URI (handled via content negotiation if needed)

### Internal Versioning

Internal service versioning uses semantic versioning:

- **Maven Version**: `1.0.0-SNAPSHOT` → `1.0.0` → `1.1.0` → `2.0.0`
- **API Version**: Always matches major version (`v1`, `v2`, etc.)

## Migration Guide

### Migrating Between Versions

When migrating from `v1` to `v2`:

1. **Review Changelog**: Check breaking changes
2. **Update Endpoints**: Update base URL from `/api/v1` to `/api/v2`
3. **Update DTOs**: Update request/response models
4. **Update Tests**: Update test cases
5. **Test Thoroughly**: Test in non-production environment first
6. **Deploy**: Deploy updated consumer application

### Breaking Changes Documentation

Breaking changes are documented in:
- **CHANGELOG.md**: Version-specific changes
- **Migration Guide**: Step-by-step migration instructions
- **API Documentation**: Updated endpoint documentation

## Version Negotiation

### Content Negotiation (Future)

Future versions may support content negotiation:

```http
Accept: application/vnd.fidelity.v2+json
```

Currently, URI versioning is the only method.

## Version Headers

### Request Headers

Clients can specify API version preference:

```
API-Version: v1
```

This is optional - URI version takes precedence.

### Response Headers

Responses include version information:

```
API-Version: v1
Deprecation: false
```

## Examples

### Versioned Endpoints

**v1**:
```http
GET /api/v1/clients/{clientId}/accounts
```

**v2** (hypothetical):
```http
GET /api/v2/clients/{clientId}/accounts
```

### Deprecated Endpoint Response

**Request**:
```http
GET /api/v1/clients/{clientId}/accounts
```

**Response Headers**:
```
HTTP/1.1 200 OK
API-Version: v1
Deprecation: true
Sunset: Sat, 01 Jul 2024 00:00:00 GMT
Link: <https://api.fidelity.com/api/v2/clients/{clientId}/accounts>; rel="successor-version"
```

### Retired Endpoint Response

**Request**:
```http
GET /api/v1/clients/{clientId}/accounts
```

**Response**:
```
HTTP/1.1 410 Gone
Content-Type: application/problem+json

{
  "type": "https://api.fidelity.com/problems/version-retired",
  "title": "Gone",
  "status": 410,
  "detail": "API version v1 has been retired. Please migrate to v2.",
  "instance": "/api/v1/clients/{clientId}/accounts",
  "migrationGuide": "https://api.fidelity.com/docs/v1-to-v2-migration"
}
```

## Best Practices

### For API Consumers

1. **Pin to Version**: Always specify API version in URLs
2. **Monitor Deprecation**: Check `Deprecation` and `Sunset` headers
3. **Plan Migrations**: Start migration planning when deprecation is announced
4. **Test Versions**: Test new versions in non-production first
5. **Version in Code**: Make version configurable (not hardcoded)

### For API Providers

1. **Document Changes**: Document all changes in CHANGELOG
2. **Provide Migration Guides**: Help consumers migrate smoothly
3. **Give Notice**: Provide 6+ months notice before deprecation
4. **Maintain Versions**: Support multiple versions during transition
5. **Monitor Usage**: Track version usage to inform retirement decisions

## Version History

| Version | Release Date | Status | Notes |
|---------|--------------|--------|-------|
| v1 | 2024-01-01 | Current | Initial API release |

## Future Considerations

- **Content Negotiation**: May add content negotiation for minor versions
- **Version Discovery**: API to list available versions
- **Version Aliases**: Alias `latest` to current version (not recommended for production)
- **Feature Flags**: Per-client feature flags for gradual rollouts
