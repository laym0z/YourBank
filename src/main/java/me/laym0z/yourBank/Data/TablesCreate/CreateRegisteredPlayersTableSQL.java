package me.laym0z.yourBank.Data.TablesCreate;

import me.laym0z.yourBank.Data.DB.MySQL;
import me.laym0z.yourBank.YourBank;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateRegisteredPlayersTableSQL {
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
                CREATE TABLE IF NOT EXISTS registered_players (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    uuid VARCHAR(255) NOT NULL UNIQUE,
                    name VARCHAR(255),
                    join_date DATE
                );""";
        }
        else {
            createTableSQL = """
                CREATE TABLE IF NOT EXISTS registered_players (
                    id integer PRIMARY KEY,
                    uuid TEXT NOT NULL UNIQUE,
                    name TEXT,
                    join_date TEXT
                );""";
        }
        return createTableSQL;
    }
}
