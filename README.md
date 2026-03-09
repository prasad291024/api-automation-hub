# api-automation-hub

> A Maven multi-module API test automation framework built with Java 17, REST Assured, TestNG, and Allure Reporting.

---

## 📌 Project Overview

`api-automation-hub` is a multi-module Maven project that brings together two independent API test projects under a single shared engine (`framework-core`). It promotes code reuse, consistent reporting, and centralised dependency management across all test modules.

### Modules

| Module | Description |
|---|---|
| `framework-core` | Shared engine JAR — RestAssured, Allure, Logging, Retry, Auth, Validation |
| `restful-booker` | API tests for the Restful Booker booking API (CRUD + E2E flows) |
| `petstore` | API tests for the Swagger Petstore API (CRUD + E2E flows) |

---

## 🏗️ Architecture

```
api-automation-hub/                  ← Parent POM (packaging=pom)
├── framework-core/                  ← Shared engine (JAR)
│   └── src/main/java/com/prasad_v/
│       ├── annotations/             ← Custom annotations
│       ├── auth/                    ← Auth handlers (Basic, OAuth, Token)
│       ├── config/                  ← ConfigurationManager, EnvironmentManager
│       ├── constants/               ← API and config constants
│       ├── contracts/               ← Contract validation
│       ├── enums/                   ← RequestType enums
│       ├── exceptions/              ← Custom exceptions
│       ├── interceptors/            ← Request/response interceptors
│       ├── listeners/               ← TestNG execution listeners
│       ├── logging/                 ← CustomLogger, LogManager, LogSanitizer
│       ├── mock/                    ← MockServer manager
│       ├── reporting/               ← Allure & Extent report managers
│       ├── requestbuilder/          ← RequestBuilder, HeaderManager
│       ├── retry/                   ← RetryAnalyzer, RetryListener
│       ├── services/                ← BaseApiService
│       ├── testdata/                ← Excel & JSON data providers
│       ├── utils/                   ← RestUtils, DateUtils, AllureManager
│       └── validation/              ← Schema, JSON path, response validators
│
├── restful-booker/                  ← Booking API test module
│   └── src/
│       ├── main/java/com/prasad_v/
│       │   ├── builders/            ← BookingBuilder
│       │   ├── modules/             ← PayloadManager
│       │   ├── pojos/               ← Booking, Auth, BookingResponse POJOs
│       │   ├── services/            ← BookingService, UserService
│       │   └── utils/               ← TestDataProvider
│       └── test/java/com/prasad_v/
│           ├── asserts/             ← AssertActions
│           └── tests/               ← CRUD + E2E + Integration tests
│
└── petstore/                        ← Petstore API test module
    └── src/
        ├── main/java/com/petstore/api/
        │   ├── config/              ← Config (base URL, endpoints, status codes)
        │   ├── models/              ← Pet, Category, Tag POJOs
        │   └── services/            ← PetService
        └── test/java/com/petstore/api/
            ├── base/                ← BaseTest (ConfigurationManager integrated)
            ├── dataproviders/       ← TestDataProvider
            └── tests/               ← CreatePet, GetPet, UpdatePet, DeletePet, E2E tests
```

---

## ⚙️ Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Maven | Multi-Module |
| REST Assured | 5.5.5 |
| TestNG | 7.11.0 |
| Allure Reporting | 2.29.1 |
| Lombok | 1.18.38 |
| Jackson | 2.19.0 |
| Log4j2 | 2.24.3 |
| AssertJ | 3.27.3 |
| AspectJ Weaver | 1.9.22.1 |

---

## 🛠️ Prerequisites

Before running the project, ensure you have:

- **JDK 17+** installed (project tested with JDK 25)
- **Maven 3.8+** installed
- **IntelliJ IDEA** (recommended) with Lombok plugin enabled
- **Allure CLI** installed (for generating reports locally)
  - Install via: `npm install -g allure-commandline` or `brew install allure`
- **Internet access** to reach the Restful Booker and Petstore APIs

### Verify your setup

```bash
java -version
mvn -version
allure --version
```

---

## 🚀 Setup & Installation

### 1. Clone the repository

```bash
git clone https://github.com/prasad291024/api-automation-hub.git
cd api-automation-hub
```

### 2. Build all modules

```bash
mvn clean install -DskipTests
```

This will:
- Build `framework-core` and install it as a JAR in your local Maven repository
- Compile `restful-booker` and `petstore` modules

### 3. Enable Annotation Processing in IntelliJ

Go to **File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors** and ensure **Enable annotation processing** is checked ✅

---

## ▶️ How to Run Tests

### Run all modules together

```bash
mvn clean test
```

### Run a specific module only

```bash
# Restful Booker tests only
mvn test -pl restful-booker

# Petstore tests only
mvn test -pl petstore
```

### Run a specific TestNG suite

```bash
# Run E2E tests for restful-booker
mvn test -pl restful-booker -Dsurefire.suiteXmlFiles=testng_E2E.xml

# Run regression suite
mvn test -pl restful-booker -Dsurefire.suiteXmlFiles=testng_reg.xml

# Run parallel tests
mvn test -pl restful-booker -Dsurefire.suiteXmlFiles=testng_parallel.xml
```

### Build framework-core only (reinstall JAR)

```bash
mvn clean install -pl framework-core -DskipTests
```

---

## 📊 How to Generate Allure Reports

### Step 1 — Run tests (generates allure-results)

```bash
mvn clean test -pl restful-booker
# or
mvn clean test -pl petstore
```

### Step 2 — Generate and open the report

```bash
# For restful-booker
allure serve restful-booker/target/allure-results

# For petstore
allure serve petstore/target/allure-results
```

### Step 3 — Generate static report (optional)

```bash
allure generate restful-booker/target/allure-results -o restful-booker/target/allure-report --clean
allure open restful-booker/target/allure-report
```

---

## 🔧 Configuration

### Restful Booker environments

Config files are located at:
`restful-booker/src/test/resources/config/`

| File | Environment |
|---|---|
| `dev.properties` | Development |
| `qa.properties` | QA |
| `prod.properties` | Production |

### Petstore configuration

The base URL is managed via:
`petstore/src/test/resources/config/petstore.properties`

```properties
base.url=https://petstore.swagger.io/v2
```

---

## 👤 Author

**Prasad** — SDET | API Automation Engineer

🔗 GitHub: [prasad291024](https://github.com/prasad291024)

---

## 📄 License

This project is for learning and portfolio purposes.
