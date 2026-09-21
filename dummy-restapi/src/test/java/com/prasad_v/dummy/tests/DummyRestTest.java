package com.prasad_v.dummy.tests;

import com.prasad_v.dummy.asserts.DummyRestAssertions;
import com.prasad_v.dummy.base.BaseDummyTest;
import com.prasad_v.dummy.clients.DummyRestClient;
import com.prasad_v.dummy.models.Employee;
import com.prasad_v.utils.DataGenerator;
import com.prasad_v.validation.SchemaValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("Dummy REST API")
@Feature("Employee Management")
@Owner("Prasad")
public class DummyRestTest extends BaseDummyTest {

    private DummyRestClient dummyRestClient;
    private String createdEmployeeId;
    private String initialEmployeeName;
    private String updatedEmployeeName;
    private String initialSalary;
    private String updatedSalary;
    private String employeeAge;

    @BeforeClass
    public void setup() {
        dummyRestClient = new DummyRestClient();
        initialEmployeeName = DataGenerator.generateRandomFirstName() + " " + DataGenerator.generateRandomLastName();
        updatedEmployeeName = "Updated " + DataGenerator.generateRandomFirstName();
        initialSalary = String.valueOf(DataGenerator.generatePrice(40000, 80000));
        updatedSalary = String.valueOf(DataGenerator.generatePrice(85000, 120000));
        employeeAge = String.valueOf(DataGenerator.generateRandomInt(22, 58));
    }

    // ── GET ALL ──────────────────────────────────────────────────────────────

    @Test(description = "Verify all employees retrieval and schema validation")
    @Story("Get Employees")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Fetch all employees, verify success status and validate response against JSON schema")
    public void testGetAllEmployees() {
        Response response = executeWithRetry(() -> dummyRestClient.getAllEmployeesResponse());
        DummyRestAssertions.assertStatusCode(response, 200);
        DummyRestAssertions.assertResponseStatus(response, "success");
        SchemaValidator.assertSchema(response, "employees-schema.json");
    }

    // ── GET BY ID ────────────────────────────────────────────────────────────

    @Test(description = "Verify single employee retrieval by ID")
    @Story("Get Employees")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch employee by ID 1 and verify name and salary fields are present")
    public void testGetEmployeeById() {
        Response response = executeWithRetry(() -> dummyRestClient.getEmployeeResponse(1));
        DummyRestAssertions.assertStatusCode(response, 200);
        DummyRestAssertions.assertResponseStatus(response, "success");
        Assert.assertNotNull(response.jsonPath().get("data.employee_name"));
        Assert.assertNotNull(response.jsonPath().get("data.employee_salary"));
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify employee creation returns 200 with created data")
    @Story("Create Employee")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new employee with dynamic data and verify the response contains created name and ID")
    public void testCreateEmployee() {
        Employee employee = Employee.builder()
                .name(initialEmployeeName)
                .salary(initialSalary)
                .age(employeeAge)
                .build();

        Response response = executeWithRetry(() -> dummyRestClient.createEmployeeResponse(employee));
        DummyRestAssertions.assertEmployeeCreated(response, initialEmployeeName);
        createdEmployeeId = response.jsonPath().getString("data.id");
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify employee update returns 200", dependsOnMethods = "testCreateEmployee")
    @Story("Update Employee")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update salary and name of created employee and verify updated name in response")
    public void testUpdateEmployee() {
        Employee updateRequest = Employee.builder()
                .name(updatedEmployeeName)
                .salary(updatedSalary)
                .age(employeeAge)
                .build();

        Response response = executeWithRetry(() -> dummyRestClient.updateEmployeeResponse(Integer.parseInt(createdEmployeeId), updateRequest));
        DummyRestAssertions.assertEmployeeUpdated(response, updatedEmployeeName);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify employee deletion returns 200", dependsOnMethods = "testUpdateEmployee")
    @Story("Delete Employee")
    @Severity(SeverityLevel.NORMAL)
    @Description("Delete the created employee and verify success status in response")
    public void testDeleteEmployee() {
        Response response = executeWithRetry(() -> dummyRestClient.deleteEmployeeResponse(Integer.parseInt(createdEmployeeId)));
        DummyRestAssertions.assertEmployeeDeleted(response);
    }

    // ── NEGATIVE ─────────────────────────────────────────────────────────────

    @Test(description = "Verify get employee with invalid ID returns 404 or null data")
    @Story("Get Employees")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch employee with non-existent ID and verify 404 or null data response")
    public void testGetEmployeeByInvalidId() {
        Response response = executeWithRetry(() -> dummyRestClient.getEmployeeResponse(999999));
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 404 || 
                (statusCode == 200 && (response.jsonPath().get("data") == null || "null".equals(response.jsonPath().getString("data")))),
                "Expected 404 or 200 with null data, got status: " + statusCode);
    }
}
