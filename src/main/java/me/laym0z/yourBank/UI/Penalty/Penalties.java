package me.laym0z.yourBank.UI.Penalty;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.UI.Bank.BankMain;
import me.laym0z.yourBank.UI.MenuComponents.Buttons.ListButtons;
import me.laym0z.yourBank.UI.MenuComponents.Buttons.PenaltiesButtons;
import me.laym0z.yourBank.UI.MenuComponents.MenuInteraction;
import me.laym0z.yourBank.UI.Titles;
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

import java.util.*;

public class Penalties implements Listener {

    public static void openPenaltyListMenu(Player admin, String owner, boolean toRemove) {
        Database Database = new Database(YourBank.getDatabaseConnector());
        UUID uuid = admin.getUniqueId();

        Inventory menu = Bukkit.createInventory(null, 27, Titles.PENALTIES_TITLE);
        menu.setItem(18, MenuInteraction.createPaper(PenaltiesButtons.getGoBackButton()));
        menu.setItem(9, MenuInteraction.createPaper(ListButtons.getPrevButton()));
        menu.setItem(17, MenuInteraction.createPaper(ListButtons.getNextButton()));
        menu.setItem(2, MenuInteraction.createPaper(owner));

        YourBank.getPluginContext().penaltiesManager.setPenaltiesPerPlayer(uuid, Database.getAllPenaltiesOfPlayer(owner));
        YourBank.getPluginContext().penaltiesManager.setPlayerPage(uuid, 0);
        YourBank.getPluginContext().penaltiesManager.setPlayerMenu(uuid, menu);
        if (toRemove) {
            YourBank.pluginContext.penaltiesManager.setToRemove(uuid);
        }
        MenuInteraction.displayPenalties(menu, null, uuid);
        admin.openInventory(menu);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getView().getTitle().equals(Titles.PENALTIES_TITLE)) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item==null) return;
            String displayName = Objects.requireNonNull(item.getItemMeta()).getDisplayName();
            UUID uuid = player.getUniqueId();
            if (displayName.equals(ListButtons.getPrevButton())) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                MenuInteraction.displayPenalties(YourBank.getPluginContext().penaltiesManager.getPlayerMenu(uuid),
                        MenuInteraction.listAction.PREV, uuid);
            }
            else if (displayName.equals(ListButtons.getNextButton())) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                MenuInteraction.displayPenalties(YourBank.getPluginContext().penaltiesManager.getPlayerMenu(uuid),
                        MenuInteraction.listAction.NEXT, uuid);
            }
            else if (ChatColor.stripColor(displayName).equals("Штраф")) {
                List<String> lore = item.getItemMeta().getLore();

                String idString = lore.get(0);
                String sumString = lore.get(1);
                String receiver = lore.get(5);

                int ID = Integer.parseInt(ChatColor.stripColor(idString.replace("ID: ", "")));
                int sum = Integer.parseInt(ChatColor.stripColor(sumString.replace("Сума: ", "")));

                String receiverFormated = ChatColor.stripColor(receiver.replace("Отримувач: ", ""));
                String name = YourBank.getPluginContext().penaltiesManager.getPlayerMenu(uuid).getItem(2).
                        getItemMeta().getDisplayName();

                if (name.equals(player.getName())) {
                    Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                        PenaltyAgreement.openPenaltyAgreementMenu(ID, sum, player, receiverFormated);
                    }, 1L); // 1 тік затримки
                } else if (YourBank.pluginContext.penaltiesManager.getToRemove(uuid)) {
                    Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                        RemovePenaltyAgreement.openRemovePenaltyAgreementMenu(ID, sum, player);
                    }, 1L); // 1 тік затримки
                }
            }
            else if (displayName.equals(PenaltiesButtons.getGoBackButton())) {
                Database Database = new Database(YourBank.getDatabaseConnector());
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    BankMain.openBankMenu(player, Database.getPlayerData(player.getName()));
                }, 1L); // 1 тік затримки
            }
        }
    }
}
