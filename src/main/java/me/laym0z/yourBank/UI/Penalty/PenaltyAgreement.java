package me.laym0z.yourBank.UI.Penalty;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.UI.Bank.BankMain;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PenaltyAgreement implements Listener {
    public static void openPenaltyAgreementMenu(int ID, int sum, Player player, String receiver) {
        List<String> temp = new ArrayList<>();
        temp.add(String.valueOf(ID));
        temp.add(String.valueOf(sum));
        temp.add(player.getName());
        temp.add(receiver);
        YourBank.getPluginContext().penaltiesManager.setPlayerPenalty(player.getUniqueId(), temp);

        Inventory menu = Bukkit.createInventory(null, 54, Titles.PAY_PENALTY_TITLE);

        Map<Integer, String> confirmButtons = PenaltiesButtons.getConfirmButtons();
        Map<Integer, String> cancelButtons = PenaltiesButtons.getCancelButtons();

        for (Map.Entry<Integer, String> entry : confirmButtons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }
        for (Map.Entry<Integer, String> entry : cancelButtons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }

        player.openInventory(menu);
    }
    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Database Database = new Database(YourBank.getDatabaseConnector());
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if (event.getView().getTitle().equals(Titles.PAY_PENALTY_TITLE)) {
            event.setCancelled(true);

            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            if (displayName.equals(PenaltiesButtons.getConfirmButton())) {
                List<String> info = YourBank.getPluginContext().penaltiesManager.getPlayerPenalty(player.getUniqueId());
                boolean result = Database.payPenalty(Integer.parseInt(info.get(0)), Integer.parseInt(info.get(1)), info.get(2), info.get(3));
                if (result) {
                    player.sendMessage(ChatColor.DARK_GREEN+""+ChatColor.BOLD+ "[Банк]"+
                            ChatColor.RESET+ChatColor.GREEN+" Штраф успішно оплачено");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    player.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                            ChatColor.RESET+ChatColor.RED+" Не вистачає коштів для оплати");
                }
                player.closeInventory();
            }
            else if (displayName.equals(PenaltiesButtons.getCancelButton())) {
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    BankMain.openBankMenu(player, Database.getPlayerData(player.getName()));
                    YourBank.pluginContext.penaltiesManager.removePlayerPenalty(player.getUniqueId());
                }, 1L); // 1 тік затримки
            }
        }
    }
    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(Titles.PAY_PENALTY_TITLE)) {
            Player player = (Player) event.getPlayer();
            YourBank.pluginContext.penaltiesManager.removePlayerPenalty(player.getUniqueId());
        }
    }
}
