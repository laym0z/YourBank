package me.laym0z.yourBank;

import me.laym0z.yourBank.Data.CreateBankTable;
import me.laym0z.yourBank.Data.CreatePenaltiesTable;
import me.laym0z.yourBank.UI.*;
import me.laym0z.yourBank.commands.Bank;
import me.laym0z.yourBank.commands.BankCreate;
import me.laym0z.yourBank.commands.BankMenu;
import me.laym0z.yourBank.commands.Penalty;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class YourBank extends JavaPlugin implements Listener {
    static YourBank instance;
    @Override
    public void onEnable() {
        String path = "jdbc:sqlite:plugins/yourBank/yourBank.db";
        CreateBankTable.Create(path);
        CreatePenaltiesTable.Create(path);
        instance = this;
        Objects.requireNonNull(this.getCommand("bankcreate")).setExecutor(new BankCreate());
        Objects.requireNonNull(this.getCommand("bank")).setExecutor(new Bank());
        Objects.requireNonNull(this.getCommand("penalty")).setExecutor(new Penalty());
        Objects.requireNonNull(this.getCommand("bankmenu")).setExecutor(new BankMenu());
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new BankMain(), this);
        getServer().getPluginManager().registerEvents(new Deposit(), this);
        getServer().getPluginManager().registerEvents(new Withdraw(), this);
        getServer().getPluginManager().registerEvents(new Transfer(), this);
        getServer().getPluginManager().registerEvents(new Penalties(), this);
        getServer().getPluginManager().registerEvents(new PenaltyAgreement(), this);
        getServer().getPluginManager().registerEvents(new BankForAdmin(), this);

    }
    public static YourBank getInstance() {
        return instance;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
