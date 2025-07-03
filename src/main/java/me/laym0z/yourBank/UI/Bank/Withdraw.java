package me.laym0z.yourBank.UI.Bank;

import me.laym0z.yourBank.UI.MenuComponents.WithdrawAndDeposit;
import me.laym0z.yourBank.UI.Titles;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Withdraw implements Listener {
    public static void openWithdrawMenu(Player admin) {
        WithdrawAndDeposit.buildMainMenu(Titles.WITHDRAW_TITLE, admin);
    }
}
