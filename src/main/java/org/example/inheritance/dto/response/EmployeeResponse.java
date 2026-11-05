package org.example.inheritance.dto.response;

import lombok.Getter;

@Getter
public class EmployeeResponse extends PersonResponse {

    private String position;
    private int salary;

    public EmployeeResponse(int id, String name, int age, String position, int salary) {
        super(id, name, age);
        this.position = position;
        this.salary = salary;
    }
}
