package me.laym0z.yourBank.Data;

import me.laym0z.yourBank.Test.PrintHashMaps;
import me.laym0z.yourBank.YourBank;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ClearOnLeave implements Listener {
    @EventHandler
    public void clearOnLeave(PlayerQuitEvent event) {
        YourBank.pluginContext.penaltiesManager.removeFromIDToRemove(event.getPlayer().getUniqueId());
        YourBank.pluginContext.penaltiesManager.removePlayerPenalty(event.getPlayer().getUniqueId());
        YourBank.pluginContext.penaltiesManager.removeFromToRemoveList(event.getPlayer().getUniqueId());
        YourBank.pluginContext.penaltiesManager.removePlayerPage(event.getPlayer().getUniqueId());
        YourBank.pluginContext.penaltiesManager.removePlayerMenu(event.getPlayer().getUniqueId());
        YourBank.pluginContext.penaltiesManager.removePenaltiesPerPlayer(event.getPlayer().getUniqueId());

        YourBank.pluginContext.transferManager.removePlayerPage(event.getPlayer().getUniqueId());
        YourBank.pluginContext.transferManager.removePlayerMenu(event.getPlayer().getUniqueId());

        YourBank.pluginContext.sessionManager.removeReceiver(event.getPlayer().getUniqueId());
        PrintHashMaps.printHashMapsInfo();
    }
}
