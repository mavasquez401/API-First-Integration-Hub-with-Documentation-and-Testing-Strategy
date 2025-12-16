# API-First Integration Hub

A Spring Boot-based integration hub that provides REST APIs for portfolio and account aggregation, integrating with internal OMS systems and external market data vendors.

## Overview

The API-First Integration Hub is designed following enterprise API best practices, including:

- **RESTful API Design**: Versioned REST endpoints with OpenAPI documentation
- **Enterprise Error Handling**: RFC7807 Problem Details format
- **Security**: OAuth2 JWT authentication and authorization
- **Testing**: Comprehensive testing strategy with Postman/Newman automation
- **Documentation**: Fidelity-style documentation covering all aspects

## Features

- **Account Management**: Retrieve accounts for clients with filtering
- **Portfolio Aggregation**: Aggregate positions and valuations across systems
- **Reference Data**: Instrument metadata and market data
- **Health Checks**: Service health and dependency monitoring
- **OpenAPI Documentation**: Interactive Swagger UI for API exploration

## Architecture

The hub follows a layered architecture:

```
┌─────────────────────────────────────────┐
│         REST Controllers (v1)           │
│  (Account, Portfolio, Reference, Health)│
└──────────────┬──────────────────────────┘
               │
┌──────────────┴──────────────────────────┐
│        Service Layer                    │
│  (AccountService, PortfolioService)     │
└──────────────┬──────────────────────────┘
               │
┌──────────────┴──────────────────────────┐
│        Adapter Layer                    │
│  (OmsAdapter, MarketDataVendorAdapter)  │
└──────────────┬──────────────────────────┘
               │
     ┌─────────┴─────────┐
     │                   │
┌────▼────┐       ┌──────▼──────┐
│   OMS   │       │ Market Data │
│  System │       │   Vendor    │
└─────────┘       └─────────────┘
```

## Getting Started

### Prerequisites

- **Java 17+**: Required for building and running
- **Maven 3.6+**: For building the project
- **Node.js** (optional): For running Postman/Newman tests

### Building the Project

```bash
# Clone the repository
git clone <repository-url>
cd API-First-Integration-Hub

# Build the project
mvn clean install

# Run tests
mvn test
```

### Running the Application

```bash
# Run with Maven
mvn spring-boot:run

# Or run the JAR
java -jar target/api-first-integration-hub-1.0.0-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### Configuration

Configuration is in `src/main/resources/application.yml`.

#### Local/dev mode (default)

By default the app runs with auth **disabled** so you can hit endpoints without a real IdP:

```yaml
integration:
  hub:
    security:
      enabled: false
```

#### Production mode (OAuth2 JWT)

Enable security and provide your IdP settings (recommended via env vars or your deployment config):

```yaml
integration:
  hub:
    security:
      enabled: true
```

Then configure Spring Security OAuth2 resource server properties in your deployment (example):

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-idp.example/realms/integration
          jwk-set-uri: https://your-idp.example/realms/integration/protocol/openid-connect/certs
```

## API Documentation

### Swagger UI

Interactive API documentation is available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### API Endpoints

#### Health Check
```
GET /api/v1/health
```

#### Accounts
```
GET /api/v1/clients/{clientId}/accounts
GET /api/v1/clients/{clientId}/accounts?accountStatus=ACTIVE&accountType=BROKERAGE
```

#### Portfolios
```
GET /api/v1/accounts/{accountId}/portfolio
```

#### Reference Data
```
GET /api/v1/reference/instruments/{symbol}
```

See [docs/API.md](docs/API.md) for complete API documentation.

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn test -Dtest=*IntegrationTest
```

### Postman/Newman Tests

```bash
# Install Newman (if not installed)
npm install -g newman newman-reporter-html

# Run Postman collection
./postman/newman.sh
```

See [docs/TESTING_STRATEGY.md](docs/TESTING_STRATEGY.md) for comprehensive testing documentation.

## Documentation

Comprehensive documentation is available in the `docs/` directory:

- **[API.md](docs/API.md)**: Complete API reference with examples
- **[ERROR_HANDLING.md](docs/ERROR_HANDLING.md)**: Error handling and RFC7807 Problem Details
- **[SECURITY.md](docs/SECURITY.md)**: Security, authentication, and authorization
- **[VERSIONING.md](docs/VERSIONING.md)**: API versioning strategy
- **[DATA_CONTRACTS.md](docs/DATA_CONTRACTS.md)**: Data models, schemas, and enums
- **[CONSUMER_PROVIDER_RESPONSIBILITIES.md](docs/CONSUMER_PROVIDER_RESPONSIBILITIES.md)**: Integration responsibilities
- **[TESTING_STRATEGY.md](docs/TESTING_STRATEGY.md)**: Testing strategy and best practices

## Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/com/fidelity/integration/hub/
│   │   │   ├── controller/v1/      # REST controllers
│   │   │   ├── service/            # Business logic
│   │   │   ├── adapter/            # External system adapters
│   │   │   ├── model/              # DTOs and enums
│   │   │   ├── config/             # Configuration
│   │   │   ├── exception/          # Exception handling
│   │   │   └── filter/             # Filters (correlation IDs)
│   │   └── resources/
│   │       └── application.yml     # Configuration
│   └── test/                       # Tests
├── docs/                           # Documentation
├── postman/                        # Postman collections
└── pom.xml                         # Maven configuration
```

## Key Technologies

- **Spring Boot 3.2.0**: Application framework
- **Spring Security**: OAuth2 JWT resource server
- **Springdoc OpenAPI**: API documentation
- **Lombok**: Reducing boilerplate code
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **WireMock**: HTTP service mocking (for tests)
- **Postman/Newman**: API testing automation

## Standards and Best Practices

This project follows enterprise API standards:

- **RESTful Design**: Resource-based URLs, HTTP methods, status codes
- **Versioning**: URI versioning (`/api/v1`)
- **Error Handling**: RFC7807 Problem Details format
- **Security**: OAuth2 JWT authentication
- **Documentation**: OpenAPI/Swagger with comprehensive examples
- **Testing**: Layered testing strategy (unit, integration, E2E)
- **Logging**: Structured logging with correlation IDs

## Contributing

1. Follow the existing code style
2. Add tests for new features
3. Update documentation as needed
4. Ensure all tests pass
5. Follow the versioning strategy for API changes

## License

[Your License Here]

## Support

For API support and questions:
- **Documentation**: See `docs/` directory
- **API Issues**: Include correlation ID from error response
- **Security Issues**: Report to security@fidelity.com (example)

## Acknowledgments

This project demonstrates enterprise API integration patterns and best practices for building production-ready REST APIs.
