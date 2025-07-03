package me.laym0z.yourBank.UI.Penalty;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.UI.Bank.BankMain;
import me.laym0z.yourBank.UI.MenuComponents.MenuInteraction;
import me.laym0z.yourBank.YourBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class RemovePenaltyAgreement implements Listener {

    public static void openRemovePenaltyAgreementMenu(int ID, int sum, Player player){
        YourBank.pluginContext.penaltiesManager.setIDToRemove(player.getUniqueId(), ID);
        List<String> temp = new ArrayList<>();
        temp.add(String.valueOf(ID));
        temp.add(String.valueOf(sum));
        temp.add(player.getName());
        YourBank.getPluginContext().penaltiesManager.setPlayerPenalty(player.getUniqueId(), temp);

        Inventory menu = Bukkit.createInventory(null, 54, "Підтвердження видалення");

        menu.setItem(19, MenuInteraction.createPaper(ChatColor.RED+"Видалити"));
        menu.setItem(20, MenuInteraction.createPaper(ChatColor.RED+"Видалити"));
        menu.setItem(21, MenuInteraction.createPaper(ChatColor.RED+"Видалити"));
        menu.setItem(28, MenuInteraction.createPaper(ChatColor.RED+"Видалити"));
        menu.setItem(29, MenuInteraction.createPaper(ChatColor.RED+"Видалити"));
        menu.setItem(30, MenuInteraction.createPaper(ChatColor.RED+"Видалити"));
        menu.setItem(37, MenuInteraction.createPaper(ChatColor.RED+"Видалити"));
        menu.setItem(38, MenuInteraction.createPaper(ChatColor.RED+"Видалити"));
        menu.setItem(39, MenuInteraction.createPaper(ChatColor.RED+"Видалити"));

        menu.setItem(23, MenuInteraction.createPaper(ChatColor.YELLOW+"Відміна"));
        menu.setItem(24, MenuInteraction.createPaper(ChatColor.YELLOW+"Відміна"));
        menu.setItem(25, MenuInteraction.createPaper(ChatColor.YELLOW+"Відміна"));
        menu.setItem(32, MenuInteraction.createPaper(ChatColor.YELLOW+"Відміна"));
        menu.setItem(33, MenuInteraction.createPaper(ChatColor.YELLOW+"Відміна"));
        menu.setItem(34, MenuInteraction.createPaper(ChatColor.YELLOW+"Відміна"));
        menu.setItem(41, MenuInteraction.createPaper(ChatColor.YELLOW+"Відміна"));
        menu.setItem(42, MenuInteraction.createPaper(ChatColor.YELLOW+"Відміна"));
        menu.setItem(43, MenuInteraction.createPaper(ChatColor.YELLOW+"Відміна"));

        player.openInventory(menu);
    }
    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Database Database = new Database(YourBank.getDatabaseConnector());
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if (event.getView().getTitle().equals("Підтвердження видалення")) {
            event.setCancelled(true);
            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            if (ChatColor.stripColor(displayName).equals("Видалити")) {
                Database.removePenalty(YourBank.pluginContext.penaltiesManager.getIDToRemove(player.getUniqueId()));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN+"Штраф був видалений");
            }
            else if (ChatColor.stripColor(displayName).equals("Відміна")) {
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    BankMain.openBankMenu(player, Database.getPlayerData(player.getName()));
                }, 1L); // 1 тік затримки
            }
        }
    }
    @EventHandler
    public void inventoryCloseEvent (InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().equals("Підтвердження видалення")) {
            YourBank.pluginContext.penaltiesManager.removeFromIDToRemove(player.getUniqueId());
            YourBank.pluginContext.penaltiesManager.removePlayerPenalty(player.getUniqueId());
        }
    }
}
