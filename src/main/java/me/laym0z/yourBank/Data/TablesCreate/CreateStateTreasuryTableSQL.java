package me.laym0z.yourBank.Data.TablesCreate;

import me.laym0z.yourBank.Data.DB.MySQL;
import me.laym0z.yourBank.YourBank;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class CreateStateTreasuryTableSQL {
    public static void Create() {
        try (Connection conn = YourBank.getDatabaseConnector().getConnection()) {
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
        } catch (SQLException error) {
            System.out.println(error.getMessage());
        }
        String createTableSQL = getStringCreate();
        String insertDefaultSQL = getStringInsert();

        try (Connection conn = YourBank.getDatabaseConnector().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            stmt.executeUpdate(insertDefaultSQL);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    @NotNull
    private static String getStringCreate() {
        String createTableSQL;
        if (YourBank.getDatabaseConnector() instanceof MySQL) {
            createTableSQL = """
                CREATE TABLE IF NOT EXISTS StateTreasury (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    diamonds INT
                );""";
        }
        else {
            createTableSQL = """
                CREATE TABLE IF NOT EXISTS StateTreasury (
                    id integer PRIMARY KEY,
                    diamonds INT
                );""";
        }
        return createTableSQL;
    }
    @NotNull
    private static String getStringInsert() {
        String insertDefaultSQL;
        if (YourBank.getDatabaseConnector() instanceof MySQL) {
            insertDefaultSQL = """
                INSERT IGNORE INTO StateTreasury (id, diamonds) VALUES (1, 0);
                """;
        }
        else {
            insertDefaultSQL = """
                INSERT OR IGNORE INTO StateTreasury (id, diamonds) VALUES (1, 0);
                """;;
        }
        return insertDefaultSQL;
    }
}
