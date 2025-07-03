package me.laym0z.yourBank.UI.MenuComponents.Buttons;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BankForBankerButtons {
    static String depositButton = ChatColor.GREEN+""+ChatColor.BOLD + "[↑] Поповнити рахунок";
    static String withdrawButton = ChatColor.GREEN+""+ChatColor.BOLD +"[↓] Зняти кошти";

    @NotNull
    public static Map<Integer, String> get() {

        return Map.of(
                11, depositButton,
                12, depositButton,
                20, depositButton,
                21, depositButton,

                14, withdrawButton,
                15, withdrawButton,
                23, withdrawButton,
                24, withdrawButton
        );
    }
    public static  String getDepositButton() {
        return depositButton;
    }
    public static  String getWithdrawButton() {
        return withdrawButton;
    }
}
