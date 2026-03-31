package com.prasad_v.dummy.services;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.enums.RequestType;
import com.prasad_v.services.BaseApiService;
import io.restassured.response.Response;

public class DummyRestService extends BaseApiService {

    private final String baseUrl;
    private final String employeesEndpoint;
    private final String employeeEndpoint;
    private final String createEndpoint;
    private final String updateEndpoint;
    private final String deleteEndpoint;

    public DummyRestService() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        this.baseUrl = config.getProperty("dummy.base.url", "https://dummy.restapiexample.com/api/v1");
        this.employeesEndpoint = config.getProperty("dummy.employees.endpoint", "/employees");
        this.employeeEndpoint = config.getProperty("dummy.employee.endpoint", "/employee");
        this.createEndpoint = config.getProperty("dummy.create.endpoint", "/create");
        this.updateEndpoint = config.getProperty("dummy.update.endpoint", "/update");
        this.deleteEndpoint = config.getProperty("dummy.delete.endpoint", "/delete");
    }

    public Response getAllEmployees() {
        String path = baseUrl + employeesEndpoint;
        return execute(RequestType.GET, path, null, null);
    }

    public Response getEmployeeById(int id) {
        String path = baseUrl + employeeEndpoint + "/" + id;
        return execute(RequestType.GET, path, null, null);
    }

    public Response createEmployee(Object payload) {
        String path = baseUrl + createEndpoint;
        return execute(RequestType.POST, path, null, payload);
    }

    public Response updateEmployee(int id, Object payload) {
        String path = baseUrl + updateEndpoint + "/" + id;
        return execute(RequestType.PUT, path, null, payload);
    }

    public Response deleteEmployee(int id) {
        String path = baseUrl + deleteEndpoint + "/" + id;
        return execute(RequestType.DELETE, path, null, null);
    }
}
