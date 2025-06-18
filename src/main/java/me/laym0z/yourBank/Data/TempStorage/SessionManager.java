package me.laym0z.yourBank.Data.TempStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private final Map<UUID, String> adminAndReceiver = new HashMap<>();

    public void setReceiver(UUID uuid, String receiverName) {
        adminAndReceiver.put(uuid, receiverName);
    }

    public String getReceiver(UUID uuid) {
        return adminAndReceiver.get(uuid);
    }

    public boolean hasReceiver(UUID uuid) {
        return adminAndReceiver.containsKey(uuid);
    }

    public void removeReceiver(UUID uuid) {
        adminAndReceiver.remove(uuid);
    }

    //--------------------PRINTS-------------------------------

    public void printAll() {
        System.out.println("adminAndReceiver: "+adminAndReceiver);
    }
}
