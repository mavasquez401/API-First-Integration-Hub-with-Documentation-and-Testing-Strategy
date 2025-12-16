#!/bin/bash

# Newman script for running Postman collection tests
# 
# Prerequisites:
#   - Install Newman: npm install -g newman
#   - Install HTML reporter: npm install -g newman-reporter-html
#
# Usage:
#   ./newman.sh                    # Run with default environment
#   ./newman.sh local              # Run with local environment
#   ./newman.sh --env custom.json  # Run with custom environment file

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COLLECTION_FILE="${SCRIPT_DIR}/IntegrationHub.postman_collection.json"
ENV_FILE="${SCRIPT_DIR}/IntegrationHub.postman_environment.json"
REPORT_DIR="${SCRIPT_DIR}/reports"

# Create reports directory if it doesn't exist
mkdir -p "${REPORT_DIR}"

# Generate timestamp for report filename
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
HTML_REPORT="${REPORT_DIR}/newman-report-${TIMESTAMP}.html"
JSON_REPORT="${REPORT_DIR}/newman-report-${TIMESTAMP}.json"

# Check if custom environment file is provided
if [ "$1" == "--env" ] && [ -n "$2" ]; then
    ENV_FILE="$2"
    echo "Using custom environment file: ${ENV_FILE}"
fi

# Verify files exist
if [ ! -f "${COLLECTION_FILE}" ]; then
    echo "Error: Collection file not found: ${COLLECTION_FILE}"
    exit 1
fi

if [ ! -f "${ENV_FILE}" ]; then
    echo "Error: Environment file not found: ${ENV_FILE}"
    exit 1
fi

echo "Running Newman tests..."
echo "Collection: ${COLLECTION_FILE}"
echo "Environment: ${ENV_FILE}"
echo ""

# Run Newman with HTML and JSON reporters
newman run "${COLLECTION_FILE}" \
    --environment "${ENV_FILE}" \
    --reporters cli,html,json \
    --reporter-html-export "${HTML_REPORT}" \
    --reporter-json-export "${JSON_REPORT}" \
    --bail

EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo "✓ All tests passed!"
    echo "HTML Report: ${HTML_REPORT}"
    echo "JSON Report: ${JSON_REPORT}"
else
    echo ""
    echo "✗ Some tests failed (exit code: ${EXIT_CODE})"
    echo "Check reports for details:"
    echo "HTML Report: ${HTML_REPORT}"
    echo "JSON Report: ${JSON_REPORT}"
fi

exit $EXIT_CODE
