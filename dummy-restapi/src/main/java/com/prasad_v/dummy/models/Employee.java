package com.prasad_v.dummy.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {
    private String id;
    private String name;
    private String salary;
    private String age;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_salary")
    private String employeeSalary;

    @JsonProperty("employee_age")
    private String employeeAge;

    @JsonProperty("profile_image")
    private String profileImage;

    public Employee() {
    }

    public Employee(String id, String name, String salary, String age) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.employeeName = name;
        this.employeeSalary = salary;
        this.employeeAge = age;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name != null ? name : employeeName; }
    public void setName(String name) { this.name = name; }

    public String getSalary() { return salary != null ? salary : employeeSalary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getAge() { return age != null ? age : employeeAge; }
    public void setAge(String age) { this.age = age; }

    public String getEmployeeName() { return employeeName != null ? employeeName : name; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeSalary() { return employeeSalary != null ? employeeSalary : salary; }
    public void setEmployeeSalary(String employeeSalary) { this.employeeSalary = employeeSalary; }

    public String getEmployeeAge() { return employeeAge != null ? employeeAge : age; }
    public void setEmployeeAge(String employeeAge) { this.employeeAge = employeeAge; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String salary;
        private String age;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder salary(String salary) { this.salary = salary; return this; }
        public Builder age(String age) { this.age = age; return this; }

        public Employee build() {
            return new Employee(id, name, salary, age);
        }
    }
}
