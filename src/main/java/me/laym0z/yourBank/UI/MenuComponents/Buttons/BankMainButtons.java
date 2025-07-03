package me.laym0z.yourBank.UI.MenuComponents.Buttons;

import me.laym0z.yourBank.UI.MenuComponents.MenuInteraction;
import org.bukkit.ChatColor;

import java.util.Map;

public class BankMainButtons {

    static String transferButton = ChatColor.GREEN+""+ChatColor.BOLD+"[\uD83D\uDCE7] Переказ";
    static String penaltiesButton = ChatColor.DARK_RED+""+ChatColor.BOLD+"[-] Штрафи";

    public static Map<Integer, String> get() {
         return Map.of(
                 20, transferButton,
                 21, transferButton,
                 29, transferButton,
                 30, transferButton,

                 23, penaltiesButton,
                 24, penaltiesButton,
                 32, penaltiesButton,
                 33, penaltiesButton
         );
    }
    public static String getTransferButton() {
        return transferButton;
    }
    public static String getPenaltiesButton() {
        return penaltiesButton;
    }
}
