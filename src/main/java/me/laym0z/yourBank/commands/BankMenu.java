package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.Data;
import me.laym0z.yourBank.UI.BankForAdmin;
import me.laym0z.yourBank.UI.BankMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BankMenu implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        String path = "jdbc:sqlite:plugins/yourBank/yourBank.db";
        Player admin = (Player) commandSender;
        if (args.length==0) {
            return false;
        }
        if (args.length > 1) {
            return false;
        }
        if (Data.getPlayer(args[0], path, "Bank")) {
            BankForAdmin.openBankMenu(admin, args[0]);
        }
        else {
            commandSender.sendMessage(ChatColor.RED+"[Банк] Цей гравець не має банківського рахунку!");
        }
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
