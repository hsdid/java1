package org.example.inheritance.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Teacher extends Person {
    private String specialization;
    private int salary;

    public Teacher(String name, int age, String specialization, int salary) {
        super(name, age);
        this.specialization = specialization;
        this.salary = salary;
    }
}
