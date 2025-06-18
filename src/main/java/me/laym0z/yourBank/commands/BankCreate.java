package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.Data;
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

        if (args.length != 1 || args[0].isEmpty()) {
            commandSender.sendMessage(ChatColor.RED+"[Банк] Введи гравця");
            return true;
        }
        Player receiver = Bukkit.getPlayerExact(args[0]);

        if (receiver == null) {
            commandSender.sendMessage(ChatColor.RED+"[Банк] Такого гравця не існує або він не в мережі");
            return true;
        }
        if (!Data.getPlayersPassport(receiver.getName())) {
            commandSender.sendMessage(ChatColor.RED+"[Банк] Цей гравець не має паспорту!");
            return true;
        }
        if (Data.getPlayersBank(receiver.getName())) {
            commandSender.sendMessage(ChatColor.RED+"[Банк] Гравець вже має банківський рахунок");
            return true;
        }

        if (Data.createBankAccount(args[0])) {
            receiver.sendMessage(ChatColor.GREEN+"[Банк] Ви відкрили банківський рахунок");
            commandSender.sendMessage(ChatColor.GREEN+"[Банк] Користувач зареєстрований");
        }
        else {
            commandSender.sendMessage(ChatColor.RED+"[Банк] Під час реєстрації сталася помилка");
        }
        return true;
    }
}
