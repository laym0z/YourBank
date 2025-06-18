package me.laym0z.yourBank.UI.Bank;

import me.laym0z.yourBank.Data.Data;
import me.laym0z.yourBank.Test.PrintHashMaps;
import me.laym0z.yourBank.UI.MenuComponents.MenuInteraction;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Transfer implements Listener {
    public static void openTransferMenu(Player player) {

        Inventory menu = Bukkit.createInventory(null, 54, "Переведення");
        // Створення предметів

        //Додати
        menu.setItem(20, MenuInteraction.createPaper("+1"));
        menu.setItem(21, MenuInteraction.createPaper("+4"));
        menu.setItem(22, MenuInteraction.createPaper("+8"));
        menu.setItem(23, MenuInteraction.createPaper("+16"));
        menu.setItem(24, MenuInteraction.createPaper("+32"));
        menu.setItem(25, MenuInteraction.createPaper("+64"));

        //Відняти
        menu.setItem(29, MenuInteraction.createPaper("-1"));
        menu.setItem(30, MenuInteraction.createPaper("-4"));
        menu.setItem(31, MenuInteraction.createPaper("-8"));
        menu.setItem(32, MenuInteraction.createPaper("-16"));
        menu.setItem(33, MenuInteraction.createPaper("-32"));
        menu.setItem(34, MenuInteraction.createPaper("-64"));

        menu.setItem(0, MenuInteraction.createPaper("§eПопередні"));
        menu.setItem(8, MenuInteraction.createPaper("§eНаступні"));

        //підтвердити
        menu.setItem(48, MenuInteraction.createPaper("Перевести"));
        menu.setItem(49, MenuInteraction.createPaper("Перевести"));
        menu.setItem(50, MenuInteraction.createPaper("Перевести"));

        menu.setItem(45, MenuInteraction.createPaper("Назад"));

        UUID uuid = player.getUniqueId();

        YourBank.getPluginContext().transferManager.setPlayerPage(uuid, 0);
        YourBank.getPluginContext().transferManager.setPlayerMenu(uuid, menu);
        setListOfPlayers(getAllBankUsers(player), player, menu, "");

        player.openInventory(menu);
    }


    public static List<List<String>> getAllBankUsers(Player mainPlayer) {
        List <String> allNames = Data.getAllPlayers(mainPlayer.getName());
        List<List<String>> grouped = new ArrayList<>();

        //--------TEST--------
//        for (int i = 0; i <= 20; i++) {
//            allNames.add(String.valueOf(i));
//        }
        //--------------------

        int groupSize = 7;
        for (int i = 0; i < allNames.size(); i += groupSize) {
            int end = Math.min(i + groupSize, allNames.size());
            grouped.add(allNames.subList(i, end));
        }
        return grouped;
    }

    public static void setListOfPlayers(List<List<String>> players, Player player, Inventory menu, String action) {

        int indexOfPlayerInSubList = YourBank.getPluginContext().transferManager.getPlayerPage(player.getUniqueId());
        if (Objects.equals(action, "plus") && indexOfPlayerInSubList+1 < players.size()) indexOfPlayerInSubList++;
        else if (Objects.equals(action, "minus") && indexOfPlayerInSubList-1 >= 0) indexOfPlayerInSubList--;

        // Перевірка виходу за межі
        if (indexOfPlayerInSubList < 0 || indexOfPlayerInSubList >= players.size()) return;


        List<String> subList = players.get(indexOfPlayerInSubList);

        // Очищаємо старі слоти, якщо потрібно
        for (int i = 1; i <= 7; i++) {
            menu.setItem(i, null);
        }

        // Додаємо гравців у слоти (з 1 по 7)
        for (int i = 0; i < subList.size(); i++) {
            menu.setItem(i + 1, MenuInteraction.createPaper("§eГравцю: " + subList.get(i)));
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getView().getTitle().equals("Переведення")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            String displayName = clickedItem.getItemMeta().getDisplayName();

            Player player = (Player) event.getWhoClicked();

            UUID uuid = player.getUniqueId();
            String[] formated = displayName.split(" ");
            if (displayName.startsWith("§e+")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(3);
                MenuInteraction.IncButtons(clickedInventory, Integer.parseInt(displayName), Material.DIAMOND_ORE);
            }
            else if (displayName.startsWith("§e-")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(3);
                MenuInteraction.DicButtons(clickedInventory, Integer.parseInt(displayName), Material.DIAMOND_ORE);
            }
            if (displayName.equals("§eПопередні")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                setListOfPlayers(getAllBankUsers(player), player, YourBank.getPluginContext().transferManager.getPlayerMenu(uuid), "minus");

            }
            else if (displayName.equals("§eНаступні")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                setListOfPlayers(getAllBankUsers(player), player, YourBank.getPluginContext().transferManager.getPlayerMenu(uuid), "plus");
            }

            else if (displayName.startsWith("§eГравцю:")) {
                YourBank.getPluginContext().transferManager.getPlayerMenu(uuid).setItem(40, MenuInteraction.createPaper(formated[1]));
            }
            else if (displayName.equals("§eПеревести")) {
                if (YourBank.getPluginContext().transferManager.getPlayerMenu(uuid).getItem(40) == null) {
                    player.sendMessage(ChatColor.RED+"[Банк] Вибери отримувача");
                    return;
                }
                int sum = MenuInteraction.getAmountFromSlots(YourBank.getPluginContext().transferManager.getPlayerMenu(uuid));
                if (sum == 0) {
                    player.sendMessage(ChatColor.RED+"[Банк] Введи суму");
                    return;
                }
                String[] data = Data.getPlayerData(player.getName());
                if (Integer.parseInt(data[1]) < sum) {
                    player.sendMessage(ChatColor.RED+"[Банк] Недостатньо коштів");
                    return;
                }

                String receiver = Objects.requireNonNull(Objects.requireNonNull(YourBank.getPluginContext().transferManager
                        .getPlayerMenu(uuid).getItem(40)).getItemMeta()).getDisplayName();

                if (Data.getPlayersBank(receiver)) {
                    if (Data.makeTransaction(receiver, player.getName(), sum)) {
                        player.sendMessage(ChatColor.GREEN+"[Банк] Переведення коштів успішне");
                    }
                    else {
                        player.sendMessage(ChatColor.RED+"[Банк] У тебе недостатньо коштів");
                    }
                    player.closeInventory();
                }
                else {
                    event.getWhoClicked().sendMessage(ChatColor.RED+"[Банк] У цього гравця немає банківського рахунку");
                }
            }
            else if (displayName.equals("§eНазад")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    BankMain.openBankMenu(player, Data.getPlayerData(player.getName()));
                }, 1L); // 1 тік затримки
            }
        }
    }
    @EventHandler
    public static void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Переведення")) {
            YourBank.getPluginContext().transferManager.removePlayerPage(event.getPlayer().getUniqueId());
            YourBank.getPluginContext().transferManager.removePlayerMenu(event.getPlayer().getUniqueId());
        }

    }
}