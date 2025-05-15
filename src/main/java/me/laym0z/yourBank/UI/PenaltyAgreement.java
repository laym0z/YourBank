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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PenaltyAgreement implements Listener {
    static HashMap<UUID, List<String>> playerPenalties = new HashMap<>();
    public static void openPenaltyAgreementMenu(int ID, int sum, Player player) {
        List<String> temp = new ArrayList<>();
        temp.add(String.valueOf(ID));
        temp.add(String.valueOf(sum));
        temp.add(player.getName());
        playerPenalties.put(player.getUniqueId(), temp);
        Inventory menu = Bukkit.createInventory(null, 54, "Підтвердження оплати");

        menu.setItem(19, createPaper("Оплатити"));
        menu.setItem(20, createPaper("Оплатити"));
        menu.setItem(21, createPaper("Оплатити"));
        menu.setItem(28, createPaper("Оплатити"));
        menu.setItem(29, createPaper("Оплатити"));
        menu.setItem(30, createPaper("Оплатити"));
        menu.setItem(37, createPaper("Оплатити"));
        menu.setItem(38, createPaper("Оплатити"));
        menu.setItem(39, createPaper("Оплатити"));

        menu.setItem(23, createPaper("Відміна"));
        menu.setItem(24, createPaper("Відміна"));
        menu.setItem(25, createPaper("Відміна"));
        menu.setItem(32, createPaper("Відміна"));
        menu.setItem(33, createPaper("Відміна"));
        menu.setItem(34, createPaper("Відміна"));
        menu.setItem(41, createPaper("Відміна"));
        menu.setItem(42, createPaper("Відміна"));
        menu.setItem(43, createPaper("Відміна"));

        player.openInventory(menu);
    }
    private static ItemStack createPaper(String name) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name); // §e — жовтий текст;
            paper.setItemMeta(meta);
        }
        return paper;
    }
    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if (event.getView().getTitle().equals("Підтвердження оплати")) {
            event.setCancelled(true);

            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

            switch (displayName) {
                case "Оплатити" -> {
                    List<String> info = playerPenalties.get(player.getUniqueId());
                    boolean result = Data.payPenalty(Integer.parseInt(info.get(0)), Integer.parseInt(info.get(1)), info.get(2));
                    if (result) {
                        player.sendMessage(ChatColor.GREEN+"[Банк] Штраф успішно оплачено");
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                    else {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        player.sendMessage(ChatColor.RED+"[Банк] Не вистачає коштів для оплати");
                    }
                    player.closeInventory();
                }
                case "Відміна" -> {
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                        BankMain.openBankMenu(player, Data.getPlayerData(player.getName()));
                    }, 1L); // 1 тік затримки
                }
            }
        }
    }
}
