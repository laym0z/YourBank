package me.laym0z.yourBank.UI.Penalty;

import me.laym0z.yourBank.Data.TempStorage.SQLQueries.Data;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Penalties implements Listener {

    public static void openPenaltyListMenu(Player admin, String owner, boolean toRemove) {

        UUID uuid = admin.getUniqueId();

        Inventory menu = Bukkit.createInventory(null, 27, "Список штрафів");
        menu.setItem(18, MenuInteraction.createPaper(ChatColor.GRAY+"[↓] Назад"));
        menu.setItem(9, MenuInteraction.createPaper(ChatColor.GOLD+"[←] Попередня сторінка"));
        menu.setItem(17, MenuInteraction.createPaper(ChatColor.GOLD+"[→] Наступна сторінка"));
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
            List<String> lore = getStringList(penalties, index);

            menu.setItem(i+10, MenuInteraction.createPaperLore(lore, ChatColor.GOLD+"Штраф"));
            index++;
        }
    }

    @NotNull
    private static List<String> getStringList(List<List<String>> penalties, int index) {
        List<String> penalty = penalties.get(index);
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GOLD+"ID: "+ChatColor.WHITE+penalty.get(0));
        lore.add(ChatColor.GOLD+"Сума: "+ChatColor.WHITE+penalty.get(2));
        lore.add(ChatColor.GOLD+"Причина: "+ChatColor.WHITE+penalty.get(3));
        lore.add(ChatColor.GOLD+"Дата створення: "+ChatColor.WHITE+penalty.get(4));
        lore.add(ChatColor.GOLD+"Термін виплати: "+ChatColor.WHITE+penalty.get(5));
        lore.add(ChatColor.GOLD+"Отримувач: "+ChatColor.WHITE+penalty.get(6));
        return lore;
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
            if (ChatColor.stripColor(displayName).equals("[←] Попередня сторінка")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                displayPenalties(YourBank.getPluginContext().penaltiesManager.getPlayerMenu(uuid), "-", uuid);
            }
            else if (ChatColor.stripColor(displayName).equals("[→] Наступна сторінка")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                displayPenalties(YourBank.getPluginContext().penaltiesManager.getPlayerMenu(uuid), "+", uuid);
            }
            else if (ChatColor.stripColor(displayName).equals("Штраф")) {
                List<String> lore = item.getItemMeta().getLore();

                String idString = lore.get(0);
                String sumString = lore.get(1);
                String receiver = lore.get(5);

                int ID = Integer.parseInt(ChatColor.stripColor(idString.replace("ID: ", "")));
                int sum = Integer.parseInt(ChatColor.stripColor(sumString.replace("Сума: ", "")));
                String receiverFormated = ChatColor.stripColor(receiver.replace("Отримувач: ", ""));
                String name = YourBank.getPluginContext().penaltiesManager.getPlayerMenu(uuid).getItem(2).getItemMeta().getDisplayName();
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
            else if (ChatColor.stripColor(displayName).equals("[↓] Назад")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    BankMain.openBankMenu(player, Data.getPlayerData(player.getName()));
                }, 1L); // 1 тік затримки
            }
        }
    }
}
