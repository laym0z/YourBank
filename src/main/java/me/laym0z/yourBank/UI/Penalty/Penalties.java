package me.laym0z.yourBank.UI.Penalty;

import me.laym0z.yourBank.Data.Data;
import me.laym0z.yourBank.Test.PrintHashMaps;
import me.laym0z.yourBank.UI.Bank.BankMain;
import me.laym0z.yourBank.UI.MenuComponents.MenuInteraction;
import me.laym0z.yourBank.YourBank;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class Penalties implements Listener {

    public static void openPenaltyListMenu(Player admin, String owner, boolean toRemove) {

        UUID uuid = admin.getUniqueId();

        Inventory menu = Bukkit.createInventory(null, 27, "Список штрафів");
        menu.setItem(18, MenuInteraction.createPaper("Назад"));
        menu.setItem(9, MenuInteraction.createPaper("Попередня сторінка"));
        menu.setItem(17, MenuInteraction.createPaper("Наступна сторінка"));
        menu.setItem(2, MenuInteraction.createPaper(owner));

        YourBank.getPluginContext().penaltiesManager.setPenaltiesPerPlayer(uuid, Data.getAllPenaltiesOfPlayer(owner));
        YourBank.getPluginContext().penaltiesManager.setPlayerPage(uuid, 0);
        YourBank.getPluginContext().penaltiesManager.setPlayerMenu(uuid, menu);
        if (toRemove) {
            YourBank.pluginContext.penaltiesManager.setToRemove(uuid);
        }
        displayPenalties(menu, null, uuid);
        admin.openInventory(menu);
    }

    public static void displayPenalties(Inventory menu, String action, UUID uuid) {
        List<List<String>> penalties = YourBank.getPluginContext().penaltiesManager.getPenaltiesPerPlayer(uuid);
        int index = YourBank.getPluginContext().penaltiesManager.getPlayerPage(uuid);

        if (Objects.equals(action, "-") && index - 7 >= 0) {
            index-=7;
            YourBank.getPluginContext().penaltiesManager.setPlayerPage(uuid, index);
        }
        else if (Objects.equals(action, "+") && index + 7 < penalties.size()) {
            index+=7;
            YourBank.getPluginContext().penaltiesManager.setPlayerPage(uuid, index);
        }
        for (int i = 10; i < 17; i++) {
            menu.setItem(i, null);
        }
        for (int i = 0; i < penalties.size(); i++ ) {
            if (i == 7) break;
            if (index >= penalties.size()) {
                break;
            }
            List<String> penalty = penalties.get(index);
            List<String> lore = new ArrayList<>();

            lore.add("ID: "+penalty.get(0));
            lore.add("Сума: "+penalty.get(2));
            lore.add("Причина: "+penalty.get(3));
            lore.add("Дата створення: "+penalty.get(4));
            lore.add("Термін виплати: "+penalty.get(5));

            menu.setItem(i+10, MenuInteraction.createPaperLore(lore, "Штраф"));
            index++;
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getView().getTitle().equals("Список штрафів")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item==null) return;
            String displayName = Objects.requireNonNull(item.getItemMeta()).getDisplayName();
            UUID uuid = player.getUniqueId();
            switch (displayName) {
                case "§eПопередня сторінка" -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    displayPenalties(YourBank.getPluginContext().penaltiesManager.getPlayerMenu(uuid), "-", uuid);
                }
                case "§eНаступна сторінка" -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    displayPenalties(YourBank.getPluginContext().penaltiesManager.getPlayerMenu(uuid), "+", uuid);
                }
                case "§eШтраф" -> {
                    List<String> lore = item.getItemMeta().getLore();

                    String idString = lore.get(0);
                    String sumString = lore.get(1);

                    int ID = Integer.parseInt(idString.replace("ID: ", ""));
                    int sum = Integer.parseInt(sumString.replace("Сума: ", ""));
                    String name = YourBank.getPluginContext().penaltiesManager.getPlayerMenu(uuid).getItem(2).getItemMeta().getDisplayName().substring(2);
                    if (name.equals(player.getName())) {
                        Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                            PenaltyAgreement.openPenaltyAgreementMenu(ID, sum, player);
                        }, 1L); // 1 тік затримки
                    } else if (YourBank.pluginContext.penaltiesManager.getToRemove(uuid)) {
                        Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                            RemovePenaltyAgreement.openRemovePenaltyAgreementMenu(ID, sum, player);
                        }, 1L); // 1 тік затримки
                    }
                }
                case "§eНазад" -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                        BankMain.openBankMenu(player, Data.getPlayerData(player.getName()));
                    }, 1L); // 1 тік затримки
                }
            }
        }
    }
}
