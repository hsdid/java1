package org.example.inheritance.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "person_type")
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_Id_generator")
    @SequenceGenerator(name = "person_Id_generator", sequenceName = "person_id_seq", initialValue = 1, allocationSize = 100)
    private int id;
    private String name;
    private int age;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private LocalDate birthDate;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

}
