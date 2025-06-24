package me.laym0z.yourBank.UI.Bank;

import me.laym0z.yourBank.Data.TempStorage.SQLQueries.Data;
import me.laym0z.yourBank.UI.Penalty.Penalties;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BankMain implements Listener {
    public static void openBankMenu(Player player, String[] data) {
        Inventory menu = Bukkit.createInventory(null, 54, "Банк");

        List<List<String>> names = Data.getTopPlayers();
        // Створення предметів
        menu.setItem(1, MenuInteraction.createPaper(format(Integer.parseInt(data[1]) , "ДР"))); // diamond_ore
        menu.setItem(6, createTopPlayers(names));// top 3 players
        menu.setItem(8, MenuInteraction.createPaper("NONE"));//top 3 cities

        menu.setItem(20, MenuInteraction.createPaper(ChatColor.GREEN+""+ChatColor.BOLD+"[\uD83D\uDCE7] Переказ"));
        menu.setItem(21, MenuInteraction.createPaper(ChatColor.GREEN+""+ChatColor.BOLD+"[\uD83D\uDCE7] Переказ"));
        menu.setItem(29, MenuInteraction.createPaper(ChatColor.GREEN+""+ChatColor.BOLD+"[\uD83D\uDCE7] Переказ"));
        menu.setItem(30, MenuInteraction.createPaper(ChatColor.GREEN+""+ChatColor.BOLD+"[\uD83D\uDCE7] Переказ"));

        menu.setItem(23, MenuInteraction.createPaper(ChatColor.DARK_RED+""+ChatColor.BOLD+"[-] Штрафи"));
        menu.setItem(24, MenuInteraction.createPaper(ChatColor.DARK_RED+""+ChatColor.BOLD+"[-] Штрафи"));
        menu.setItem(32, MenuInteraction.createPaper(ChatColor.DARK_RED+""+ChatColor.BOLD+"[-] Штрафи"));
        menu.setItem(33, MenuInteraction.createPaper(ChatColor.DARK_RED+""+ChatColor.BOLD+"[-] Штрафи"));

        String dateOfCreate = data[2];
        menu.setItem(3, MenuInteraction.createPaper(ChatColor.GOLD+"Дата створення: "+ChatColor.WHITE+dateOfCreate));

        player.openInventory(menu);

    }
    private static ItemStack createTopPlayers(List<List<String>> names) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Топ 3:"); // §e — жовтий текст
            for (int i = 0; i < 3; i++) {
                if (i < names.size()) {
                    lore.add(ChatColor.WHITE+""+(i+1)+". "+names.get(i).get(0)+": "+names.get(i).get(1)+" ДР");
                }
                else {
                    lore.add(ChatColor.GRAY+"-");
                }
            }
            meta.setLore(lore);
            paper.setItemMeta(meta);
        }
        return paper;
    }

    public static String format(int amount, String type) {
        int t = amount % 64;
        if (t != 0) {
            if (amount <=64) {
                return ChatColor.BOLD+""+ChatColor.GOLD+amount+" "+ChatColor.WHITE+type;
            }
            int stacks = (amount-(amount % 64))/64;
            return ChatColor.BOLD+""+ ChatColor.GOLD+amount+ChatColor.WHITE+" "+type+ChatColor.GOLD+" | "+stacks+ChatColor.WHITE+" ст. та "+
                    ChatColor.GOLD+t+ChatColor.WHITE+" "+type;
        }

        return ChatColor.BOLD+""+ChatColor.GOLD+amount+" "+ChatColor.WHITE+type+ChatColor.GOLD+" | "+amount/64+ChatColor.WHITE+" ст. ";
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getView().getTitle().equals("Банк")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            String displayName = Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName();
            if (ChatColor.stripColor(displayName).equals("[\uD83D\uDCE7] Переказ")) {
                if (Data.isPlayerBlocked(player.getName())) {
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
            else if (ChatColor.stripColor(displayName).equals("[-] Штрафи")) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(YourBank.getInstance(), () -> {
                    Penalties.openPenaltyListMenu(player, player.getName(),false);
                }, 1L); // 1 тік затримки
            }
        }
    }
}
