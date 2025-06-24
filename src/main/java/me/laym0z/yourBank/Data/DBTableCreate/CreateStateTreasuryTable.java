package me.laym0z.yourBank.Data.DBTableCreate;

import java.sql.*;

public class CreateStateTreasuryTable {
    public static void Create(String path) {
        try (Connection conn = DriverManager.getConnection(path)) {
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
        } catch (SQLException error) {
            System.out.println(error.getMessage());
        }
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS StateTreasury (
                    id integer PRIMARY KEY,
                    diamonds INT
                );""";
        String insertDefaultSQL = """
                INSERT OR IGNORE INTO StateTreasury (id, diamonds) VALUES (1, 0);
                """;

        try (Connection conn = DriverManager.getConnection(path);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            stmt.executeUpdate(insertDefaultSQL);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

//
//        try (Connection conn = DriverManager.getConnection(path);
//             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
//            stmt.execute();
//            System.out.println("Table has been created.");
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        try (Connection conn = DriverManager.getConnection(path);
//             PreparedStatement stmt = conn.prepareStatement(insertDefaultSQL)) {
//            stmt.execute();
//        }
//        catch (SQLException error) {
//            System.out.println(error.getMessage());
//        }
    }
}
