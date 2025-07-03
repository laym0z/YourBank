package me.laym0z.yourBank.UI.MenuComponents.Buttons;

import org.bukkit.ChatColor;

import java.util.Map;

public class ListButtons {
    static String prevButton = ChatColor.GOLD+"[←] Попередні";
    static String nextButton = ChatColor.GOLD+"[→] Наступні";

    public static String getPrevButton() {
        return prevButton;
    }
    public static String getNextButton() {
        return nextButton;
    }

}
