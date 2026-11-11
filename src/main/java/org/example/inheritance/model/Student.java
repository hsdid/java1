package org.example.inheritance.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@NoArgsConstructor
public class Student extends Person {

    @Column(name = "std_group")
    private String group;
    private int scholarship;

    public Student(String name, int age, String group, int scholarship) {
        super(name, age);
        this.group = group;
        this.scholarship = scholarship;
    }

}
