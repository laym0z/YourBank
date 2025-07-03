package me.laym0z.yourBank.UI.Bank;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.UI.MenuComponents.Buttons.BankMainButtons;
import me.laym0z.yourBank.UI.Penalty.Penalties;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BankMain implements Listener {
    public static void openBankMenu(Player player, String[] data) {
        Database Database = new Database(YourBank.getDatabaseConnector());

        Inventory menu = Bukkit.createInventory(null, 54, Titles.BANK_TITLE);
        String dateOfCreate = data[2];
        List<List<String>> names = Database.getTopPlayers();


        menu.setItem(1, MenuInteraction.createPaper(MenuInteraction.formatAmountOfDiamonds(Integer.parseInt(data[1]) , "ДР"))); // diamond_ore
        menu.setItem(6, MenuInteraction.createTopPlayers(names));// top 3 players
        Map<Integer, String> buttons = BankMainButtons.get();

        for (Map.Entry<Integer, String> entry : buttons.entrySet()) {
            menu.setItem(entry.getKey(), MenuInteraction.createPaper(entry.getValue()));
        }

        menu.setItem(3, MenuInteraction.createPaper(ChatColor.GOLD+"Дата створення: "+ChatColor.WHITE+dateOfCreate));

        player.openInventory(menu);

    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if (event.getView().getTitle().equals(Titles.BANK_TITLE)) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            String displayName = Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName();


            if (displayName.equals(BankMainButtons.getTransferButton())) {
                Database Database = new Database(YourBank.getDatabaseConnector());
                if (Database.isPlayerBlocked(player.getName())) {
                    player.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Банк]"+
                            ChatColor.RESET+ChatColor.RED+" Можливість переказів заблокована через несплату штрафів");

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    return;
                }
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    Transfer.openTransferMenu(player);
                }, 1L); // 1 тік затримки
            }
            else if (displayName.equals(BankMainButtons.getPenaltiesButton())) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    Penalties.openPenaltyListMenu(player, player.getName(),false);
                }, 1L); // 1 тік затримки
            }
        }
    }
}
