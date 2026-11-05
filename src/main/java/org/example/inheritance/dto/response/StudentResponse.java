package org.example.inheritance.dto.response;

import lombok.Getter;

@Getter
public class StudentResponse extends PersonResponse {

    private String group;
    private int scholarship;

    public StudentResponse(int id, String name, int age, String group, int scholarship) {
        super(id, name, age);
        this.group = group;
        this.scholarship = scholarship;
    }
}
