package me.laym0z.yourBank.Data.DB;
import me.laym0z.yourBank.config.bankConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {
    private final DatabaseConnector connector;

    public Database(DatabaseConnector connector) {
        this.connector = connector;
    }

    //----------------------REGISTERED PLAYER----------------------------
    public void addToTable(String uuid, String name, String date) {
        String createTableSQL;
        if (connector instanceof MySQL) {
            createTableSQL = """
                    INSERT IGNORE INTO registered_players (uuid, name, join_date) VALUES (?, ?, ?);
                    """;
        }
        else {
            createTableSQL = """
                INSERT OR IGNORE INTO registered_players (uuid, name, join_date) VALUES (?, ?, ?);
                """;
        }
        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, name);
            if (connector instanceof MySQL) {
                pstmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            }
            else {
                pstmt.setString(3, date);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public boolean hasPlayedBefore(String name) {
        String sql = "SELECT 1 FROM registered_players WHERE name = ? LIMIT 1";
        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return false;
    }

    //-------------------------BANK--------------------------------

    public boolean createBankAccount(String name) {
        String insertDataSQL = "INSERT INTO Bank(name, diamonds, create_date) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertDataSQL)) {

            stmt.setString(1, name);
            stmt.setInt(2, 0);
            if (connector instanceof MySQL) {
                stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            }
            else {
                stmt.setString(3, String.valueOf(LocalDate.now()));
            }
            int result = stmt.executeUpdate();
            if (result == 1) {
                System.out.println("Database has been inserted.");
                return true;
            }
            else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return false;
    }

    public boolean getPlayersBank(String name) {
        String sql = "SELECT 1 FROM Bank WHERE name = ? LIMIT 1";
        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return false;
    }

    public List<String> getAllPlayers(String name) {
        String SQLselectQuery = "SELECT name FROM Bank WHERE name != ? ORDER BY name ASC";
        List<String> names = new ArrayList<>();

        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLselectQuery)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return names;

    }

    public List<List<String>> getTopPlayers() {
        String SQLselectQuery = "SELECT name, diamonds FROM Bank ORDER BY diamonds DESC";
        List<List<String>> names = new ArrayList<>();
        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLselectQuery)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    List<String> temp = new ArrayList<>();
                    temp.add(rs.getString("name"));
                    temp.add(String.valueOf(rs.getInt("diamonds")));
                    names.add(temp);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return names;

    }

    public String[] getPlayerData(String name) {
        String SQLselectQuery = "SELECT * FROM Bank WHERE name = ?";
        String[] data = null;

        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLselectQuery)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    data = new String[]{
                            rs.getString("name"),
                            String.valueOf(rs.getInt("diamonds")),
                            rs.getString("create_date")
                    };
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return data;
    }

    public HashMap<Integer, String> addToBalance(String type, String name, int Amount) {
        HashMap <Integer, String> result = new HashMap<>();
        String SQLQuery = "UPDATE Bank SET diamonds = diamonds + ? WHERE name = ?";

        if (type.equals("deep_diamonds")) {
            Amount = ConvertToDiamonds(Amount);
        }

        try (Connection conn = connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery)){
            ps.setInt(1, Amount);
            ps.setString(2, name);
            int update = ps.executeUpdate();
            if (update == 1) result.put(1, "[Банк] Баланс оновлено");
            else if (update == 0) result.put(0, "[Банк] На рахунку недостатньо коштів");
        }
        catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            result.put(0, "[Банк] Сталась помилка");
            return result;
        }
        return result;
    }

    public HashMap<Integer, String> deductFromBalance(String type, String name, int Amount) {
        HashMap <Integer, String> result = new HashMap<>();
        String SQLQuery = "UPDATE Bank SET diamonds = diamonds - ? WHERE name = ? AND diamonds >= ?";

        if (type.equals("deep_diamonds")) {
            Amount = ConvertToDiamonds(Amount);
        }
        try (Connection conn = connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery)){
            ps.setInt(1, Amount);
            ps.setString(2, name);
            ps.setInt(3, Amount);
            int update = ps.executeUpdate();
            if (update == 1) result.put(1, "[Банк] Баланс оновлено");
            else if (update == 0) result.put(0, "[Банк] На рахунку недостатньо коштів");
        }
        catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            result.put(0, "[Банк] Сталась помилка");
            return result;
        }
        return result;
    }

    public int ConvertToDiamonds(int Amount) {
        return Amount/bankConfig.getInstance().getExchangeRate();
    }

    public boolean isPlayerBlocked(String name) {
        boolean isBlocked=false;
        String SQLSelect = "SELECT is_blocked FROM Bank WHERE name = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement select = conn.prepareStatement(SQLSelect)) {
            select.setString(1, name);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    isBlocked = rs.getBoolean("is_blocked");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return isBlocked;
    }

    public boolean makeTransaction(String receiver, String sender, int sum, Boolean payCommission) {
        String SQLAddToReceiver = "UPDATE Bank SET diamonds = diamonds + ? WHERE name = ?";
        String SQLDeductFromSender = "UPDATE Bank SET diamonds = diamonds - ? WHERE name = ? AND diamonds >= ?";
        String SQLAddToStateTreasury = "UPDATE StateTreasury SET diamonds = diamonds + ?";

        try (Connection conn = connector.getConnection();
            PreparedStatement deductFromSender = conn.prepareStatement(SQLDeductFromSender)) {
            int resultSum;
            double percent = bankConfig.getInstance().getPerTransaction();
            if (payCommission) {
                resultSum = (int) Math.round(sum + (sum*percent));
            }
            else {
                resultSum = sum;
            }
            conn.setAutoCommit(false);
            deductFromSender.setInt(1, resultSum);
            deductFromSender.setString(2, sender);
            deductFromSender.setInt(3, resultSum);
            int update = deductFromSender.executeUpdate();
            System.out.println("deduct update: "+ update);
            if (update==0) {
                return false;
            }
            try (PreparedStatement addToReceiver = conn.prepareStatement(SQLAddToReceiver)) {
                if (payCommission) {
                    resultSum = sum;
                }
                else {
                    resultSum = sum - ((int) Math.round(sum*percent));
                }
                addToReceiver.setInt(1,resultSum);
                addToReceiver.setString(2, receiver);
                int addUpdate = addToReceiver.executeUpdate();
                System.out.println("Player: "+receiver);
                System.out.println("Add update: "+ addUpdate);
            }
            try (PreparedStatement addToStateTreasury = conn.prepareStatement(SQLAddToStateTreasury)) {
                addToStateTreasury.setInt(1, (int) Math.round(sum*percent));

                int addToState = addToStateTreasury.executeUpdate();
                System.out.println("add to state update: "+ addToState);
            }
            conn.commit();
            return true;
        }
        catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return false;
    }

    //------------------------PENALTY-------------------------------

    public void addPenalty(String name, int amount, String reason, LocalDate creationDate, LocalDate paymentTerm, String receiver) {
        String SQLAddPenalty ="INSERT INTO Penalties(name, amount, reason, creation_date, payment_term, receiver) VALUES (?, ?, ?, ?, ?, ?)";
        String SQLBlockQuery = "UPDATE Bank SET is_blocked = 1 WHERE name = ?";
        boolean haveToBlock=haveToBlock(name);

        try (Connection conn = connector.getConnection();
             //add penalty
             PreparedStatement addPenalty = conn.prepareStatement(SQLAddPenalty)) {
            conn.setAutoCommit(false);
            addPenalty.setString(1, name);
            addPenalty.setInt(2, amount);
            addPenalty.setString(3, reason);
            if (connector instanceof MySQL) {
                addPenalty.setDate(4, java.sql.Date.valueOf(creationDate));
                addPenalty.setDate(5, java.sql.Date.valueOf(paymentTerm));
            }
            else {
                addPenalty.setString(4, String.valueOf(creationDate));
                addPenalty.setString(5, String.valueOf(paymentTerm));
            }
            addPenalty.setString(6, receiver);
            addPenalty.executeUpdate();

            //block if we have to

            if (haveToBlock) {
                try (PreparedStatement blockStmt = conn.prepareStatement(SQLBlockQuery)) {
                    blockStmt.setString(1, name);
                    blockStmt.executeUpdate();
                }
            }
            conn.commit();
        }
        catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            try {
                if (!e.getMessage().contains("not in transaction")) {
                    connector.getConnection().rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Rollback Error: " + ex.getMessage());
            }
        }
    }

    public List<List<String>> getAllPenaltiesOfPlayer(String name) {
        List<List<String>> penalties = new ArrayList<>();
        String sql ="SELECT * FROM Penalties WHERE name = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getString(i));
                    }
                    penalties.add(row);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return penalties;
    }

    public boolean doesPlayerHavePenalties(String name) {
        String SQLselectQuery = "SELECT name FROM Penalties WHERE name = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLselectQuery)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return false;
    }

    public boolean removePenalty(int id) {
        String SQLDelete = "DELETE FROM Penalties WHERE id = ?";
        String SQLUnBlockQuery = "UPDATE Bank SET is_blocked = 0 WHERE name = ?";
        String SelectNameById = "SELECT name FROM Penalties WHERE id = ?";
        String name;

        boolean result = false;
        try (Connection conn = connector.getConnection();
             PreparedStatement selectName = conn.prepareStatement(SelectNameById)) {
            conn.setAutoCommit(false);

            selectName.setInt(1, id);
            try (ResultSet rs = selectName.executeQuery()) {
                name = rs.getString("name");
            }


            try(PreparedStatement deleteStmt = conn.prepareStatement(SQLDelete)) {

                deleteStmt.setInt(1, id);
                int affectedRows = deleteStmt.executeUpdate();
                result = affectedRows > 0;
            }

            if (!haveToBlock(name)) {
                try (PreparedStatement unBlockStmt = conn.prepareStatement(SQLUnBlockQuery)) {
                    unBlockStmt.setString(1, name);
                    unBlockStmt.executeUpdate();
                }
            }
            conn.commit();
            return result;

        }  catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            try {
                // У випадку помилки — відкат змін
                if (!e.getMessage().contains("not in transaction")) {
                    connector.getConnection().rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Rollback Error: " + ex.getMessage());
            }
        }
        return result;
    }

    public boolean payPenalty(int ID, int sum, String name, String receiver) {

        String sqlCheck = "SELECT diamonds FROM bank WHERE name = ?";
        String sqlBank = "UPDATE bank SET diamonds = diamonds - ? WHERE name = ?";
        String sqlPenalty = "DELETE FROM Penalties WHERE id = ?";
        String SQLUnBlockQuery = "UPDATE Bank SET is_blocked = 0 WHERE name = ?";
        String SQLPayToState = "UPDATE StateTreasury SET diamonds = diamonds + ?";
        String SQLPayToPlayer = "UPDATE Bank SET diamonds = diamonds + ? WHERE name = ?";

        try (Connection conn = connector.getConnection()) {
            conn.setAutoCommit(false); // Початок транзакції

            // Перевірка, чи вистачає діамантів
            try (PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)) {
                checkStmt.setString(1, name);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int balance = rs.getInt("diamonds");
                        if (balance < sum) {
                            conn.rollback();
                            return false;
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            // Віднімаємо суму з рахунку
            try (PreparedStatement updateStmt = conn.prepareStatement(sqlBank)) {
                updateStmt.setInt(1, sum);
                updateStmt.setString(2, name);
                updateStmt.executeUpdate();
            }
            if (receiver.equalsIgnoreCase("держава")) {
                try (PreparedStatement payToStateQuery = conn.prepareStatement(SQLPayToState)) {
                    payToStateQuery.setInt(1, sum);
                    payToStateQuery.executeUpdate();
                }
            }else {
                try (PreparedStatement payToPlayerQuery = conn.prepareStatement(SQLPayToPlayer)) {
                    payToPlayerQuery.setInt(1, sum);
                    payToPlayerQuery.setString(2, receiver);
                    payToPlayerQuery.executeUpdate();
                }
            }
            // Видаляємо штраф
            try (PreparedStatement deleteStmt = conn.prepareStatement(sqlPenalty)) {
                deleteStmt.setInt(1, ID);
                deleteStmt.executeUpdate();
            }
            if (!haveToBlock(name)) {
                try (PreparedStatement blockStmt = conn.prepareStatement(SQLUnBlockQuery)) {
                    blockStmt.setString(1, name);
                    blockStmt.executeUpdate();
                }
            }
            conn.commit(); // Якщо все пройшло успішно — фіксуємо зміни
            return true;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            try {
                if (!e.getMessage().contains("not in transaction")) {
                    connector.getConnection().rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Rollback Error: " + ex.getMessage());
            }
        }
        return false;
    }

    public boolean haveToBlock(String name) {
        String SQLselectQuery = "SELECT COUNT(*) FROM Penalties WHERE name = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(SQLselectQuery)) {
            conn.setAutoCommit(false);
            selectStmt.setString(1, name);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count >= 5) {
                        conn.rollback();
                        return true;
                    }
                }
            }
        }
        catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return false;
    }

}