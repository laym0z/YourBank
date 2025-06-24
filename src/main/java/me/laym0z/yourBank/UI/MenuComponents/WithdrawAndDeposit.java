package me.laym0z.yourBank.UI.MenuComponents;

import me.laym0z.yourBank.UI.Bank.BankForBanker;
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
import java.util.HashMap;
import java.util.UUID;

public class WithdrawAndDeposit implements Listener {

    public static void buildMainMenu(String title, Player admin) {
        Inventory menu = Bukkit.createInventory(null, 54, title);
        // Створення предметів
        menu.setItem(3, MenuInteraction.createPaper(ChatColor.GOLD+ "ДР"));// diamond_ore

        menu.setItem(5, MenuInteraction.createPaper(ChatColor.GOLD+ "ГДР"));//deep_diamond_ore
        //Додати
        menu.setItem(20, MenuInteraction.createPaper(ChatColor.GREEN+ "+1"));
        menu.setItem(21, MenuInteraction.createPaper(ChatColor.GREEN+ "+4"));
        menu.setItem(22, MenuInteraction.createPaper(ChatColor.GREEN+"+8"));
        menu.setItem(23, MenuInteraction.createPaper(ChatColor.GREEN+"+16"));
        menu.setItem(24, MenuInteraction.createPaper(ChatColor.GREEN+"+32"));
        menu.setItem(25, MenuInteraction.createPaper(ChatColor.GREEN+"+64"));
        //Відняти
        menu.setItem(29, MenuInteraction.createPaper(ChatColor.YELLOW+"-1"));
        menu.setItem(30, MenuInteraction.createPaper(ChatColor.YELLOW+"-4"));
        menu.setItem(31, MenuInteraction.createPaper(ChatColor.YELLOW+"-8"));
        menu.setItem(32, MenuInteraction.createPaper(ChatColor.YELLOW+"-16"));
        menu.setItem(33, MenuInteraction.createPaper(ChatColor.YELLOW+"-32"));
        menu.setItem(34, MenuInteraction.createPaper(ChatColor.YELLOW+"-64"));
        //підтвердити
        menu.setItem(48, MenuInteraction.createPaper(ChatColor.GREEN+"Підтвердити"));
        menu.setItem(49, MenuInteraction.createPaper(ChatColor.GREEN+"Підтвердити"));
        menu.setItem(50, MenuInteraction.createPaper(ChatColor.GREEN+"Підтвердити"));

        menu.setItem(45, MenuInteraction.createPaper(ChatColor.GRAY+"[↓] Назад"));

        YourBank.getPluginContext().diamondChoose.setChoose(admin.getUniqueId(), Material.DIAMOND_ORE);

        admin.openInventory(menu);

    }
    @EventHandler
    public static void InventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Поповнення") || event.getView().getTitle().equals("Зняти")) {
            event.setCancelled(true); // Забороняємо забирати предмет
            Inventory clickedInventory = event.getClickedInventory();

            if (clickedInventory == null) return;

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            UUID uuid = player.getUniqueId();
            String displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.startsWith(ChatColor.GREEN+"+")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(3);
                MenuInteraction.IncButtons(clickedInventory, Integer.parseInt(displayName), YourBank.getPluginContext().diamondChoose.getChoose(uuid));
            } else if (displayName.startsWith(ChatColor.YELLOW+"-")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(3);
                MenuInteraction.DicButtons(clickedInventory, Integer.parseInt(displayName), YourBank.getPluginContext().diamondChoose.getChoose(uuid));
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
            else if (ChatColor.stripColor(displayName).equals("[↓] Назад")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    BankForBanker.openBankMenu(player, YourBank.getPluginContext().sessionManager.getReceiver(uuid));
                }, 1L); // 1 тік затримки
            }
            else if (ChatColor.stripColor(displayName).equals("Підтвердити")) {
                HashMap<Integer, String> result = new HashMap<>();
                if (event.getView().getTitle().equals("Зняти")) {
                    result = MenuInteraction.ConfirmWithdraw(clickedInventory,
                            player.getInventory(), YourBank.getPluginContext().diamondChoose.getChoose(uuid), YourBank.getPluginContext().sessionManager.getReceiver(uuid));
                } else if (event.getView().getTitle().equals("Поповнення")) {
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
        if (title.equals("Поповнення") || title.equals("Зняти")) {
            YourBank.pluginContext.diamondChoose.removeChoose(event.getPlayer().getUniqueId());
        }
    }
}
