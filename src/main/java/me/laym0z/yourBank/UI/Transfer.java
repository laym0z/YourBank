package me.laym0z.yourBank.UI;

import me.laym0z.yourBank.Data.Data;
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

import java.util.*;

public class Transfer implements Listener {
    private static final Map<UUID, Integer> playerPage = new HashMap<>();
    static HashMap<UUID, Inventory> playersMenus = new HashMap<>();
    public static void openTransferMenu(Player player) {

        Inventory menu = Bukkit.createInventory(null, 54, "Переведення");
        // Створення предметів

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

        menu.setItem(0, createPaper("§eПопередні"));
        menu.setItem(8, createPaper("§eНаступні"));

        //підтвердити
        menu.setItem(48, createPaper("Перевести"));
        menu.setItem(49, createPaper("Перевести"));
        menu.setItem(50, createPaper("Перевести"));

        menu.setItem(45, createPaper("Назад"));
        setListOfPlayers(getAllOnlinePlayers(player), player, menu, "");

        UUID uuid = player.getUniqueId();
        playerPage.put(uuid, 0);
        playersMenus.put(uuid, menu);

        player.openInventory(menu);
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

    public static List<List<String>> getAllOnlinePlayers(Player mainPlayer) {
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
        int indexOfPlayerInSubList = playerPage.get(player.getUniqueId());
        System.out.println("Players size: "+players.size());
        if (Objects.equals(action, "plus") && indexOfPlayerInSubList+1 < players.size()) indexOfPlayerInSubList++;
        else if (Objects.equals(action, "minus") && indexOfPlayerInSubList-1 >= 0) indexOfPlayerInSubList--;

        // Перевірка виходу за межі
        if (indexOfPlayerInSubList < 0 || indexOfPlayerInSubList >= players.size()) return;

        System.out.println("Index of sublist: " + indexOfPlayerInSubList);

        List<String> subList = players.get(indexOfPlayerInSubList);
        System.out.println("Sublist: " + subList);

        // Очищаємо старі слоти, якщо потрібно
        for (int i = 1; i <= 7; i++) {
            menu.setItem(i, null);
        }

        // Додаємо гравців у слоти (з 1 по 7)
        for (int i = 0; i < subList.size(); i++) {
            menu.setItem(i + 1, createPaper("§eГравцю: " + subList.get(i)));
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
            String bankPath = "jdbc:sqlite:plugins/yourBank/yourBank.db";
            UUID uuid = player.getUniqueId();
            String[] formated = displayName.split(" ");
            if (displayName.startsWith("+")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(1);
                MenuInteraction.IncButtons(clickedInventory, Integer.parseInt(displayName), Material.DIAMOND_ORE);
            }
            else if (displayName.startsWith("-")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(1);
                MenuInteraction.DicButtons(clickedInventory, Integer.parseInt(displayName), Material.DIAMOND_ORE);
            }
            if (displayName.equals("§eПопередні")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                setListOfPlayers(getAllOnlinePlayers(player), player, playersMenus.get(uuid), "minus");

            }
            else if (displayName.equals("§eНаступні")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                setListOfPlayers(getAllOnlinePlayers(player), player, playersMenus.get(uuid), "plus");
            }

            else if (displayName.startsWith("§eГравцю:")) {
                playersMenus.get(uuid).setItem(40, createPaper(formated[1]));
            }
            else if (displayName.equals("Перевести")) {
                if (playersMenus.get(uuid).getItem(40) == null) {
                    player.sendMessage(ChatColor.RED+"[Банк] Вибери отримувача");
                    return;
                }
                int sum = MenuInteraction.getAmountFromSlots(playersMenus.get(uuid));
                if (sum == 0) {
                    player.sendMessage(ChatColor.RED+"[Банк] Введи суму");
                    return;
                }
                String[] data = Data.getPlayerData(player.getName());
                if (Integer.parseInt(data[1]) < sum) {
                    player.sendMessage(ChatColor.RED+"[Банк] Недостатньо коштів");
                    return;
                }
                String receiver = playersMenus.get(uuid).getItem(40).getItemMeta().getDisplayName();
                if (Data.getPlayer(receiver, bankPath, "Bank")) {
                    if (Data.makeTransaction(receiver, player.getName(), sum)) {
                        player.sendMessage(ChatColor.GREEN+"[Банк] Переведення коштів успішне");
                    }
                    else {
                        player.sendMessage(ChatColor.RED+"[Банк] У Вас недостатньо коштів");
                    }
                    player.closeInventory();
                }
                else {
                    event.getWhoClicked().sendMessage(ChatColor.RED+"[Банк] У цього гравця немає банківського рахунку");
                }
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