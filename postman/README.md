# Postman Collection for API-First Integration Hub

This directory contains Postman collections and scripts for testing the Integration Hub APIs.

## Files

- `IntegrationHub.postman_collection.json` - Postman collection with all API endpoints and test cases
- `IntegrationHub.postman_environment.json` - Environment variables for local testing
- `newman.sh` - Script to run collection tests via Newman CLI
- `reports/` - Directory for generated test reports (created automatically)

## Prerequisites

### Install Newman

```bash
npm install -g newman
npm install -g newman-reporter-html
```

### Install Postman (Optional)

If you prefer using the Postman GUI:
1. Open Postman
2. Import `IntegrationHub.postman_collection.json`
3. Import `IntegrationHub.postman_environment.json`
4. Select the "Integration Hub - Local" environment

## Running Tests

### Using Newman CLI

```bash
# Run with default environment
./postman/newman.sh

# Run with custom environment file
./postman/newman.sh --env custom-environment.json
```

### Using Postman GUI

1. Select the "Integration Hub - Local" environment
2. Click "Run" on the collection
3. Review test results in the test runner

## Environment Variables

The default environment includes:

- `baseUrl`: Base URL for the API (default: http://localhost:8080)
- `authToken`: JWT token for authentication (mock token for testing)
- `clientId`: Test client ID (CLIENT-98765)
- `accountId`: Test account ID (ACC-12345)
- `instrumentSymbol`: Test instrument symbol (AAPL)

## Test Coverage

The collection includes tests for:

1. **Health Endpoint**
   - Health check (success case)

2. **Accounts Endpoints**
   - Get accounts by client (success)
   - Get accounts with status filter
   - Client not found (error case)
   - Invalid client ID format (validation error)

3. **Portfolio Endpoints**
   - Get portfolio by account (success)
   - Account not found (error case)

4. **Reference Data Endpoints**
   - Get instrument by symbol (success)
   - Instrument not found (error case)
   - Invalid symbol format (validation error)

## Authentication

Note: For local testing, you may need to disable security or configure a mock token. 
In production, replace `authToken` with a valid JWT token from your OAuth2 provider.

## Reports

Test reports are generated in the `reports/` directory:
- HTML report: `newman-report-YYYYMMDD_HHMMSS.html`
- JSON report: `newman-report-YYYYMMDD_HHMMSS.json`

Open the HTML report in a browser to view detailed test results.
