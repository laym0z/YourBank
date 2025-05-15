package me.laym0z.yourBank.UI;

import me.laym0z.yourBank.Data.Data;
import me.laym0z.yourBank.YourBank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Penalties implements Listener {

    private static final Map<UUID, Integer> playerPage = new HashMap<>();
    private static final Map<UUID, Inventory> playerMenu = new HashMap<>();
    private static final Map<UUID, List<List<String>>> penaltiesPerPlayer = new HashMap<>();

    public static void openPenaltyListMenu(Player player, String name) {

        penaltiesPerPlayer.put(player.getUniqueId(), Data.getAllPenaltiesOfPlayer(name));
        Inventory menu = Bukkit.createInventory(null, 27, "Список штрафів");


        menu.setItem(18, createPaper("Назад"));
        menu.setItem(9, createPaper("Попередня сторінка"));
        menu.setItem(17, createPaper("Наступна сторінка"));
        menu.setItem(2, createPaper(name));
        playerPage.put(player.getUniqueId(), 0);
        playerMenu.put(player.getUniqueId(), menu);
        displayPenalties(menu, "", player.getUniqueId());
        player.openInventory(menu);
    }
    private static ItemStack createPaper(List<String> lore) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("Штраф"); // §e — жовтий текст
            meta.setLore(lore);
            paper.setItemMeta(meta);
        }
        return paper;
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
    public static void displayPenalties(Inventory menu, String action, UUID uuid) {
        List<List<String>> penalties = penaltiesPerPlayer.get(uuid);
        int index = playerPage.get(uuid);

        if (Objects.equals(action, "-") && index - 7 >= 0) {
            index-=7;
            playerPage.put(uuid, index);
        }
        else if (Objects.equals(action, "+") && index + 7 < penalties.size()) {
            index+=7;
            playerPage.put(uuid, index);
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

            menu.setItem(i+10, createPaper(lore));
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
            String displayName = item.getItemMeta().getDisplayName();
            UUID uuid = player.getUniqueId();
            System.out.println("Name: "+ displayName);
            if (displayName.equals("Попередня сторінка")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                displayPenalties(playerMenu.get(uuid), "-", uuid);
            }
            else if (displayName.equals("Наступна сторінка")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                displayPenalties(playerMenu.get(uuid), "+", uuid);
            }

            else if (displayName.equals("Штраф")
                    && playerMenu.get(uuid).getItem(2).getItemMeta().getDisplayName().equals(player.getName())) {
                //Оплатити штраф!!!
                System.out.println("I AM HERE");
                List<String> lore = item.getItemMeta().getLore();
                String idString = lore.get(0);
                String sumString = lore.get(1);
                idString = idString.replace("ID: ", "");
                sumString = sumString.replace("Сума: ", "");

                int ID = Integer.parseInt(idString);
                int sum = Integer.parseInt(sumString);

                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    PenaltyAgreement.openPenaltyAgreementMenu(ID, sum, player);
                }, 1L); // 1 тік затримки


            }
            else if (displayName.equals("Назад")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    BankMain.openBankMenu(player, Data.getPlayerData(player.getName()));
                }, 1L); // 1 тік затримки
            }
        }
    }
}
