package me.laym0z.yourBank.Data.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite implements DatabaseConnector {
    static String registeredPlayersPath = "jdbc:sqlite:plugins/registerPlayers/registerPlayers.db";
    String bankPath;

    public SQLite(String path) {
        this.bankPath = path;
    }
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(bankPath);
    }

}
