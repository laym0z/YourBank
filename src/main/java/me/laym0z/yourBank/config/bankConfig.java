package me.laym0z.yourBank.config;

import me.laym0z.yourBank.YourBank;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class bankConfig {

    private final static bankConfig instance = new bankConfig();

    private File file;
    private YamlConfiguration config;
    private int exchangeRateInt;
    private double perTransactionDouble;
    public bankConfig() {

    }
    public void Load() {
        file = new File(YourBank.getInstance().getDataFolder(), "config.yml");
        if (!file.exists()) {
            YourBank.getInstance().saveResource("config.yml", false);
        }
        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String exchangeRate = config.getString("diamond_ore_to_deep_diamond_ore");
        String perTransaction = config.getString("diamond_ore_tax");

        try {
            exchangeRateInt = Integer.parseInt(exchangeRate);
            if (exchangeRateInt<1) exchangeRateInt=2;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            exchangeRateInt = 2;
        }

        try {
            perTransactionDouble = Double.parseDouble(perTransaction);
            if (perTransactionDouble < 0 || perTransactionDouble > 100) {
                perTransactionDouble = 0.02;
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            perTransactionDouble = 0.02;
        }
    }

    public static bankConfig getInstance() {
        return instance;
    }

    public void Save() {
        try {
            config.save(file);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int getExchangeRate() {
        return exchangeRateInt;
    }

    public double getPerTransaction() {
        return perTransactionDouble;
    }

//    public void SetValue(String path, Object value) {
//        config.set(path, value);
//        Save();
//    }
}
