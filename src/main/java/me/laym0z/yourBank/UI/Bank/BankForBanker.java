package me.laym0z.yourBank.UI.Bank;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.UI.MenuComponents.Buttons.BankForBankerButtons;
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

import java.util.Map;
import java.util.Objects;

public class BankForBanker implements Listener {
    public static void openBankForBankerMenu(Player banker, String owner) {

        Database Database = new Database(YourBank.getDatabaseConnector());
        String[] data = Database.getPlayerData(owner);
        Map<Integer, String> buttons = BankForBankerButtons.get();

        Inventory menu = Bukkit.createInventory(null, 36, Titles.BANKER_MENU_TITLE);

        menu.setItem(4, MenuInteraction.createPaper(MenuInteraction.formatAmountOfDiamonds(Integer.parseInt(data[1]), "ДР")));

        for (Map.Entry<Integer, String> entry : buttons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }

        YourBank.getPluginContext().sessionManager.setReceiver(banker.getUniqueId(), owner);
        banker.openInventory(menu);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getView().getTitle().equals(Titles.BANKER_MENU_TITLE)) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            String displayName = Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName();

            if (displayName.equals(BankForBankerButtons.getDepositButton())) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> Deposit.openDepositMenu(player), 1L); // 1 тік затримки
            }

            else if (displayName.equals(BankForBankerButtons.getWithdrawButton())) {
                Database Database = new Database(YourBank.getDatabaseConnector());
                if (Database.isPlayerBlocked(player.getName())) {
                    player.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                                    ChatColor.RESET+ChatColor.RED+" Операція заблокована через велику кількість несплачених штрафів");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    return;
                }
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> Withdraw.openWithdrawMenu(player), 1L); // 1 тік затримки
            }
        }
    }

}