package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BankCreate implements CommandExecutor, TabCompleter {
    String bankPath = "jdbc:sqlite:plugins/yourBank/yourBank.db";
    String passportPath = "jdbc:sqlite:plugins/yourPassport/Passports.db";
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        Player receiver = Bukkit.getPlayer(args[0]);

        if (args.length != 1) {
            commandSender.sendMessage(ChatColor.RED+"Введи гравця");
            return true;
        }
        if (receiver == null) {
            commandSender.sendMessage(ChatColor.RED+"Такого гравця не існує або він не в мережі");
            return true;
        }
        if (!Data.getPlayer(receiver.getName(), passportPath, "Players")) {
            commandSender.sendMessage(ChatColor.RED+"Цей гравець не має паспорту!");
            return true;
        }
        if (Data.getPlayer(receiver.getName(), bankPath, "Bank")) {
            commandSender.sendMessage(ChatColor.RED+"Гравець вже має банківський рахунок");
            return true;
        }


        String insertDataSQL = "INSERT INTO Bank(name, diamonds, create_date) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(bankPath);
             PreparedStatement stmt = conn.prepareStatement(insertDataSQL)) {

            stmt.setString(1, args[0]);              // name
            stmt.setInt(2, 0);              // diamonds
            stmt.setString(3, String.valueOf(LocalDate.now()));              // create_date

            stmt.executeUpdate(); // <--- правильний метод для INSERT/UPDATE/DELETE
            System.out.println("Data has been inserted.");

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        receiver.sendMessage(ChatColor.GREEN+"Ви відкрили банківський рахунок");
        commandSender.sendMessage(ChatColor.GREEN+"Користувач зареєстрований");
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length==1) {
            return List.of("<гравець>");
        }
        return List.of();
    }
}
