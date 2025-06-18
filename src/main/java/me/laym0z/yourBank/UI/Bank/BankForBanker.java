package me.laym0z.yourBank.UI.Bank;

import me.laym0z.yourBank.Data.Data;
import me.laym0z.yourBank.UI.MenuComponents.MenuInteraction;
import me.laym0z.yourBank.YourBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BankForBanker implements Listener {
    public static void openBankMenu(Player banker, String owner) {
        String[] data = Data.getPlayerData(owner);

        banker.closeInventory();
        Inventory menu = Bukkit.createInventory(null, 36, "Меню банкіра");
        menu.setItem(11, MenuInteraction.createPaper("§eПоповнити рахунок"));
        menu.setItem(12, MenuInteraction.createPaper("§eПоповнити рахунок"));
        menu.setItem(20, MenuInteraction.createPaper("§eПоповнити рахунок"));
        menu.setItem(21, MenuInteraction.createPaper("§eПоповнити рахунок"));

        menu.setItem(4, MenuInteraction.createPaper(format(Integer.parseInt(data[1]), "ДР")));
        menu.setItem(14, MenuInteraction.createPaper("§eЗняти кошти"));
        menu.setItem(15, MenuInteraction.createPaper("§eЗняти кошти"));
        menu.setItem(23, MenuInteraction.createPaper("§eЗняти кошти"));
        menu.setItem(24, MenuInteraction.createPaper("§eЗняти кошти"));

        YourBank.getPluginContext().sessionManager.setReceiver(banker.getUniqueId(), owner);
        banker.openInventory(menu);
    }

    public static String format(int amount, String type) {
        int t = amount % 64;
        if (t != 0) {
            int stacks = (amount-(amount % 64))/64;
            return amount+" "+type+" | "+stacks+" ст. та "+t+" "+type;
        }
        return amount+" "+type+" | "+amount/64+" ст. ";
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getView().getTitle().equals("Меню банкіра")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            String displayName = Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName();
            switch (displayName) {
                case "§eПоповнити рахунок" -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.closeInventory();

                    Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> Deposit.openDepositMenu(player), 1L); // 1 тік затримки
                }
                case "§eЗняти кошти" -> {
                    if (Data.isPlayerBlocked(player.getName())) {
                        player.sendMessage(ChatColor.RED+ "[Банк] Операція заблокована через велику кількість несплачених штрафів");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> Withdraw.openWithdrawMenu(player), 1L); // 1 тік затримки
                }
            }
        }
    }
}