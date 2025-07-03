package me.laym0z.yourBank.UI.MenuComponents.Buttons;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChangeAmountButtons {
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
}
