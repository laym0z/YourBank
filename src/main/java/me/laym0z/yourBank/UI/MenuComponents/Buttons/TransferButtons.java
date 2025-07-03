package me.laym0z.yourBank.UI.MenuComponents.Buttons;

import org.bukkit.ChatColor;

import java.util.Map;

public class TransferButtons {
    static String applyButton = ChatColor.GREEN+""+ChatColor.BOLD+"[\uD83D\uDCE7] Підтвердити";
    static String goBackButton = ChatColor.GRAY+"[↓] Назад";
    public static String CommissionChooseButton = ChatColor.GOLD+"Оплата комісії";


    public static Map<Integer, String> getAddButtons() {
        return Map.of(
                20, ChatColor.GREEN+ "+1",
                21, ChatColor.GREEN+"+4",
                22, ChatColor.GREEN+"+8",
                23, ChatColor.GREEN+"+16",
                24, ChatColor.GREEN+"+32",
                25, ChatColor.GREEN+"+64"
        );
    }
    public static Map<Integer, String> getDeductButtons() {
        return Map.of(
                29, ChatColor.RED+"-1",
                30, ChatColor.RED+"-4",
                31, ChatColor.RED+"-8",
                32, ChatColor.RED+"-16",
                33, ChatColor.RED+"-32",
                34, ChatColor.RED+"-64"
        );
    }
    public static Map<Integer, String> getApplyButtons() {
        return Map.of(
                48, applyButton,
                49, applyButton,
                50, applyButton
        );
    }
    public static String getGoBackButton() {
        return goBackButton;
    }
    public static String getCommissionChooseButton() {
        return CommissionChooseButton;
    }
    public static String getApplyButton() {
        return applyButton;
    }
}
