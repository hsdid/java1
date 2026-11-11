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

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;

@Slf4j
public class PeselToBirthDateAndGenderChange implements CustomTaskChange {

    @Override
    public void execute(Database database) throws CustomChangeException {
        Connection connection = ((JdbcConnection) database.getConnection()).getUnderlyingConnection();
        int updated = migratePersonTable(connection);
        log.info("PESEL -> birth_date + gender: updated {} rows", updated);
    }

    private int migratePersonTable(Connection connection) throws CustomChangeException {
        String selectSql = "select id, pesel from person where pesel is not null";
        String updateSql = "update person set birth_date = ?, gender = ? where id = ?";

        int updated = 0;
        try (PreparedStatement select = connection.prepareStatement(selectSql);
             PreparedStatement update = connection.prepareStatement(updateSql);
             ResultSet rs = select.executeQuery()) {

            while (rs.next()) {
                String pesel = rs.getString("pesel");
                if (pesel == null || pesel.isBlank()) {
                    continue;
                }

                PeselData peselData = parsePesel(pesel.trim());

                update.setDate(1, Date.valueOf(peselData.birthDate()));
                update.setString(2, peselData.gender());
                update.setInt(3, rs.getInt("id"));

                update.executeUpdate();

                updated++;
            }
        } catch (SQLException | IllegalArgumentException e) {
            throw new CustomChangeException("Failed to migrate PESEL in person table", e);
        }
        return updated;
    }

    private PeselData parsePesel(String pesel) {
        if (pesel.length() != 11 || !pesel.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("Invalid PESEL (length/characters): " + pesel);
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
            throw new IllegalArgumentException("Invalid month in PESEL: " + pesel);
        }

        LocalDate birthDate;
        try {
            birthDate = LocalDate.of(century + year, month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid date in PESEL: " + pesel, e);
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
