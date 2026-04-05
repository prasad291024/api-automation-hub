package com.prasad_v.dummy.tests;

import com.prasad_v.dummy.base.BaseDummyTest;
import com.prasad_v.dummy.services.DummyRestService;
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

    private DummyRestService dummyRestService;

    @BeforeClass
    public void setup() {
        dummyRestService = new DummyRestService();
    }

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
}
