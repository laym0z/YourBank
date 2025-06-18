package me.laym0z.yourBank.Test;

import me.laym0z.yourBank.YourBank;

public class PrintHashMaps {
    public static void printHashMapsInfo() {
        System.out.println("--------------------------------------------------------");
        System.out.println("|                   HashMaps                            |");
        System.out.println("--------------------------------------------------------");
        YourBank.getPluginContext().diamondChoose.printAll();
        YourBank.getPluginContext().sessionManager.printAll();
        YourBank.getPluginContext().transferManager.printAll();
        YourBank.getPluginContext().penaltiesManager.printAll();
        System.out.println("--------------------------------------------------------");
        System.out.println("|                      END                              |");
        System.out.println("--------------------------------------------------------");
    }
}
