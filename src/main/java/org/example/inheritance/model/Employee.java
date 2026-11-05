package org.example.inheritance.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Employee extends Person {

    private String position;
    private int salary;

    public Employee(String name, int age, String position, int salary) {
        super(name, age);
        this.position = position;
        this.salary = salary;
    }
}
