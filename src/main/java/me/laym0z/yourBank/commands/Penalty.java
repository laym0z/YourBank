package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.Data;
import me.laym0z.yourBank.UI.BankMain;
import me.laym0z.yourBank.UI.Penalties;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Penalty implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        int amount;
        String termPayment;
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED+"Введи дані");
            return true;
        }
        if (args.length < 5 && (!args[0].equals("remove") && !args[0].equals("list"))) {
            commandSender.sendMessage(ChatColor.RED+"Дані введені не вірно");
            return true;
        }
        //ADD
        if (args.length >= 4 && args[0].equals("add")) {
            String name = args[1];
            termPayment = args[3];
            if (!Data.hasPlayedBefore(name)) {
                commandSender.sendMessage(String.format(ChatColor.RED+"Гравця %s не існує!", name));
                return true;
            }

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED+"Некорекне поле суми: "+ChatColor.UNDERLINE+args[2]);
                return true;
            }
            if (!termPayment.matches("^\\d{2}-\\d{2}-\\d{4}$")){
                commandSender.sendMessage(ChatColor.RED+"Дата має вводитися в наступному форматі: DD-MM-YYYY \nПриклад: 01-01-2025");
                return true;
            }
            if (LocalDate.parse(termPayment).isBefore(LocalDate.now())) {
                commandSender.sendMessage(ChatColor.RED+"Термін оплати не може бути раніше поточної дати!");
                return true;
            }

            StringBuilder reason = new StringBuilder();
            int i = 0;
            for (String arg : args) {
                if (i > 3) reason.append(arg).append(" ");
                i++;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
            LocalDate date = LocalDate.now();

            Data.addPenalty(name, amount, reason.toString(), formatter.format(date), termPayment);
            commandSender.sendMessage(String.format(ChatColor.GREEN+"Гравцю %s був виписаний штраф на суму %d ДР", name, amount));
            return true;
        }
        //LIST
        else if (args.length == 2 && args[0].equals("list")) {
            if (!Data.doesPlayerHavePenalties(args[1])) {
                commandSender.sendMessage(ChatColor.RED+"Цей гравець не має штрафів");
                return true;
            }
            Penalties.openPenaltyListMenu((Player) commandSender, args[1]);
        }
        //REMOVE
        else if (args.length == 2 && args[0].equals("remove")) {
            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED+"Не коректне значення ID: "+args[1]);
                return true;
            }
            if (Data.removePenalty(id)) {
                commandSender.sendMessage(String.format(ChatColor.GOLD+"Штраф з ID %d був бидалений", id));
            }
            else {
                commandSender.sendMessage(String.format(ChatColor.RED+"Штрафу з ID %d не існує", id));
            }

        }
        else if (args.length > 2) {
            commandSender.sendMessage(ChatColor.RED+"Занадто багато аргументів!");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        //1 argument
        if (strings.length==1) {
            List <String> valid = new ArrayList<>();
            StringUtil.copyPartialMatches(strings[0], List.of("add", "list", "remove"), valid);
            return valid;
        }

        //2 argument
        else if (strings.length == 2 && strings[0].equals("add")) {
            return List.of("<гравець>");
        }
        else if (strings.length == 2 && strings[0].equals("list")) {
            return List.of("<гравець>");
        }
        else if (strings.length == 2 && strings[0].equals("remove")) {
            return List.of("<ID штрафу>");
        }
        //3 argument
        else if (strings.length == 3 && strings[0].equals("add")) {
            return List.of("<сума>");
        }
        //4 argument
        else if (strings.length == 4 && strings[0].equals("add")) {
            return List.of("<термін виплати DD-MM-YYYY>");
        }
        //5 argument
        else if (strings.length >= 5 && strings[0].equals("add")) {
            return List.of("<причина>");
        }
        return List.of();
    }
}
