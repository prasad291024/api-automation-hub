package com.prasad_v.dummy.tests;

import com.prasad_v.dummy.base.BaseDummyTest;
import com.prasad_v.dummy.services.DummyRestService;
import com.prasad_v.validation.SchemaValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

@Epic("Dummy REST API")
@Feature("Employee Management")
@Owner("Prasad")
public class DummyRestTest extends BaseDummyTest {

    private DummyRestService dummyRestService;
    private String createdEmployeeId;

    @BeforeClass
    public void setup() {
        dummyRestService = new DummyRestService();
    }

    // ── GET ALL ──────────────────────────────────────────────────────────────

    @Test(description = "Verify all employees retrieval and schema validation")
    @Story("Get Employees")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Fetch all employees, verify success status and validate response against JSON schema")
    public void testGetAllEmployees() {
        Response response = dummyRestService.getAllEmployees();
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "success");
        SchemaValidator.assertSchema(response, "employees-schema.json");
    }

    // ── GET BY ID ────────────────────────────────────────────────────────────

    @Test(description = "Verify single employee retrieval by ID")
    @Story("Get Employees")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch employee by ID 1 and verify name and salary fields are present")
    public void testGetEmployeeById() {
        Response response = dummyRestService.getEmployeeById(1);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "success");
        Assert.assertNotNull(response.jsonPath().get("data.employee_name"));
        Assert.assertNotNull(response.jsonPath().get("data.employee_salary"));
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify employee creation returns 200 with created data")
    @Story("Create Employee")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new employee and verify the response contains the created name and ID")
    public void testCreateEmployee() {
        Map<String, Object> payload = Map.of(
                "name", "John Doe",
                "salary", "50000",
                "age", "30"
        );
        Response response = dummyRestService.createEmployee(payload);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "success");
        Assert.assertEquals(response.jsonPath().getString("data.name"), "John Doe");
        createdEmployeeId = response.jsonPath().getString("data.id");
        Assert.assertNotNull(createdEmployeeId);
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify employee update returns 200", dependsOnMethods = "testCreateEmployee")
    @Story("Update Employee")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update salary of created employee and verify updated name in response")
    public void testUpdateEmployee() {
        Map<String, Object> payload = Map.of(
                "name", "John Doe Updated",
                "salary", "60000",
                "age", "31"
        );
        Response response = dummyRestService.updateEmployee(Integer.parseInt(createdEmployeeId), payload);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "success");
        Assert.assertEquals(response.jsonPath().getString("data.name"), "John Doe Updated");
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test(description = "Verify employee deletion returns 200", dependsOnMethods = "testUpdateEmployee")
    @Story("Delete Employee")
    @Severity(SeverityLevel.NORMAL)
    @Description("Delete the created employee and verify success status in response")
    public void testDeleteEmployee() {
        Response response = dummyRestService.deleteEmployee(Integer.parseInt(createdEmployeeId));
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "success");
    }

    // ── NEGATIVE ─────────────────────────────────────────────────────────────

    @Test(description = "Verify get employee with invalid ID returns 404")
    @Story("Get Employees")
    @Severity(SeverityLevel.NORMAL)
    @Description("Fetch employee with non-existent ID and verify 404 response")
    public void testGetEmployeeByInvalidId() {
        Response response = dummyRestService.getEmployeeById(999999);
        Assert.assertEquals(response.getStatusCode(), 404);
    }
}
