package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.TempStorage.SQLQueries.Data;
import me.laym0z.yourBank.UI.Bank.BankMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Bank implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) commandSender;

        if (Data.getPlayersBank(player.getName())) {
            BankMain.openBankMenu(player, Data.getPlayerData(player.getName()));
        }
        else {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                    ChatColor.RESET+ChatColor.RED+" У тебе немає банківсього рахунку!");
        }
        return true;
    }
}
