package me.laym0z.yourBank.UI;

import me.laym0z.yourBank.Data.Data;
import me.laym0z.yourBank.YourBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class BankForAdmin implements Listener {
    static HashMap<Player, String> adminAndReceiver = new HashMap<>();
    public static void openBankMenu(Player admin, String owner) {
        String[] data = Data.getPlayerData(owner);
        admin.closeInventory();
        Inventory menu = Bukkit.createInventory(null, 36, "Меню адміна");
        menu.setItem(11, createPaper("§eПоповнити рахунок"));
        menu.setItem(12, createPaper("§eПоповнити рахунок"));
        menu.setItem(20, createPaper("§eПоповнити рахунок"));
        menu.setItem(21, createPaper("§eПоповнити рахунок"));



        menu.setItem(4, createPaper(format(Integer.parseInt(data[1]), "ДР")));
        menu.setItem(14, createPaper("§eЗняти кошти"));
        menu.setItem(15, createPaper("§eЗняти кошти"));
        menu.setItem(23, createPaper("§eЗняти кошти"));
        menu.setItem(24, createPaper("§eЗняти кошти"));

        adminAndReceiver.put(admin, owner);
        admin.openInventory(menu);

    }

    private static ItemStack createPaper(String name) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + name); // §e — жовтий текст

            paper.setItemMeta(meta);
        }
        return paper;
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
        if (event.getView().getTitle().equals("Меню адміна")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            String displayName = clickedItem.getItemMeta().getDisplayName();
            switch (displayName) {
                case "§eПоповнити рахунок" -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.closeInventory();

                    Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                        Deposit.openDepositMenu(player, adminAndReceiver.get(player));
                    }, 1L); // 1 тік затримки
                }
                case "§eЗняти кошти" -> {
                    if (Data.isPlayerBlocked(player.getName())) {
                        player.sendMessage(ChatColor.RED+ "[Банк] Можливість переказів заблокована через несплату штрафів");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                        Withdraw.openWithdrawMenu(player, adminAndReceiver.get(player));
                    }, 1L); // 1 тік затримки
                }
            }
        }
    }
}