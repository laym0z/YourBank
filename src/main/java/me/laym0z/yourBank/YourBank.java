package me.laym0z.yourBank;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.Data.DB.DatabaseConnector;
import me.laym0z.yourBank.Data.DB.SQLite;
import me.laym0z.yourBank.Data.TablesCreate.CreateBankTableSQL;
import me.laym0z.yourBank.Data.TablesCreate.CreatePenaltiesTableSQL;
import me.laym0z.yourBank.Data.TablesCreate.CreateRegisteredPlayersTableSQL;
import me.laym0z.yourBank.Data.TablesCreate.CreateStateTreasuryTableSQL;
import me.laym0z.yourBank.Data.PluginContext;
import me.laym0z.yourBank.Data.ClearOnLeave;
import me.laym0z.yourBank.Data.DB.MySQL;
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
import me.laym0z.yourBank.config.bankConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;
import java.util.Objects;

public final class YourBank extends JavaPlugin implements Listener {
    static YourBank instance;
    public static PluginContext pluginContext;
    static DatabaseConnector database;
    @Override
    public void onEnable() {
        pluginContext = new PluginContext();
        instance = this;

        saveDefaultConfig();
        bankConfig.getInstance().Load();
        String DBType = getConfig().getString("database.type");

        //-----------------------MySQL--------------------
        if ("mysql".equalsIgnoreCase(DBType)) {
            ConfigurationSection MSCfg = getConfig().getConfigurationSection("database.mysql");
            database = new MySQL(
                    MSCfg.getString("host"),
                    MSCfg.getInt("port"),
                    MSCfg.getString("database"),
                    MSCfg.getString("user"),
                    MSCfg.getString("password")
            );

        } else {
            //------------------SQLite-------------------
            String dbFile = getConfig().getString("database.sqlite_file");
            if (dbFile == null) {
                dbFile = "yourBank.db";
            }
            String path = "jdbc:sqlite:plugins/YourBank/" + dbFile;
            database = new SQLite(path);
        }
        CreateBankTableSQL.Create();
        CreatePenaltiesTableSQL.Create();
        CreateStateTreasuryTableSQL.Create();
        CreateRegisteredPlayersTableSQL.Create();

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
        getServer().getPluginManager().registerEvents(this, this);

    }
    public static DatabaseConnector getDatabaseConnector() {return database;}

    public static YourBank getInstance() {
        return instance;
    }

    public static PluginContext getPluginContext() {return pluginContext;}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Database data = new Database(YourBank.getDatabaseConnector());
        Player player = event.getPlayer();
        data.addToTable(player.getUniqueId().toString(), player.getName(), String.valueOf(LocalDate.now()));
    }
}
