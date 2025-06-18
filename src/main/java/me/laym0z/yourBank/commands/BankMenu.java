package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.Data;
import me.laym0z.yourBank.UI.Bank.BankForBanker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;;

public class BankMenu implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        Player banker = (Player) commandSender;
        if (args.length != 1) {
            return false;
        }
        if (Data.getPlayersBank(args[0])) {
            BankForBanker.openBankMenu(banker, args[0]);
        }
        else {
            commandSender.sendMessage(ChatColor.RED+"[Банк] Цей гравець не має банківського рахунку!");
        }
        return true;
    }
}
