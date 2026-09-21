package com.prasad_v.dummy.clients;

import com.prasad_v.dummy.models.Employee;
import com.prasad_v.dummy.services.DummyRestService;
import io.restassured.response.Response;

/**
 * Client layer abstraction for Dummy REST API.
 * Encapsulates service calls and typed employee operations.
 */
public class DummyRestClient {

    private final DummyRestService dummyRestService;

    public DummyRestClient(DummyRestService dummyRestService) {
        this.dummyRestService = dummyRestService;
    }

    public DummyRestClient() {
        this(new DummyRestService());
    }

    public Response getAllEmployeesResponse() {
        return dummyRestService.getAllEmployees();
    }

    public Response getEmployeeResponse(int id) {
        return dummyRestService.getEmployeeById(id);
    }

    public Response createEmployeeResponse(Employee employee) {
        return dummyRestService.createEmployee(employee);
    }

    public Response createEmployeeResponse(Object payload) {
        return dummyRestService.createEmployee(payload);
    }

    public Response updateEmployeeResponse(int id, Employee employee) {
        return dummyRestService.updateEmployee(id, employee);
    }

    public Response updateEmployeeResponse(int id, Object payload) {
        return dummyRestService.updateEmployee(id, payload);
    }

    public Response deleteEmployeeResponse(int id) {
        return dummyRestService.deleteEmployee(id);
    }

    public DummyRestService getService() {
        return dummyRestService;
    }
}
