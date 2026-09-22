# API Automation Hub

> A modular Java API test automation framework built with REST Assured and TestNG, designed around reusable framework components, application-specific API suites, structured validation, and CI-ready reporting.

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-Multi--Module-C71A36)](https://maven.apache.org/)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5.5.5-25A162)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.11.0-red)](https://testng.org/)
[![Allure](https://img.shields.io/badge/Allure-Reporting-FF6B35)](https://allurereport.org/)

---

## Overview

`api-automation-hub` is a **Maven multi-module API automation framework** built with Java, REST Assured, and TestNG.

The repository is structured around a shared framework layer and multiple API-specific test modules. This separates reusable automation infrastructure from service-specific test scenarios and makes the project suitable for demonstrating API automation architecture rather than isolated test scripts.

### The architecture

```text
                    API Automation Hub
                           │
                           ▼
                 ┌───────────────────┐
                 │   framework-core   │
                 │                   │
                 │ Request Builders  │
                 │ Configuration     │
                 │ Validation        │
                 │ Retry             │
                 │ Listeners         │
                 │ Logging           │
                 └─────────┬─────────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
   RESTful Booker       Petstore        ReqRes
          │                │                │
          └────────────────┼────────────────┘
                           │
             ┌─────────────┴─────────────┐
             │                           │
             ▼                           ▼
        GoRest.co.in              Dummy REST API
             │                           │
             └─────────────┬─────────────┘
                           ▼
                    TestNG Execution
                           │
                           ▼
                    Allure Reporting
                           │
                           ▼
                     CI / GitHub
```

---

## Why This Project Exists

API automation becomes difficult to maintain when every service has its own request construction, configuration, assertions, logging, retry handling, and reporting implementation.

This project explores a different approach:

```text
Reusable Framework Components
            ↓
Application-Specific API Modules
            ↓
Consistent Test Execution
            ↓
Structured Validation
            ↓
Actionable Test Reporting
            ↓
CI Integration
```

The goal is to demonstrate **API automation framework engineering**, not simply HTTP request scripting.

---

## Framework Capabilities

| Engineering Area | Implementation |
|---|---|
| Language | Java 17 |
| Build System | Maven multi-module |
| API Automation | REST Assured |
| Test Framework | TestNG |
| Shared Framework | `framework-core` |
| Configuration | Module-level properties |
| Validation | Reusable assertion/validation layers |
| Retry Handling | Framework retry support |
| Logging | Framework logging utilities |
| Listeners | TestNG/listener integration |
| Authentication | API-key and Bearer-token configuration |
| Schema Validation | JSON schema resources |
| Reporting | Allure |
| Test Metadata | Epic, Feature, Story, Severity, Owner, Description |
| CI Reporting | GitHub Pages / workflow integration |

---

# Modules

The repository contains a shared framework module plus five API-specific test modules.

| Module | API Under Test | Coverage |
|---|---|---|
| `framework-core` | — | Shared automation infrastructure |
| `restful-booker` | RESTful Booker | CRUD, integration and E2E |
| `petstore` | Swagger Petstore | CRUD and E2E |
| `reqres-in` | ReqRes | CRUD and negative testing |
| `gorest-co-in` | GoRest | CRUD and negative testing |
| `dummy-restapi` | Dummy REST API | CRUD and negative testing |

This structure allows the same engineering principles to be applied across multiple APIs while keeping service-specific tests isolated.

---

# Framework Architecture

## Shared Core

The `framework-core` module acts as the reusable foundation for the API test modules.

Conceptually:

```text
                     framework-core
                           │
       ┌───────────────────┼────────────────────┐
       │                   │                    │
       ▼                   ▼                    ▼
 Request Construction   Configuration       Validation
       │                   │                    │
       ├───────────────────┼────────────────────┤
       │                   │                    │
       ▼                   ▼                    ▼
     Retry              Logging             Listeners
       │                   │                    │
       └───────────────────┼────────────────────┘
                           ▼
                  Application Modules
```

The purpose of this separation is to avoid duplicating framework-level behavior inside every API module.

---

# API Test Structure

Each API module follows its own test package and resource structure while consuming the shared framework capabilities.

Example:

```text
<api-module>/
│
├── src/
│   └── test/
│       ├── java/
│       │   └── <package>/
│       │       ├── asserts/
│       │       ├── base/
│       │       └── tests/
│       │
│       └── resources/
│           ├── config/
│           └── schemas/
│
├── testng.xml
├── pom.xml
├── allure-results/
└── logs/
```

The separation between:

- test implementation
- reusable base classes
- assertions
- configuration
- schemas
- execution configuration

helps keep service-specific tests easier to understand and maintain.

---

# Test Coverage

The framework demonstrates several API testing patterns.

### Functional API Testing

Validation of common REST operations including:

```text
GET
POST
PUT
PATCH
DELETE
```

where supported by the target API.

### CRUD Testing

Typical resource lifecycle:

```text
Create
  ↓
Read
  ↓
Update
  ↓
Read / Verify
  ↓
Delete
  ↓
Verify
```

### Negative Testing

The suites also include negative scenarios such as invalid requests, invalid resources, and authentication-related failures where supported by the target API.

### Integration / E2E Testing

Selected modules include workflows that validate multiple API operations as a business flow rather than treating every endpoint as an isolated test.

---

# Validation Strategy

API testing should validate more than an HTTP status code.

The framework supports structured validation around API responses.

Typical validation dimensions include:

```text
HTTP Response
      │
      ├── Status Code
      │
      ├── Response Body
      │
      ├── Response Fields
      │
      ├── Business Assertions
      │
      └── Schema / Contract Validation
```

This makes failures more meaningful than a simple:

```java
assertEquals(response.getStatusCode(), 200);
```

---

# Configuration & Authentication

Environment-specific configuration is kept outside the test implementation.

Modules that require credentials use module-level configuration files such as:

```text
src/test/resources/config/dev.properties
```

Examples include:

### ReqRes

```properties
reqres.api.key=your_api_key
```

### GoRest

```properties
gorest.auth.token=Bearer your_token
```

> Never commit real API keys, bearer tokens, passwords, or other secrets to the repository.

For CI execution, credentials should be supplied through the CI platform's secret-management mechanism rather than committed configuration files.

---

# Schema Validation

Where applicable, JSON schemas are maintained separately from Java test code.

Example:

```text
src/test/resources/
└── schemas/
    └── user-schema.json
```

This keeps API contract definitions separate from test implementation and allows response structure to be validated independently.

---

# Reporting

The project integrates **Allure** for test reporting.

The reporting layer captures useful execution information such as:

- Request / response attachments
- Failure information
- Stack traces
- API snapshots
- Test metadata
- Epic
- Feature
- Story
- Severity
- Owner
- Description

The result is intended to make a failed API test easier to investigate rather than simply indicating that a test failed.

---

## Allure Report

A generated Allure report is published through GitHub Pages.

**Latest report:**

[View Allure Report](https://prasad291024.github.io/api-automation-hub/)

The workflow responsible for publishing the report is:

```text
.github/workflows/allure-report.yml
```

A sample report screenshot is available at:

```text
docs/images/allure-report.png
```

---

# Test Execution

## Run the complete test suite

```bash
mvn clean test
```

This executes the Maven multi-module test configuration.

---

## Run a specific API module

### RESTful Booker

```bash
mvn -pl restful-booker -am clean test
```

### Petstore

```bash
mvn -pl petstore -am clean test
```

### ReqRes

```bash
mvn -pl reqres-in -am clean test
```

### GoRest

```bash
mvn -pl gorest-co-in -am clean test
```

### Dummy REST API

```bash
mvn -pl dummy-restapi -am clean test
```

The `-am` option also builds required Maven modules, including shared dependencies such as `framework-core`.

---

# Local Allure Report

After executing the tests:

```bash
mvn clean test
```

Generate the Allure report:

```bash
npx --yes allure-commandline generate allure-results --clean -o allure-report
```

Open it locally:

```bash
npx --yes allure-commandline open allure-report
```

If GNU Make is available, the repository also provides convenience targets:

```bash
make test-allure
make allure-report
make allure-open
```

---

# CI/CD

The framework is designed to support repeatable automated execution in CI.

The CI flow can be represented as:

```text
        Source Change
             │
             ▼
       Build Project
             │
             ▼
       Run API Tests
             │
             ▼
      Collect Results
             │
             ▼
       Generate Allure
             │
             ▼
       Publish Report
```

The repository includes GitHub workflow configuration for Allure report publication.

This provides a persistent test-reporting surface that can be inspected after automated execution.

---

# Project Structure

At a high level:

```text
api-automation-hub/
│
├── .github/
│   └── workflows/
│
├── docs/
│   └── images/
│
├── framework-core/
│   └── src/
│
├── restful-booker/
│   ├── src/
│   ├── pom.xml
│   └── testng.xml
│
├── petstore/
│   ├── src/
│   ├── pom.xml
│   └── testng.xml
│
├── reqres-in/
│   ├── src/
│   ├── pom.xml
│   └── testng.xml
│
├── gorest-co-in/
│   ├── src/
│   ├── pom.xml
│   └── testng.xml
│
├── dummy-restapi/
│   ├── src/
│   ├── pom.xml
│   └── testng.xml
│
├── pom.xml
├── Makefile
└── README.md
```

---

# Design Principles

The project is organized around several practical automation-engineering principles.

### 1. Reuse Before Duplication

Common framework behavior belongs in `framework-core` rather than being recreated in every API module.

### 2. Separation of Concerns

API-specific tests should focus on:

```text
Scenario
   ↓
Request
   ↓
Response
   ↓
Assertions
```

while framework infrastructure handles cross-cutting concerns such as configuration, logging, retries, and reporting.

### 3. Configuration Outside Test Logic

Environment-specific values should not be hardcoded into test implementations.

### 4. Actionable Reporting

A test report should help answer:

```text
What failed?
Where did it fail?
What request was executed?
What response was received?
What assertion failed?
```

### 5. Independent Module Execution

Each API module can be executed independently while still consuming the shared framework.

### 6. CI Repeatability

The same Maven-based test structure should be executable locally and through CI.

---

# Engineering Focus

This repository demonstrates API automation beyond individual endpoint checks.

The engineering focus is:

```text
API Testing
     ↓
Reusable Test Components
     ↓
Multi-Module Architecture
     ↓
Configuration & Authentication
     ↓
Validation & Contract Checks
     ↓
Retry / Logging / Listeners
     ↓
Allure Diagnostics
     ↓
CI Execution
```

This makes the repository useful as a practical example of how API automation can evolve from test scripts into a maintainable automation framework.

---

# Security Considerations

Do not commit:

```text
API Keys
Bearer Tokens
Passwords
Private Credentials
Environment Secrets
```

Use environment-specific configuration locally and CI secret management for automated execution.

Before publishing additional API modules or examples, verify that generated reports, logs, and attachments do not contain credentials or sensitive request/response data.

---

# Repository Hygiene

Generated artifacts should generally not be treated as source code.

Examples include:

```text
target/
allure-results/
allure-report/
logs/
```

Where these directories are generated during execution, they should be reviewed against `.gitignore` and CI artifact requirements rather than being committed indiscriminately.

The repository contains generated Allure/report artifacts in multiple module directories, so this is an area worth cleaning as part of the repository-maintenance pass.

---

# Future Improvements

Potential engineering extensions include:

- Environment profiles such as `dev`, `qa`, and `staging`
- Centralized request specification builders
- Stronger contract/schema validation across modules
- Parallel module execution where test-data isolation permits
- Containerized CI execution
- Automated dependency/security checks
- API test result dashboards
- Expanded negative and boundary-value coverage
- OpenAPI-driven contract validation
- Test-data lifecycle management
- Improved secret injection through CI environments

These are intentionally presented as **future improvements**, not existing capabilities.

---

# Technology Stack

```text
Java 17
   │
   ├── Maven
   │
   ├── REST Assured
   │
   ├── TestNG
   │
   ├── AssertJ
   │
   ├── Awaitility
   │
   ├── AspectJ
   │
   └── Allure
```

---

# Author

**Prasad**  
SDET | QA Engineer

GitHub: [@prasad291024](https://github.com/prasad291024)

---

## Repository

[api-automation-hub](https://github.com/prasad291024/api-automation-hub)
