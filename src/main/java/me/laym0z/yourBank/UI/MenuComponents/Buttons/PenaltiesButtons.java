package me.laym0z.yourBank.UI.MenuComponents.Buttons;

import me.laym0z.yourBank.UI.MenuComponents.MenuInteraction;
import org.bukkit.ChatColor;

import java.util.Map;

public class PenaltiesButtons extends TransferButtons{

    public static String confirmButton = ChatColor.GREEN+"Підтвердити";
    public static String cancelButton = ChatColor.RED+"Відміна";

    public static Map<Integer, String> getConfirmButtons() {
        return Map.of(
                19, confirmButton,
                20, confirmButton,
                21, confirmButton,
                28, confirmButton,
                29, confirmButton,
                30, confirmButton,
                37, confirmButton,
                38, confirmButton,
                39, confirmButton
        );
    }

    public static Map<Integer, String> getCancelButtons() {
        return Map.of(
                23, cancelButton,
                24, cancelButton,
                25, cancelButton,
                32, cancelButton,
                33, cancelButton,
                34, cancelButton,
                41, cancelButton,
                42, cancelButton,
                43, cancelButton

        );
    }

    public static String getConfirmButton() {return confirmButton;}

    public static String getCancelButton() {return cancelButton;}
}
