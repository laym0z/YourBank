package me.laym0z.yourBank.Data.DBTableCreate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateBankTable {
    public static void Create(String path) {
        try (Connection conn = DriverManager.getConnection(path)) {
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
        } catch (SQLException error) {
            System.out.println(error.getMessage());
        }
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS Bank (
                    id integer PRIMARY KEY,
                    name TEXT NOT NULL UNIQUE,
                    diamonds INT,
                    create_date TEXT,
                    is_blocked BOOLEAN NOT NULL DEFAULT 0
                );""";
        try (Connection conn = DriverManager.getConnection(path);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table has been created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
