package me.laym0z.yourBank.Data.TablesCreate;

import me.laym0z.yourBank.Data.DB.MySQL;
import me.laym0z.yourBank.YourBank;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateBankTableSQL {
    public static void Create() {
        try (Connection conn = YourBank.getDatabaseConnector().getConnection()) {
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
        } catch (SQLException error) {
            System.out.println(error.getMessage());
        }
        String createTableSQL = getString();

        try (Connection conn = YourBank.getDatabaseConnector().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table has been created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @NotNull
    private static String getString() {
        String createTableSQL;
        if (YourBank.getDatabaseConnector() instanceof MySQL) {
                createTableSQL = """
                CREATE TABLE IF NOT EXISTS Bank (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL UNIQUE,
                    diamonds INT,
                    create_date DATE,
                    is_blocked BOOLEAN NOT NULL DEFAULT FALSE
                );""";
        }
        else {
            createTableSQL = """
                CREATE TABLE IF NOT EXISTS Bank (
                    id integer PRIMARY KEY,
                    name TEXT NOT NULL UNIQUE,
                    diamonds INT,
                    create_date TEXT,
                    is_blocked BOOLEAN NOT NULL DEFAULT 0
                );""";
        }
        return createTableSQL;
    }
}
