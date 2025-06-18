package me.laym0z.yourBank.Data;

import me.laym0z.yourBank.Data.TempStorage.DiamondChoose;
import me.laym0z.yourBank.Data.TempStorage.PenaltiesManager;
import me.laym0z.yourBank.Data.TempStorage.SessionManager;
import me.laym0z.yourBank.Data.TempStorage.TransferManager;

public class PluginContext {
    public SessionManager sessionManager = new SessionManager();
    public DiamondChoose diamondChoose = new DiamondChoose();
    public PenaltiesManager penaltiesManager = new PenaltiesManager();
    public TransferManager transferManager = new TransferManager();
}
