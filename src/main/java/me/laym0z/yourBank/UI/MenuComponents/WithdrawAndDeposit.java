package me.laym0z.yourBank.UI.MenuComponents;

import me.laym0z.yourBank.UI.Bank.BankForBanker;
import me.laym0z.yourBank.UI.MenuComponents.Buttons.ChangeAmountButtons;
import me.laym0z.yourBank.UI.MenuComponents.Buttons.WithdrawAndDepositButtons;
import me.laym0z.yourBank.UI.Titles;
import me.laym0z.yourBank.YourBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WithdrawAndDeposit implements Listener {

    public static void buildMainMenu(String title, Player admin) {
        Inventory menu = Bukkit.createInventory(null, 54, title);
        // Створення предметів
        menu.setItem(3, MenuInteraction.createPaper(ChatColor.GOLD+ "ДР"));// diamond_ore

        menu.setItem(5, MenuInteraction.createPaper(ChatColor.GOLD+ "ГДР"));//deep_diamond_ore

        Map<Integer, String> addButtons = WithdrawAndDepositButtons.getAddButtons();
        Map <Integer, String> deductButtons = WithdrawAndDepositButtons.getDeductButtons();
        Map <Integer, String> applyButtons = WithdrawAndDepositButtons.getApplyButtons();

        for (Map.Entry<Integer, String> entry : addButtons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }
        for (Map.Entry<Integer, String> entry : deductButtons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }
        for (Map.Entry<Integer, String> entry : applyButtons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }

        menu.setItem(45, MenuInteraction.createPaper(WithdrawAndDepositButtons.getGoBackButton()));

        YourBank.getPluginContext().diamondChoose.setChoose(admin.getUniqueId(), Material.DIAMOND_ORE);

        admin.openInventory(menu);

    }
    @EventHandler
    public static void InventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(Titles.DEPOSIT_TITLE) ||
                event.getView().getTitle().equals(Titles.WITHDRAW_TITLE)) {
            event.setCancelled(true); // Забороняємо забирати предмет
            Inventory clickedInventory = event.getClickedInventory();

            if (clickedInventory == null) return;

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            UUID uuid = player.getUniqueId();
            String displayName = clickedItem.getItemMeta().getDisplayName();
            if (ChatColor.stripColor(displayName).startsWith("+")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = ChatColor.stripColor(displayName);
                displayName = displayName.substring(1);
                ChangeAmountButtons.IncButtons(clickedInventory, Integer.parseInt(displayName), YourBank.getPluginContext().diamondChoose.getChoose(uuid));
            } else if (ChatColor.stripColor(displayName).startsWith("-")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = ChatColor.stripColor(displayName);
                displayName = displayName.substring(1);
                ChangeAmountButtons.DicButtons(clickedInventory, Integer.parseInt(displayName), YourBank.getPluginContext().diamondChoose.getChoose(uuid));
            }
            if (ChatColor.stripColor(displayName).equals("ДР")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                YourBank.getPluginContext().diamondChoose.setChoose(uuid, Material.DIAMOND_ORE);
                MenuInteraction.Convert(clickedInventory, Material.DIAMOND_ORE);
            }
            else if (ChatColor.stripColor(displayName).equals("ГДР")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                YourBank.getPluginContext().diamondChoose.setChoose(uuid, Material.DEEPSLATE_DIAMOND_ORE);
                MenuInteraction.Convert(clickedInventory, Material.DEEPSLATE_DIAMOND_ORE);
            }
            else if (displayName.equals(WithdrawAndDepositButtons.getGoBackButton())) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    BankForBanker.openBankForBankerMenu(player, YourBank.getPluginContext().sessionManager.getReceiver(uuid));
                }, 1L); // 1 тік затримки
            }
            else if (displayName.equals(WithdrawAndDepositButtons.getApplyButton())) {
                HashMap<Integer, String> result = new HashMap<>();
                if (event.getView().getTitle().equals(Titles.WITHDRAW_TITLE)) {
                    result = MenuInteraction.ConfirmWithdraw(clickedInventory,
                            player.getInventory(), YourBank.getPluginContext().diamondChoose.getChoose(uuid), YourBank.getPluginContext().sessionManager.getReceiver(uuid));
                } else if (event.getView().getTitle().equals(Titles.DEPOSIT_TITLE)) {
                    result = MenuInteraction.ConfirmDeposit(clickedInventory,
                            player.getInventory(), YourBank.getPluginContext().diamondChoose.getChoose(uuid), YourBank.getPluginContext().sessionManager.getReceiver(uuid));
                }

                if (result.containsKey(0)) {
                    player.sendMessage(ChatColor.RED + result.get(0));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                } else {
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + result.get(1));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
            }
        }
    }
    @EventHandler
    public static void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(Titles.DEPOSIT_TITLE) || title.equals(Titles.WITHDRAW_TITLE)) {
            YourBank.pluginContext.diamondChoose.removeChoose(event.getPlayer().getUniqueId());
        }
    }
}
