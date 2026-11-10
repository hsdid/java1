package org.example.migration;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.example.migration.dto.PeselData;

import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDate;

@Slf4j
public class PeselToBirthDateAndGenderChange implements CustomTaskChange {
    @Override
    public void execute(Database database) throws CustomChangeException {
        Connection connection = ((JdbcConnection) database.getConnection()).getUnderlyingConnection();
        int updatedStudents = migrateStudentTable(connection);
        log.info("updated: " + updatedStudents + " students");
        int updatedEmployees = migrateEmployeeTable(connection);
        log.info("updated: " + updatedEmployees + " employees");
        int updatedTeacher = migrateTeacherTable(connection);
        log.info("updated: " + updatedTeacher + " teachers");

    }

    private int migrateStudentTable(Connection connection) {
        String selectSql = "select id, pesel from student where pesel is not null";
        String updateSql = "update student set birth_date = ?, gender = ? where id = ?";

        int updated = 0;
        try (PreparedStatement select = connection.prepareStatement(selectSql);
             PreparedStatement update = connection.prepareStatement(updateSql);
             ResultSet rs = select.executeQuery();
        ) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String pesel = rs.getString("pesel");

                PeselData peselData = parsePesel(pesel);

                update.setDate(1, Date.valueOf(peselData.birthDate()));
                update.setString(2, peselData.gender());
                update.setInt(3, id);

                update.executeUpdate();

                updated++;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return updated;
    }

    private int migrateEmployeeTable(Connection connection) {
        String selectSql = "select id, pesel from employee where pesel is not null";
        String updateSql = "update employee set birth_date = ?, gender = ? where id = ?";

        int updated = 0;
        try (PreparedStatement select = connection.prepareStatement(selectSql);
             PreparedStatement update = connection.prepareStatement(updateSql);
             ResultSet rs = select.executeQuery();
        ) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String pesel = rs.getString("pesel");

                PeselData peselData = parsePesel(pesel);

                update.setDate(1, Date.valueOf(peselData.birthDate()));
                update.setString(2, peselData.gender());
                update.setInt(3, id);

                update.executeUpdate();

                updated++;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return updated;
    }

    private int migrateTeacherTable(Connection connection) {
        String selectSql = "select id, pesel from teacher where pesel is not null";
        String updateSql = "update teacher set birth_date = ?, gender = ? where id = ?";

        int updated = 0;
        try (PreparedStatement select = connection.prepareStatement(selectSql);
             PreparedStatement update = connection.prepareStatement(updateSql);
             ResultSet rs = select.executeQuery();
        ) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String pesel = rs.getString("pesel");

                PeselData peselData = parsePesel(pesel);

                update.setDate(1, Date.valueOf(peselData.birthDate()));
                update.setString(2, peselData.gender());
                update.setInt(3, id);

                update.executeUpdate();

                updated++;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return updated;
    }

    private PeselData parsePesel(String pesel) {
        if (pesel.length() != 11 || !pesel.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("Nieprawidłowy PESEL (długość/znaki): " + pesel);
        }

        int year = Integer.parseInt(pesel.substring(0, 2));
        int month = Integer.parseInt(pesel.substring(2, 4));
        int day = Integer.parseInt(pesel.substring(4, 6));
        int century;

        if (month >= 1 && month <= 12) {
            century = 1900;
        } else if (month >= 21 && month <= 32) {
            century = 2000;
            month -= 20;
        } else if (month >= 41 && month <= 52) {
            century = 2100;
            month -= 40;
        } else if (month >= 61 && month <= 72) {
            century = 2200;
            month -= 60;
        } else if (month >= 81 && month <= 92) {
            century = 1800;
            month -= 80;
        } else {
            throw new IllegalArgumentException("Nieprawidłowy miesiąc w PESEL: " + pesel);
        }

        LocalDate birthDate;
        try {
            birthDate = LocalDate.of(century + year, month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Nieprawidłowa data w PESEL: " + pesel, e);
        }

        int genderDigit = Character.digit(pesel.charAt(9), 10);
        String gender = (genderDigit % 2 == 0) ? "FEMALE" : "MALE";

        return new PeselData(birthDate, gender);
    }


    @Override
    public String getConfirmationMessage() {
        return "";
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }
}
