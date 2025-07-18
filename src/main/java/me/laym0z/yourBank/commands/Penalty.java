package me.laym0z.yourBank.commands;

import me.laym0z.yourBank.Data.DB.Database;
import me.laym0z.yourBank.UI.Penalty.Penalties;
import me.laym0z.yourBank.YourBank;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Penalty implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        Database database = new Database(YourBank.getDatabaseConnector());


        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                    ChatColor.RESET+ChatColor.RED+" Введи дані");
            return true;


        }
        if (args.length < 6 && args[0].equals("add")) {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                    ChatColor.RESET+ChatColor.RED+" Дані введені не вірно");
            return false;
        }


        //ADD
        if (args.length >= 5 && args[0].equals("add")) {

            int amount;
            String receiver;

            String name = args[1];
            String termPayment = args[3];

            if (!database.hasPlayedBefore(name)) {
                commandSender.sendMessage(String.format(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                        ChatColor.RESET+ChatColor.RED+" Гравця %s не існує!", name));
                return true;
            }

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                        ChatColor.RESET+ChatColor.RED+" Некорекне поле суми: "+ChatColor.UNDERLINE+args[2]);
                return true;
            }
            if (!termPayment.matches("^\\d{4}-\\d{2}-\\d{2}$")){
                commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                        ChatColor.RESET+ChatColor.RED+" Дата має вводитися в наступному форматі: YYYY-MM-DD \nПриклад: "+ChatColor.GOLD+"2025-01-01");
                return true;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate;
            try {
                parsedDate = LocalDate.parse(termPayment, formatter);
            } catch (DateTimeParseException e) {
                commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                        ChatColor.RESET+ChatColor.RED+" Неможливо обробити дату. Перевір формат.");
                return true;
            }

            if (parsedDate.isBefore(LocalDate.now())) {
                commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                        ChatColor.RESET+ChatColor.RED+" Термін оплати не може бути раніше поточної дати!");
                return true;
            }

            //-----------------------RECEIVER----------------------------

            if (args[4].equalsIgnoreCase("Держава")) {
                receiver = args[4];
            }
            else if (database.getPlayersBank(args[4])) {
                receiver = args[4];
            }
            else {
                commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                        ChatColor.RESET+ChatColor.RED+" Цей гравець не має банківського рахунку!");
                return true;
            }
            //------------------------------------------------------------


            StringBuilder reason = new StringBuilder();

            for (int i = 5; i<args.length; i++) {
                reason.append(args[i]).append(" ");
            }

            LocalDate date = LocalDate.now();

            database.addPenalty(name, amount, reason.toString(), date, parsedDate, receiver);
            commandSender.sendMessage(String.format(ChatColor.DARK_GREEN+""+ChatColor.BOLD+ "[Штрафи]"+
                    ChatColor.RESET+ChatColor.GREEN+" Гравцю %s був виписаний штраф на суму %d ДР", name, amount));
            return true;
        }


        //LIST
        else if (args.length == 2 && args[0].equals("list")) {
            if (!database.doesPlayerHavePenalties(args[1])) {
                commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                        ChatColor.RESET+ChatColor.RED+" Цей гравець не має штрафів");
                return true;
            }
            Penalties.openPenaltyListMenu((Player) commandSender, args[1], false);
        }


        //REMOVE
        else if (args.length == 2 && args[0].equals("remove")) {
            if (!database.doesPlayerHavePenalties(args[1])) {
                commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                        ChatColor.RESET+ChatColor.RED+" Цей гравець не має штрафів");
                return true;
            }
            Penalties.openPenaltyListMenu((Player) commandSender, args[1], true);

        }
        else if (args.length > 2) {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                    ChatColor.RESET+ChatColor.RED+" Занадто багато аргументів!");
            return true;
        }
        else {
            commandSender.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+ "[Штрафи]"+
                    ChatColor.RESET+ChatColor.RED+" Неправельні аргументи");
            return true;
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
            return List.of("<нік>");
        }
        //3 argument
        else if (strings.length == 3 && strings[0].equals("add")) {
            return List.of("<сума>");
        }
        //4 argument
        else if (strings.length == 4 && strings[0].equals("add")) {
            return List.of("<термін виплати YYYY-MM-DD>");
        }
        //5 argument
        else if (strings.length == 5 && strings[0].equals("add")) {
            return List.of("[отримувач] | <нік гравця> | держава ");
        }
        //6 argument
        else if (strings.length >= 6 && strings[0].equals("add")) {
            return List.of("<причина>");
        }
        return List.of();
    }
}
