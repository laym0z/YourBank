package me.laym0z.yourBank.UI.MenuComponents;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.YourBank;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MenuInteraction {

    static int[] incSlots = {10, 11, 12, 13, 14, 15, 16};
    static int[] dicSlots = {16, 15, 14, 13, 12, 11, 10};

    public static void IncButtons(Inventory inv, int amount, Material type) {

        for (int slot : incSlots) {
            ItemStack item = inv.getItem(slot);

            // Порожній слот
            if (item == null || item.getType() == Material.AIR) {
                int toAdd = Math.min(amount, 64);
                inv.setItem(slot, new ItemStack(type, toAdd));
                amount -= toAdd;
            }

            // Якщо вже є діаманти
            else if (item.getType() == type) {
                int current = item.getAmount();
                int space = 64 - current;

                if (space > 0) {
                    int toAdd = Math.min(space, amount);
                    item.setAmount(current + toAdd);
                    amount -= toAdd;
                }
            }

            // Перевірка чи додано все
            if (amount <= 0) break;
        }
    }

    public static void DicButtons(Inventory inv, int amount, Material type){

        for (int slot : dicSlots) {
            ItemStack item = inv.getItem(slot);

            if (item == null || item.getType() != type) {
                continue; // Порожній слот або не діамант
            }

            int current = item.getAmount();

            if (current <= amount) {
                amount -= current;
                inv.setItem(slot, null);
            } else {
                item.setAmount(current - amount);
                amount = 0;
            }

            if (amount <= 0) break;
        }
    }

    public static void Convert(Inventory inv, Material type) {
        for (int slot : incSlots) {
            if (inv.getItem(slot) == null) return;
            int amount = Objects.requireNonNull(inv.getItem(slot)).getAmount();
            inv.setItem(slot, new ItemStack(type, amount));
        }
    }

    public static int getAmountFromSlots(Inventory menu) {
        int amount = 0;
        for (int slot : incSlots) {
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
        for (int slot : incSlots) {
            ItemStack item = menu.getItem(slot);
            if (item != null) amount += item.getAmount();
            else break;
        }
        if (amount ==1 && type == Material.DEEPSLATE_DIAMOND_ORE) {
            result.put(0, "[Банк] Кількість ллибинної діамантової рудимає бути як мінімум 2");
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
        /*
        перебрати всі речі в інвентраі гравця та знайти діаманти за типом та запам'ятати індекс слоту
        якщо діамантів мало, вивести помилку

        ---

        якщо діамантів вистачило

        забрати з записаних слотів потрібну кількість
            якщо потреба більше, ніж в слоті - почистити слот та відняти ту суму слоту від потреби
            якщо потреба менше, ніж в слоті - від слота віднімається потреба, а потреба дорівнює нулю

        Відбувається запит до бази даних на оновлення кількості віртуальної валюти (Реалізувати метод в Database)
        */
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
        for (int slot : incSlots) {
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


}
