# Testing Strategy

This document outlines the comprehensive testing strategy for the API-First Integration Hub.

## Testing Pyramid

The testing strategy follows the testing pyramid principle:

```
        /\
       /  \
      / E2E \          (Fewer tests - High-level validation)
     /--------\
    /Integration\      (Some tests - Component interaction)
   /-------------\
  /   Unit Tests  \    (Many tests - Fast, isolated)
 /-----------------\
```

## Test Types

### 1. Unit Tests

**Purpose**: Test individual components in isolation with mocked dependencies.

**Location**: `src/test/java/com/fidelity/integration/hub/service/`

**Examples**:
- `AccountServiceTest` - Tests service business logic
- `PortfolioServiceTest` - Tests portfolio aggregation logic
- `ReferenceDataServiceTest` - Tests reference data operations

**Characteristics**:
- Fast execution (< 1 second per test)
- No external dependencies
- Use Mockito for mocking
- Test single responsibility

**Running**:
```bash
mvn test
```

### 2. Controller Slice Tests

**Purpose**: Test web layer (controllers) in isolation, focusing on:
- Request validation
- HTTP status codes
- Response format
- Error handling (RFC7807 Problem Details)

**Location**: `src/test/java/com/fidelity/integration/hub/controller/`

**Examples**:
- `AccountControllerTest` - Tests account endpoints
- `PortfolioControllerTest` - Tests portfolio endpoints
- `ReferenceDataControllerTest` - Tests reference data endpoints

**Characteristics**:
- Use `@WebMvcTest` for Spring MVC slice
- Mock service layer
- Test HTTP concerns only
- Verify Problem Details format for errors

**Running**:
```bash
mvn test -Dtest=*ControllerTest
```

### 3. Integration Tests

**Purpose**: Test component interactions within the Spring context.

**Location**: `src/test/java/com/fidelity/integration/hub/integration/`

**Examples**:
- `PortfolioIntegrationTest` - Tests portfolio service with adapters
- `AccountIntegrationTest` - Tests account service with adapters

**Characteristics**:
- Use `@SpringBootTest` for full context
- Can use simulated adapters or WireMock
- Test real component wiring
- Slower than unit tests

**Running**:
```bash
mvn test -Dtest=*IntegrationTest
```

### 4. API Contract Tests

**Purpose**: Verify API contracts between consumers and providers.

**Options**:
- **Spring Cloud Contract** - Provider-driven contract testing
- **Pact** - Consumer-driven contract testing

**When to Use**:
- When integrating with external services
- To ensure backward compatibility
- To document API expectations

**Example (Pact - Consumer Side)**:
```java
@ExtendWith(PactConsumerTestExt.class)
class AccountServicePactTest {
    @Pact(consumer = "IntegrationHub", provider = "OMS")
    public RequestResponsePact getAccountsPact(PactDslWithProvider builder) {
        return builder
            .given("client exists")
            .uponReceiving("a request for accounts")
            .path("/api/clients/CLIENT-123/accounts")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(/* ... */)
            .toPact();
    }
}
```

**Example (Spring Cloud Contract - Provider Side)**:
```groovy
// src/test/resources/contracts/oms/account.groovy
Contract.make {
    request {
        method 'GET'
        url '/api/clients/CLIENT-123/accounts'
    }
    response {
        status 200
        body([
            accountId: $(anyNonBlankString()),
            clientId: 'CLIENT-123'
        ])
    }
}
```

### 5. Postman/Newman Tests

**Purpose**: End-to-end API testing with real HTTP requests.

**Location**: `postman/`

**Files**:
- `IntegrationHub.postman_collection.json` - Test collection
- `IntegrationHub.postman_environment.json` - Environment variables
- `newman.sh` - Script to run tests

**Running**:
```bash
./postman/newman.sh
```

**Characteristics**:
- Tests actual HTTP endpoints
- Can test authentication/authorization
- Validates complete request/response flow
- Useful for CI/CD pipelines

## Test Configuration

### Test Profile

Test-specific configuration is in `src/test/resources/application-test.yml`:

```yaml
integration:
  hub:
    security:
      enabled: false

logging:
  level:
    root: INFO
    com.fidelity.integration.hub: DEBUG
```

### Mock Security

For controller tests, use `@WithMockUser`:

```java
@WithMockUser
void getAccounts_Success_Returns200() {
    // Test code
}
```

## Test Data Management

### In-Memory Data

For unit tests, use in-memory test data:

```java
private Account createTestAccount() {
    return Account.builder()
        .accountId("ACC-12345")
        .clientId("CLIENT-98765")
        // ...
        .build();
}
```

### External Dependencies

For integration tests with external services:

1. **WireMock** - Mock HTTP services
2. **Testcontainers** - Run real services in containers
3. **Simulated Adapters** - Use mock implementations (current approach)

## Code Coverage

### Target Coverage

- **Unit Tests**: 80%+ coverage
- **Integration Tests**: Critical paths covered
- **Controller Tests**: All endpoints covered

### Generating Coverage Report

```bash
mvn clean test jacoco:report
```

View report at: `target/site/jacoco/index.html`

## Continuous Integration

### CI Pipeline Tests

1. **Unit Tests** - Fast feedback (< 2 minutes)
2. **Controller Tests** - Verify API contracts
3. **Integration Tests** - Component interactions
4. **Postman/Newman Tests** - End-to-end validation

### Example GitHub Actions

```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: mvn clean test
      - run: ./postman/newman.sh
```

## Best Practices

1. **Test Naming**: Use descriptive names like `methodName_scenario_expectedResult()`
2. **Arrange-Act-Assert**: Structure tests clearly
3. **One Assertion Per Test**: When possible, focus on one behavior
4. **Mock External Dependencies**: Keep tests fast and isolated
5. **Test Error Cases**: Verify error handling and Problem Details format
6. **Use Test Fixtures**: Create reusable test data builders
7. **Maintain Test Data**: Keep test data consistent and realistic

## Test Maintenance

### When to Update Tests

- When adding new endpoints
- When changing API contracts
- When fixing bugs (add regression tests)
- When refactoring business logic

### Test Documentation

- Document complex test scenarios
- Explain why certain tests exist
- Keep test code readable and maintainable

## Future Enhancements

- [ ] Add performance/load tests
- [ ] Add security tests (OWASP ZAP integration)
- [ ] Add contract tests (Pact/Spring Cloud Contract)
- [ ] Add mutation testing (PIT)
- [ ] Add visual regression tests for API documentation
