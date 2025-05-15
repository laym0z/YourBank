package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.Data;
import me.laym0z.yourBank.UI.BankMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Bank implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        BankMain.openBankMenu(player, Data.getPlayerData(player.getName()));
        return true;
    }
}
