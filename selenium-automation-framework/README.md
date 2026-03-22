# LinkedIn Job Search Automation Framework

## Stack
- **Java 11** — language
- **Selenium 4** — browser automation (Chrome / Firefox)
- **TestNG** — test runner and assertions
- **ExtentReports 5** — HTML execution reports
- **Apache POI** — read test data from Excel
- **REST Assured** — LinkedIn API validation
- **WebDriverManager** — auto-downloads browser drivers (no manual driver setup)
- **Maven** — build and dependency management

---

## Project Structure

```
linkedin-automation/
├── pom.xml                                   ← Maven dependencies
├── testng.xml                                ← Test suite config
├── create_test_data.py                       ← Script to generate Excel test data
│
├── src/
│   ├── main/java/com/linkedin/
│   │   ├── config/
│   │   │   └── ConfigReader.java             ← Reads config.properties
│   │   ├── base/
│   │   │   └── BaseTest.java                 ← Driver init, report hooks, screenshots
│   │   ├── pages/                            ← POM page classes (XPath only)
│   │   │   ├── LinkedInLoginPage.java
│   │   │   ├── LinkedInHomePage.java
│   │   │   └── LinkedInJobsPage.java
│   │   └── utils/
│   │       ├── ExcelReader.java              ← Apache POI Excel data reader
│   │       ├── ExtentReportManager.java      ← ExtentReports lifecycle manager
│   │       └── LinkedInApiClient.java        ← REST Assured API client
│   │
│   └── test/
│       ├── java/com/linkedin/tests/
│       │   └── LinkedInJobSearchTest.java    ← Main test class (7 TCs)
│       └── resources/
│           ├── config.properties             ← All configuration values
│           └── TestData.xlsx                 ← Excel test data (see below)
│
└── reports/
    ├── ExtentReport.html                     ← Generated after test run
    └── screenshots/                          ← Auto-captured on failures
```

---

## Setup

### 1. Prerequisites
- Java 11+
- Maven 3.8+
- Chrome or Firefox browser installed

### 2. Generate Test Data Excel
```bash
pip install openpyxl
python create_test_data.py
```

### 3. Update config.properties
Edit `src/test/resources/config.properties`:
```properties
username=your_linkedin_email@example.com
password=your_linkedin_password
api.token=your_linkedin_api_token
```

### 4. Run Tests
```bash
# Run all tests
mvn clean test

# Run with headless Chrome
# Set headless=true in config.properties, then:
mvn clean test

# Run specific test method
mvn clean test -Dtest=LinkedInJobSearchTest#tc001_loginWithValidCredentials
```

### 5. View Report
Open `reports/ExtentReport.html` in any browser after the run.

---

## Test Cases

| ID     | Name                                   | Type |
|--------|----------------------------------------|------|
| TC001  | Login with valid credentials           | UI   |
| TC002  | Navigate to Jobs page                  | UI   |
| TC003  | Data-driven job search (from Excel)    | UI   |
| TC004  | Verify job result cards                | UI   |
| TC005  | Click first job card                   | UI   |
| TC006  | API reachability check                 | API  |
| TC007  | Job search API returns HTTP 200        | API  |

---

## XPath-Only Locator Strategy

All locators in the Page Objects use **XPath only** — no CSS selectors, no IDs directly.

Example pattern used throughout:
```java
// Simple attribute XPath
"//input[@id='username']"

// Text-based
"//button[normalize-space()='Sign in']"

// Partial class match
"//a[contains(@class,'job-card-list__title')]"

// Ancestor/descendant navigation
"//ul[contains(@class,'results-list')]//li[contains(@class,'list-item')]"

// Dynamic XPath with String.format
String.format("//label[normalize-space()='%s']", experienceLevel)
```

---

## Notes

- LinkedIn uses dynamic class names that change over time — XPaths using `contains()` and `normalize-space()` are more resilient
- LinkedIn may detect Selenium automation and block login in headless mode; prefer `headless=false` for stable runs
- The API tests use the LinkedIn REST API v2 — you need a valid OAuth token from the LinkedIn Developer Portal
