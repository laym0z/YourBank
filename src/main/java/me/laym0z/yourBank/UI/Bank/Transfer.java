package me.laym0z.yourBank.UI.Bank;

import me.laym0z.yourBank.Data.DB.Database;
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
        YourBank.pluginContext.transferManager.addPayCommissionChoose(player.getUniqueId(), false);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED+"Вимкнено!");
        //Додати
        menu.setItem(20, MenuInteraction.createPaper(ChatColor.GREEN+"+1"));
        menu.setItem(21, MenuInteraction.createPaper(ChatColor.GREEN+"+4"));
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

        menu.setItem(0, MenuInteraction.createPaper(ChatColor.GOLD+"[←] Попередні"));
        menu.setItem(8, MenuInteraction.createPaper(ChatColor.GOLD+"[→] Наступні"));

        //підтвердити
        menu.setItem(48, MenuInteraction.createPaper(ChatColor.GREEN+""+ChatColor.BOLD+"[\uD83D\uDCE7] Перевести"));
        menu.setItem(49, MenuInteraction.createPaper(ChatColor.GREEN+""+ChatColor.BOLD+"[\uD83D\uDCE7] Перевести"));
        menu.setItem(50, MenuInteraction.createPaper(ChatColor.GREEN+""+ChatColor.BOLD+"[\uD83D\uDCE7] Перевести"));

        menu.setItem(45, MenuInteraction.createPaper(ChatColor.GRAY+"[↓] Назад"));

        menu.setItem(52, MenuInteraction.createPaperLore(lore,ChatColor.GOLD+"Оплата комісії"));

        UUID uuid = player.getUniqueId();

        YourBank.getPluginContext().transferManager.setPlayerPage(uuid, 0);
        YourBank.getPluginContext().transferManager.setPlayerMenu(uuid, menu);
        setListOfPlayers(getAllBankUsers(player), player, menu, "");

        player.openInventory(menu);
    }


    public static List<List<String>> getAllBankUsers(Player mainPlayer) {
        Database Database = new Database(YourBank.getDatabaseConnector());
        List <String> allNames = Database.getAllPlayers(mainPlayer.getName());
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
            menu.setItem(i + 1, MenuInteraction.createPaper(ChatColor.GOLD+ "Гравцю: " + ChatColor.WHITE+subList.get(i)));
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Database Database = new Database(YourBank.getDatabaseConnector());
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
            if (displayName.startsWith(ChatColor.GREEN+"+")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(3);
                MenuInteraction.IncButtons(clickedInventory, Integer.parseInt(displayName), Material.DIAMOND_ORE);
            }
            else if (displayName.startsWith(ChatColor.YELLOW+"-")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = displayName.substring(3);
                MenuInteraction.DicButtons(clickedInventory, Integer.parseInt(displayName), Material.DIAMOND_ORE);
            }
            if (ChatColor.stripColor(displayName).equals("[←] Попередні")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                setListOfPlayers(getAllBankUsers(player), player, YourBank.getPluginContext().transferManager.getPlayerMenu(uuid), "minus");

            }
            else if (ChatColor.stripColor(displayName).equals("[→] Наступні")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                setListOfPlayers(getAllBankUsers(player), player, YourBank.getPluginContext().transferManager.getPlayerMenu(uuid), "plus");
            }

            else if (displayName.startsWith(ChatColor.GOLD+"Гравцю:")) {
                YourBank.getPluginContext().transferManager.getPlayerMenu(uuid).setItem(40, MenuInteraction.createPaper(formated[1]));
            }
            else if (ChatColor.stripColor(displayName).equals("[\uD83D\uDCE7] Перевести")) {
                if (YourBank.getPluginContext().transferManager.getPlayerMenu(uuid).getItem(40) == null) {
                    player.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                            ChatColor.RESET+ChatColor.RED+"Вибери отримувача");
                    return;
                }
                int sum = MenuInteraction.getAmountFromSlots(YourBank.getPluginContext().transferManager.getPlayerMenu(uuid));
                if (sum == 0) {
                    player.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                            ChatColor.RESET+ChatColor.RED+" Введи суму");
                    return;
                }
                String[] data = Database.getPlayerData(player.getName());
                if (Integer.parseInt(data[1]) < sum) {
                    player.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                            ChatColor.RESET+ChatColor.RED+" Недостатньо коштів");
                    return;
                }

                String receiver = Objects.requireNonNull(Objects.requireNonNull(YourBank.getPluginContext().transferManager
                        .getPlayerMenu(uuid).getItem(40)).getItemMeta()).getDisplayName();

                if (Database.getPlayersBank(receiver.replace("§f", ""))) {
                    Boolean payCommission = YourBank.pluginContext.transferManager.getPayCommissionChoose(uuid);
                    if (Database.makeTransaction(receiver.replace("§f", ""), player.getName(), sum, payCommission)) {
                        player.sendMessage(ChatColor.DARK_GREEN+""+ChatColor.BOLD+ "[Банк]"+
                                ChatColor.RESET+ChatColor.GREEN+" Переведення коштів успішне");
                    }
                    else {
                        player.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                                ChatColor.RESET+ChatColor.RED+" Недостатньо коштів");
                    }
                    player.closeInventory();
                }
                else {
                    System.out.println("receiver: "+receiver);
                    event.getWhoClicked().sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                            ChatColor.RESET+ChatColor.RED+" У цього гравця немає банківського рахунку");
                }
            }
            else if (ChatColor.stripColor(displayName).equals("Оплата комісії")) {
                Boolean choose = YourBank.getPluginContext().transferManager.getPayCommissionChoose(uuid);
                YourBank.pluginContext.transferManager.addPayCommissionChoose(
                        uuid,
                        !choose
                );
                choose = !choose;
                ItemStack item =  event.getView().getItem(52);
                ItemMeta meta = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (choose) {
                    lore.add(ChatColor.GREEN+"Увімкнено!");
                    assert meta != null;
                    meta.setLore(lore);
                }
                else {
                    lore.add(ChatColor.RED+"Вимкнено!");
                    meta.setLore(lore);
                }
                item.setItemMeta(meta);
            }
            else if (ChatColor.stripColor(displayName).equals("[↓] Назад")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    BankMain.openBankMenu(player, Database.getPlayerData(player.getName()));
                }, 1L); // 1 тік затримки
            }
        }
    }
    @EventHandler
    public static void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Переведення")) {
            YourBank.getPluginContext().transferManager.removePlayerPage(event.getPlayer().getUniqueId());
            YourBank.getPluginContext().transferManager.removePlayerMenu(event.getPlayer().getUniqueId());
            YourBank.getPluginContext().transferManager.removeFromPayCommissionChoose(event.getPlayer().getUniqueId());
        }

    }
}