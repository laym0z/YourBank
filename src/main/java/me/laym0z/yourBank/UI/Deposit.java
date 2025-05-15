package me.laym0z.yourBank.UI;

import me.laym0z.yourBank.UI.menuHelp.MenuInteraction;
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
import java.util.UUID;


public class Deposit implements Listener {
    static HashMap<UUID, Material> choose = new HashMap<>();
    static HashMap<Player, String> adminAndOwner = new HashMap<>();
    public static void openDepositMenu(Player admin, String owner) {
        adminAndOwner.put(admin, owner);
        Inventory menu = Bukkit.createInventory(null, 54, "Поповнення");
        // Створення предметів
        menu.setItem(3, createPaper("ДР"));// diamond_ore

        menu.setItem(5, createPaper("ГДР"));//deep_diamond_ore

        //Додати
        menu.setItem(20, createPaper("+1"));
        menu.setItem(21, createPaper("+4"));
        menu.setItem(22, createPaper("+8"));
        menu.setItem(23, createPaper("+16"));
        menu.setItem(24, createPaper("+32"));
        menu.setItem(25, createPaper("+64"));

        //Відняти
        menu.setItem(29, createPaper("-1"));
        menu.setItem(30, createPaper("-4"));
        menu.setItem(31, createPaper("-8"));
        menu.setItem(32, createPaper("-16"));
        menu.setItem(33, createPaper("-32"));
        menu.setItem(34, createPaper("-64"));


        //підтвердити
        menu.setItem(48, createPaper("Підтвердити"));
        menu.setItem(49, createPaper("Підтвердити"));
        menu.setItem(50, createPaper("Підтвердити"));

        menu.setItem(45, createPaper("Назад"));
        admin.openInventory(menu);

    }
    private static ItemStack createPaper(String name) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name); // §e — жовтий текст
            paper.setItemMeta(meta);
        }
        return paper;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getView().getTitle().equals("Поповнення")) {
            event.setCancelled(true); // Забороняємо забирати предмет
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            UUID uuid = player.getUniqueId();
            String displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.startsWith("+")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(1);
                MenuInteraction.IncButtons(clickedInventory, Integer.parseInt(displayName), choose.get(uuid));
            }
            else if (displayName.startsWith("-")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(1);
                MenuInteraction.DicButtons(clickedInventory, Integer.parseInt(displayName), choose.get(uuid));
            }
            switch (displayName) {
                case "ДР" -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    choose.put(uuid, Material.DIAMOND_ORE);
                    MenuInteraction.Convert(clickedInventory, choose.get(uuid));
                }
                case "ГДР" -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    choose.put(uuid, Material.DEEPSLATE_DIAMOND_ORE);
                    MenuInteraction.Convert(clickedInventory, choose.get(uuid));
                }
                case "Назад" -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                        BankForAdmin.openBankMenu(player, adminAndOwner.get(player));
                    }, 1L); // 1 тік затримки
                }
                case "Підтвердити" -> {
                    HashMap <Integer, String> result = MenuInteraction.ConfirmDeposit(clickedInventory,
                            player.getInventory(), choose.get(uuid), adminAndOwner.get(player));

                    if (result.containsKey(0)) {
                        player.sendMessage(ChatColor.RED+result.get(0));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    }
                    else {
                        player.closeInventory();
                        player.sendMessage(ChatColor.GREEN+result.get(1));
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                }
            }
        }

    }
}
