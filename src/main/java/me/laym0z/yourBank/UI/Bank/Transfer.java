package me.laym0z.yourBank.UI.Bank;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.UI.MenuComponents.Buttons.ChangeAmountButtons;
import me.laym0z.yourBank.UI.MenuComponents.Buttons.ListButtons;
import me.laym0z.yourBank.UI.MenuComponents.Buttons.TransferButtons;
import me.laym0z.yourBank.UI.MenuComponents.MenuInteraction;
import me.laym0z.yourBank.UI.Titles;
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

        Inventory menu = Bukkit.createInventory(null, 54, Titles.TRANSFER_TITLE);
        YourBank.pluginContext.transferManager.addPayCommissionChoose(player.getUniqueId(), false);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED+"Вимкнено!");

        Map<Integer, String> addButtons = TransferButtons.getAddButtons();
        Map <Integer, String> deductButtons = TransferButtons.getDeductButtons();
        Map <Integer, String> transferButtons = TransferButtons.getApplyButtons();

        for (Map.Entry<Integer, String> entry : addButtons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }
        for (Map.Entry<Integer, String> entry : deductButtons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }
        for (Map.Entry<Integer, String> entry : transferButtons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }
        menu.setItem(0, MenuInteraction.createPaper(ListButtons.getPrevButton()));
        menu.setItem(8, MenuInteraction.createPaper(ListButtons.getNextButton()));

        menu.setItem(45, MenuInteraction.createPaper(TransferButtons.getGoBackButton()));
        menu.setItem(52, MenuInteraction.createPaperLore(lore,TransferButtons.getCommissionChooseButton()));
        menu.setItem(52, MenuInteraction.createPaperLore(lore,ChatColor.GOLD+"Оплата комісії"));
        UUID uuid = player.getUniqueId();

        YourBank.getPluginContext().transferManager.setPlayerPage(uuid, 0);
        YourBank.getPluginContext().transferManager.setPlayerMenu(uuid, menu);

        MenuInteraction.setListOfPlayers(player, menu, MenuInteraction.listAction.NOTHING);

        player.openInventory(menu);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Database Database = new Database(YourBank.getDatabaseConnector());
        if (!(event.getWhoClicked() instanceof Player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getView().getTitle().equals(Titles.TRANSFER_TITLE)) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            String displayName = clickedItem.getItemMeta().getDisplayName();

            Player player = (Player) event.getWhoClicked();

            UUID uuid = player.getUniqueId();
            Inventory menu = YourBank.getPluginContext().transferManager.getPlayerMenu(uuid);
            if (ChatColor.stripColor(displayName).startsWith("+")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = ChatColor.stripColor(displayName);
                displayName = displayName.substring(1);
                ChangeAmountButtons.IncButtons(clickedInventory, Integer.parseInt(displayName), Material.DIAMOND_ORE);
            }
            else if (ChatColor.stripColor(displayName).startsWith("-")) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                displayName = ChatColor.stripColor(displayName);
                displayName = displayName.substring(1);
                ChangeAmountButtons.DicButtons(clickedInventory, Integer.parseInt(displayName), Material.DIAMOND_ORE);
            }
            if (displayName.equals(ListButtons.getPrevButton())) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                MenuInteraction.setListOfPlayers(player, menu, MenuInteraction.listAction.PREV);
            }
            else if (displayName.equals(ListButtons.getNextButton())) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                MenuInteraction.setListOfPlayers(player, menu, MenuInteraction.listAction.NEXT);
            }
            else if (ChatColor.stripColor(displayName).startsWith("Гравцю:")) {
                String[] formatedPlayer = displayName.split(" ");
                menu.setItem(40, MenuInteraction.createPaper(formatedPlayer[1]));
            }
            else if (displayName.equals(TransferButtons.getApplyButton())) {
                if (menu.getItem(40) == null) {
                    player.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                            ChatColor.RESET+ChatColor.RED+"Вибери отримувача");
                    return;
                }
                int sum = MenuInteraction.getAmountFromSlots(menu);
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

                String receiver = Objects.requireNonNull(Objects.requireNonNull(menu.getItem(40)).getItemMeta()).getDisplayName();

                if (Database.getPlayersBank(ChatColor.stripColor(receiver))) {
                    Boolean payCommission = YourBank.pluginContext.transferManager.getPayCommissionChoose(uuid);
                    if (Database.makeTransaction(ChatColor.stripColor(receiver), player.getName(), sum, payCommission)) {
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
                    event.getWhoClicked().sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                            ChatColor.RESET+ChatColor.RED+" У цього гравця немає банківського рахунку");
                }
            }
            else if (ChatColor.stripColor(displayName).equals("Оплата комісії")) {
                Boolean choose = YourBank.getPluginContext().transferManager.getPayCommissionChoose(uuid);
                YourBank.pluginContext.transferManager.addPayCommissionChoose(uuid, !choose);
                choose = !choose;
                ItemStack item =  event.getView().getItem(52);
                assert item != null;
                ItemMeta meta = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (choose) {
                    lore.add(ChatColor.GREEN+"Увімкнено!");
                }
                else {
                    lore.add(ChatColor.RED+"Вимкнено!");
                }
                assert meta != null;
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            else if (displayName.equals(TransferButtons.getGoBackButton())) {
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
        if (event.getView().getTitle().equals(Titles.TRANSFER_TITLE)) {
            YourBank.getPluginContext().transferManager.removePlayerPage(event.getPlayer().getUniqueId());
            YourBank.getPluginContext().transferManager.removePlayerMenu(event.getPlayer().getUniqueId());
            YourBank.getPluginContext().transferManager.removeFromPayCommissionChoose(event.getPlayer().getUniqueId());
        }

    }
}