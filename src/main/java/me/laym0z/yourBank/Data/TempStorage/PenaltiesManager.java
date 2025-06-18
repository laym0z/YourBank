package me.laym0z.yourBank.Data.TempStorage;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PenaltiesManager {

    private final HashMap<UUID, Integer> playerPage = new HashMap<>();
    private final Map<UUID, Inventory> playerMenu = new HashMap<>();
    private final Map<UUID, List<List<String>>> penaltiesPerPlayer = new HashMap<>();
    private final HashMap<UUID, List<String>> playerPenalty = new HashMap<>();
    private final HashMap<UUID, Boolean> toRemove = new HashMap<>();
    private final HashMap<UUID, Integer> IDToRemove = new HashMap<>();


    //------------------PAGES MANAGER-------------------

    public void setPlayerPage(UUID uuid, int page) {
        playerPage.put(uuid, page);
    }

    public int getPlayerPage(UUID uuid) {
        return playerPage.get(uuid);
    }

    //------------------MENUS MANAGER---------------------

    public void setPlayerMenu(UUID uuid, Inventory menu) {
        playerMenu.put(uuid, menu);
    }

    public Inventory getPlayerMenu(UUID uuid) {
        return playerMenu.get(uuid);
    }


    //--------------------PENALTIES-----------------------

    public void setPenaltiesPerPlayer(UUID uuid, List<List<String>> penalties) {
        penaltiesPerPlayer.put(uuid, penalties);
    }

    public List<List<String>> getPenaltiesPerPlayer(UUID uuid) {
        return penaltiesPerPlayer.get(uuid);
    }

    public void setPlayerPenalty(UUID uuid, List<String> penaltyData) {
        playerPenalty.put(uuid, penaltyData);
    }

    public List<String> getPlayerPenalty(UUID uuid) {
        return playerPenalty.get(uuid);
    }

    public void setToRemove(UUID uuid) {
        toRemove.put(uuid, true);
    }

    public boolean getToRemove(UUID uuid) {
        return toRemove.get(uuid);
    }

    public void setIDToRemove(UUID uuid, int ID) {
        IDToRemove.put(uuid, ID);
    }

    public int getIDToRemove(UUID uuid) { return IDToRemove.get(uuid); }

    //------------------ CLEAR RAM-----------------------------

    public void removePlayerPage(UUID uuid) {
        playerPage.remove(uuid);
    }

    public void removePlayerMenu(UUID uuid) {
        playerMenu.remove(uuid);
    }

    public void removePenaltiesPerPlayer(UUID uuid) {
        penaltiesPerPlayer.remove(uuid);
    }

    public void removePlayerPenalty(UUID uuid) {
        playerPenalty.remove(uuid);
    }

    public void removeFromToRemoveList(UUID uuid) {
        toRemove.remove(uuid);
    }

    public void removeFromIDToRemove(UUID uuid) {IDToRemove.remove(uuid);}

    //--------------------PRINTS-------------------------------

    public void printAll() {
        System.out.println("PlayerMenu: "+playerMenu);
        System.out.println("PlayerPage: "+playerPage);
        System.out.println("penaltiesPerPlayer: "+penaltiesPerPlayer);
        System.out.println("playerPenalty: "+playerPenalty);
        System.out.println("toRemove: "+toRemove);
        System.out.println("IDToRemove: "+IDToRemove);
    }

}

