package me.laym0z.yourBank.Data.TempStorage;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransferManager {
    private final Map<UUID, Integer> playerPage = new HashMap<>();
    private final HashMap<UUID, Inventory> playerMenus = new HashMap<>();
    private final HashMap<UUID, Boolean> payCommission = new HashMap<>();

    public void setPlayerPage(UUID uuid, int page) {
        playerPage.put(uuid, page);
    }

    public int getPlayerPage(UUID uuid) {
        return playerPage.get(uuid);
    }

    public void setPlayerMenu(UUID uuid, Inventory menu) {
        playerMenus.put(uuid, menu);
    }

    public Inventory getPlayerMenu(UUID uuid) {
        return playerMenus.get(uuid);
    }

    public void removePlayerPage(UUID uuid) {
        playerPage.remove(uuid);
    }

    public void removePlayerMenu(UUID uuid) {
        playerMenus.remove(uuid);
    }

    public void addPayCommissionChoose(UUID uuid, Boolean choose) {
        payCommission.put(uuid, choose);
    }

    public Boolean getPayCommissionChoose(UUID uuid) {
        return payCommission.get(uuid);
    }

    public void removeFromPayCommissionChoose(UUID uuid) {
        payCommission.remove(uuid);
    }

    //--------------------PRINTS-------------------------------

    public void printAll() {
        System.out.println("playerPage: "+playerPage);
        System.out.println("playerMenus: "+playerMenus);
        System.out.println("payCommission: "+payCommission);
    }
}
