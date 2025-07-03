package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.UI.Bank.BankForBanker;
import me.laym0z.yourBank.YourBank;
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
        Database Database = new Database(YourBank.getDatabaseConnector());
        if (args.length != 1) {
            return false;
        }
        if (Database.getPlayersBank(args[0])) {
            BankForBanker.openBankForBankerMenu(banker, args[0]);
        }
        else {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                    ChatColor.RESET+ChatColor.RED+" Цей гравець не має банківського рахунку!");
        }
        return true;
    }
}
