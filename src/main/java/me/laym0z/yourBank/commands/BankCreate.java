package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.YourBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BankCreate implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        Database Database = new Database(YourBank.getDatabaseConnector());
        if (args.length != 1 || args[0].isEmpty()) {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                    ChatColor.RESET+ChatColor.RED+" Введи гравця");
            return true;
        }
        Player receiver = Bukkit.getPlayerExact(args[0]);

        if (receiver == null) {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                    ChatColor.RESET+ChatColor.RED+" Такого гравця не існує або він не в мережі");
            return true;
        }
        if (Database.getPlayersBank(receiver.getName())) {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                    ChatColor.RESET+ChatColor.RED+" Гравець вже має банківський рахунок");
            return true;
        }

        if (Database.createBankAccount(args[0])) {
            receiver.sendMessage(ChatColor.DARK_GREEN+""+ChatColor.BOLD+ "[Банк]"+
                    ChatColor.RESET+ChatColor.GREEN+" Ви відкрили банківський рахунок");
            commandSender.sendMessage(ChatColor.DARK_GREEN+""+ChatColor.BOLD+ "[Банк]"+
                    ChatColor.RESET+ChatColor.GREEN+" Користувач зареєстрований");
        }
        else {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                    ChatColor.RESET+ChatColor.RED+" Під час реєстрації сталася помилка");
        }
        return true;
    }
}
