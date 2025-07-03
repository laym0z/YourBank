package me.laym0z.yourBank.UI.Bank;

import me.laym0z.yourBank.UI.MenuComponents.WithdrawAndDeposit;
import me.laym0z.yourBank.UI.Titles;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Deposit implements Listener {
    public static void openDepositMenu(Player admin) {
        WithdrawAndDeposit.buildMainMenu(Titles.DEPOSIT_TITLE, admin);
    }
}


