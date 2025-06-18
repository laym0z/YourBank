package me.laym0z.yourBank.Data.TempStorage;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.UUID;

public class DiamondChoose {
    private final HashMap<UUID, Material> choose = new HashMap<>();

    public void setChoose(UUID uuid, Material diamond) {
        choose.put(uuid, diamond);
    }

    public Material getChoose(UUID uuid) {
        return choose.get(uuid);
    }

    public void removeChoose(UUID uuid) {
        choose.remove(uuid);
    }

    //--------------------PRINTS-------------------------------

    public void printAll() {
        System.out.println("diamondChoose: "+choose);
    }

}
