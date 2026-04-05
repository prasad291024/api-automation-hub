# api-automation-hub

A Maven multi-module API test automation framework built with Java 17, REST Assured, TestNG, and Allure.

## Project Overview

`api-automation-hub` combines multiple API test projects under one shared engine (`framework-core`) for reusable utilities, centralized dependencies, and consistent reporting.

### Modules

| Module | API Under Test | Coverage |
|---|---|---|
| `framework-core` | — | Shared engine: request builders, config, retry, listeners, logging, validation |
| `restful-booker` | [restful-booker.herokuapp.com](https://restful-booker.herokuapp.com) | CRUD + integration + E2E |
| `petstore` | [petstore.swagger.io](https://petstore.swagger.io) | CRUD + E2E |
| `reqres-in` | [reqres.in](https://reqres.in) | CRUD + negative tests |
| `gorest-co-in` | [gorest.co.in](https://gorest.co.in) | CRUD + negative tests |
| `dummy-restapi` | [dummy.restapiexample.com](https://dummy.restapiexample.com) | CRUD + negative tests |

## Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Maven | Multi-module |
| REST Assured | 5.5.5 |
| TestNG | 7.11.0 |
| Allure | 2.29.1 adapters + CLI |
| AspectJ Weaver | 1.9.24 |
| Awaitility | 4.3.0 |
| AssertJ | 3.27.7 |

## Allure Report Showcase

This repository includes full Allure integration:

- Request/response attachments
- Failure stack traces + API snapshots
- Test metadata (`Epic`, `Feature`, `Story`, `Severity`, `Owner`, `Description`)
- Auto-generated `environment.properties`, `categories.json`, and `executor.json`
- History/trend support in CI

Live report (GitHub Pages):

- [Latest Allure Report](https://prasad291024.github.io/api-automation-hub/)

Workflow that publishes the report:

- [.github/workflows/allure-report.yml](.github/workflows/allure-report.yml)

Sample Allure report view:

![Allure Report Screenshot](docs/images/allure-report.png)

## Prerequisites

- JDK 17+
- Maven 3.8+
- Node.js (for `npx allure-commandline` fallback)
- Internet access to hit public API endpoints

## API Keys & Tokens Setup

Some modules require authentication credentials. Add them to the module's `src/test/resources/config/dev.properties` before running.

| Module | Header | Where to get |
|---|---|---|
| `reqres-in` | `x-api-key` | [reqres.in](https://reqres.in) — free account |
| `gorest-co-in` | `Authorization: Bearer <token>` | [gorest.co.in](https://gorest.co.in) — free account |

**reqres-in** (`reqres-in/src/test/resources/config/dev.properties`):
```properties
reqres.api.key=pub_your_api_key_here
```

**gorest-co-in** (`gorest-co-in/src/test/resources/config/dev.properties`):
```properties
gorest.auth.token=Bearer your_token_here
```

## How To Run Tests

Run all modules:

```bash
mvn clean test
```

Run specific module:

```bash
mvn -pl restful-booker -am clean test
mvn -pl petstore -am clean test
mvn -pl reqres-in -am clean test
mvn -pl gorest-co-in -am clean test
mvn -pl dummy-restapi -am clean test
```

## How To Generate Allure Report (Local)

```bash
mvn clean test
npx --yes allure-commandline generate allure-results --clean -o allure-report
npx --yes allure-commandline open allure-report
```

If GNU Make is available:

```bash
make test-allure
make allure-report
make allure-open
```

## Report History and Trends

To preserve trend graphs between runs, copy previous history before generating a new report:

```bash
mkdir -p allure-results/history
cp -R allure-report/history/. allure-results/history/
```

(Handled automatically in CI workflow.)

## Author

Prasad - SDET | API Automation Engineer  
GitHub: [prasad291024](https://github.com/prasad291024)
