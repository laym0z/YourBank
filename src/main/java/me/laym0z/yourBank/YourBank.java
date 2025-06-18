package me.laym0z.yourBank;

import me.laym0z.yourBank.Data.CreateBankTable;
import me.laym0z.yourBank.Data.CreatePenaltiesTable;
import me.laym0z.yourBank.Data.PluginContext;
import me.laym0z.yourBank.Data.ClearOnLeave;
import me.laym0z.yourBank.Test.BankDebug;
import me.laym0z.yourBank.UI.Bank.*;
import me.laym0z.yourBank.UI.MenuComponents.WithdrawAndDeposit;
import me.laym0z.yourBank.UI.Penalty.Penalties;
import me.laym0z.yourBank.UI.Penalty.PenaltyAgreement;
import me.laym0z.yourBank.UI.Penalty.RemovePenaltyAgreement;
import me.laym0z.yourBank.commands.Bank;
import me.laym0z.yourBank.commands.BankCreate;
import me.laym0z.yourBank.commands.BankMenu;
import me.laym0z.yourBank.commands.Penalty;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class YourBank extends JavaPlugin implements Listener {
    static YourBank instance;
    public static PluginContext pluginContext;
    @Override
    public void onEnable() {
        pluginContext = new PluginContext();
        instance = this;

        String path = "jdbc:sqlite:plugins/yourBank/yourBank.db";
        CreateBankTable.Create(path);
        CreatePenaltiesTable.Create(path);

        //--------------------------COMMANDS---------------------------------

        Objects.requireNonNull(this.getCommand("bankcreate")).setExecutor(new BankCreate());
        Objects.requireNonNull(this.getCommand("bank")).setExecutor(new Bank());
        Objects.requireNonNull(this.getCommand("penalty")).setExecutor(new Penalty());
        Objects.requireNonNull(this.getCommand("bankmenu")).setExecutor(new BankMenu());
        Objects.requireNonNull(this.getCommand("bankdebug")).setExecutor(new BankDebug());

        //---------------------------LISTENERS---------------------------------

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new BankMain(), this);
        getServer().getPluginManager().registerEvents(new Deposit(), this);
        getServer().getPluginManager().registerEvents(new Withdraw(), this);
        getServer().getPluginManager().registerEvents(new Transfer(), this);
        getServer().getPluginManager().registerEvents(new Penalties(), this);
        getServer().getPluginManager().registerEvents(new PenaltyAgreement(), this);
        getServer().getPluginManager().registerEvents(new BankForBanker(), this);
        getServer().getPluginManager().registerEvents(new RemovePenaltyAgreement(), this);
        getServer().getPluginManager().registerEvents(new WithdrawAndDeposit(), this);
        getServer().getPluginManager().registerEvents(new ClearOnLeave(), this);

    }
    public static YourBank getInstance() {
        return instance;
    }

    public static PluginContext getPluginContext() {return pluginContext;}
}
