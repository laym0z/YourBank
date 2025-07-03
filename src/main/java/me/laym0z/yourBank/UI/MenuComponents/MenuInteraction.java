package me.laym0z.yourBank.UI.MenuComponents;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.YourBank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MenuInteraction {

    static int[] slots = {10, 11, 12, 13, 14, 15, 16};

    public enum listAction {
        NEXT,
        PREV,
        NOTHING
    }

    public static void Convert(Inventory inv, Material type) {
        for (int slot : slots) {
            if (inv.getItem(slot) == null) return;
            int amount = Objects.requireNonNull(inv.getItem(slot)).getAmount();
            inv.setItem(slot, new ItemStack(type, amount));
        }
    }

    public static int getAmountFromSlots(Inventory menu) {
        int amount = 0;
        for (int slot : slots) {
            ItemStack item = menu.getItem(slot);
            if (item != null) amount += item.getAmount();
            else break;
        }
        return  amount;
    }

    public static HashMap<Integer, String> ConfirmDeposit(Inventory menu, Inventory playerInv, Material type, String name) {
        Database Database = new Database(YourBank.getDatabaseConnector());
        HashMap <Integer, String> result = new HashMap<>();
        if (menu.getItem(10) == null) {
            result.put(0, "[Банк] Введіть суму");
            return result;
        }
        int amount = 0;
        for (int slot : slots) {
            ItemStack item = menu.getItem(slot);
            if (item != null) amount += item.getAmount();
            else break;
        }
        if (amount ==1 && type == Material.DEEPSLATE_DIAMOND_ORE) {
            result.put(0, "[Банк] Кількість глибинної діамантової руди має бути як мінімум 2");
        }
        if (type==Material.DEEPSLATE_DIAMOND_ORE && amount%2==1) amount -= 1;
        System.out.println("Amount: "+amount);
        ArrayList <Integer> indexes = new ArrayList<>();
        int itemIndex = 0;
        int sum = 0;
        for (ItemStack item : playerInv.getContents()) {
            if (item != null && item.getType() == type) {
                indexes.add(itemIndex);
                sum += item.getAmount();

            }
            itemIndex++;
            if (sum >= amount) break;
        }
        System.out.println("Sum: "+sum);

        if (sum < amount) {
            result.put(0, "[Банк] Недостатньо коштів");
            return result;
        }
        int i = 0;
        System.out.println(indexes);
        int temp = amount;
        while (amount > 0) {
            int itemAmount = Objects.requireNonNull(playerInv.getItem(indexes.get(i))).getAmount();
            if (amount >= itemAmount) {
                amount -= itemAmount;
                playerInv.setItem(indexes.get(i), null);
            }
            else {
                Objects.requireNonNull(playerInv.getItem(indexes.get(i))).setAmount(itemAmount-amount);
                amount = 0;
            }
            i++;
        }
        String typeString = "";
        if (type == Material.DEEPSLATE_DIAMOND_ORE) typeString="deep_diamonds";
        else if (type == Material.DIAMOND_ORE) typeString="diamonds";

        result = Database.addToBalance(typeString, name, temp);
        return result;
    }

    public static HashMap<Integer, String> ConfirmWithdraw(Inventory menu, Inventory playerInv, Material type, String name) {
        Database Database = new Database(YourBank.getDatabaseConnector());
        HashMap <Integer, String> result = new HashMap<>();
        ArrayList<Integer> playersSlots = new ArrayList<>();
        int slotsCount = 0;
        int menuCount = 0;
        int amount = 0;
        if (menu.getItem(10) == null) {
            result.put(0, "[Банк] Введіть суму");
            return result;
        }
        for (int slot : slots) {
            ItemStack item = menu.getItem(slot);
            if (item != null && item.getType()==type) {
                menuCount++;
                amount+=item.getAmount();
            }
            else break;
        }
        if (type==Material.DEEPSLATE_DIAMOND_ORE && amount%2==1) amount -= 1;
        int slotIndex = 0;
        ArrayList<Integer>blockedSlots = new ArrayList<>();
        blockedSlots.add(39);
        blockedSlots.add(38);
        blockedSlots.add(37);
        blockedSlots.add(36);
        blockedSlots.add(-106);
        for (ItemStack item : playerInv.getContents()) {
            if (item == null && !blockedSlots.contains(slotIndex)) {
                slotsCount++;
                playersSlots.add(slotIndex);
            }
            if (slotsCount >= menuCount) break;
            slotIndex++;
        }
        System.out.println("Slots: "+playersSlots);
        if (slotsCount < menuCount) {
            result.put(0, "Недостатньо місця в інвентарі");
            return result;
        }
        String typeString="";
        if (type == Material.DEEPSLATE_DIAMOND_ORE) typeString="deep_diamonds";
        else if (type == Material.DIAMOND_ORE) typeString="diamonds";
        result = Database.deductFromBalance(typeString, name, amount);
        if (result.containsKey(0)) return result;
        ItemStack item = new ItemStack(type);
        for (Integer playersSlot : playersSlots) {
            if (amount > 64) {
                item.setAmount(64);
                amount -= 64;
            } else item.setAmount(amount);
            playerInv.setItem(playersSlot, item);
        }
        return result;
    }

    public static ItemStack createPaper(String name) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name); // §e — жовтий текст

            paper.setItemMeta(meta);
        }
        return paper;
    }

    public static ItemStack createPaperLore(List<String> lore, String name) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            paper.setItemMeta(meta);
        }
        return paper;
    }

    public static String formatAmountOfDiamonds(int amount, String type) {
        int t = amount % 64;
        if (t != 0) {
            int stacks = (amount-(amount % 64))/64;
            return amount+" "+type+" | "+stacks+" ст. та "+t+" "+type;
        }
        return amount+" "+type+" | "+amount/64+" ст. ";
    }

    public static ItemStack createTopPlayers(List<List<String>> names) {
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

    public static void setListOfPlayers(Player player, Inventory menu, listAction action) {
        List<List<String>> players = getAllBankUsers(player);
        int indexOfPlayerInSubList = YourBank.getPluginContext().transferManager.getPlayerPage(player.getUniqueId());
        if (Objects.equals(action, listAction.NEXT) && indexOfPlayerInSubList+1 < players.size()) indexOfPlayerInSubList++;
        else if (Objects.equals(action, listAction.PREV) && indexOfPlayerInSubList-1 >= 0) indexOfPlayerInSubList--;

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

    public static void displayPenalties(Inventory menu, listAction action, UUID uuid) {
        List<List<String>> penalties = YourBank.getPluginContext().penaltiesManager.getPenaltiesPerPlayer(uuid);
        int index = YourBank.getPluginContext().penaltiesManager.getPlayerPage(uuid);

        if (Objects.equals(action, listAction.PREV) && index - 7 >= 0) {
            index-=7;
            YourBank.getPluginContext().penaltiesManager.setPlayerPage(uuid, index);
        }
        else if (Objects.equals(action, listAction.NEXT) && index + 7 < penalties.size()) {
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
}
