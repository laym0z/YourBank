package me.laym0z.yourBank.UI.Bank;


import me.laym0z.yourBank.UI.MenuComponents.WithdrawAndDeposit;
import me.laym0z.yourBank.YourBank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;


public class Withdraw implements Listener {
    public static void openWithdrawMenu(Player admin) {

        WithdrawAndDeposit.buildMainMenu("Зняти", admin);
    }
}
