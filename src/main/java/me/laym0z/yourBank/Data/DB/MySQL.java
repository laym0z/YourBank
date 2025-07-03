package me.laym0z.yourBank.Data.DB;

import java.sql.*;

public class MySQL implements DatabaseConnector  {
    private final String host, database, user, password;
    private final int port;

    public MySQL(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        return DriverManager.getConnection(url, user, password);
    }
}