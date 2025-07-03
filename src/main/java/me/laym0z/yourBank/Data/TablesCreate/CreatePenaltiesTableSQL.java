package me.laym0z.yourBank.Data.TablesCreate;

import me.laym0z.yourBank.Data.DB.MySQL;
import me.laym0z.yourBank.YourBank;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreatePenaltiesTableSQL {
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
                CREATE TABLE IF NOT EXISTS Penalties (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    amount INT,
                    reason VARCHAR(255),
                    creation_date DATE,
                    payment_term DATE,
                    receiver VARCHAR(255)
                );""";
        }
        else {
            createTableSQL = """
                CREATE TABLE IF NOT EXISTS Penalties (
                    id integer PRIMARY KEY,
                    name TEXT NOT NULL,
                    amount INT,
                    reason TEXT,
                    creation_date TEXT,
                    payment_term TEXT,
                    receiver TEXT
                );""";
        }
        return createTableSQL;
    }
}
