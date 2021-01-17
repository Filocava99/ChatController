package it.forgottenworld.fwchatcontrol.command;

import it.forgottenworld.fwchatcontrol.FWChatControl;
import it.forgottenworld.fwchatcontrol.punishment.Punishment;
import it.forgottenworld.fwchatcontrol.punishment.PunishmentType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdminCommand implements CommandExecutor {

    private FWChatControl plugin;
    private final int elementsPerPage = 5;

    public AdminCommand(FWChatControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            printHelp(sender, args);
        } else if (args[0].equalsIgnoreCase("word")) {
            if (args[1].equalsIgnoreCase("ban")) {
                banWord(sender, args);
            } else if (args[1].equalsIgnoreCase("list")) {
                list(sender, args);
            } else if (args[1].equalsIgnoreCase("unban")) {
                unbanWord(sender, args);
            } else {
                printHelp(sender, args);
            }
        } else if (args[0].equalsIgnoreCase("setCaps")) {
            setCapsPercentage(sender, args);
        } else if (args[0].equalsIgnoreCase("reload")) {
            reload(sender);
        } else if (args[0].equalsIgnoreCase("warn")) {
            warnPlayer(sender, args);
        } else if (args[0].equalsIgnoreCase("warnAll")) {
            warnAll(sender);
        } else if (args[0].equalsIgnoreCase("reduce")) {
            removeWarn(sender, args);
        } else if (args[0].equalsIgnoreCase("reduceAll")) {
            reduceAll(sender);
        } else if (args[0].equalsIgnoreCase("reset")) {
            resetWarn(sender, args);
        } else if (args[0].equalsIgnoreCase("resetAll")) {
            resetAll(sender);
        } else if (args[0].equalsIgnoreCase("punish")) {
            forcePunishment(sender, args);
        } else if (args[0].equalsIgnoreCase("ranking")) {
            playerWarnsRanking(sender);
        } else if (args[0].equalsIgnoreCase("info")) {
            info(sender, args);
        } else if (args[0].equalsIgnoreCase("capitalize")) {
            toggleFirstLetterCapitalization(sender);
        } else if (args[0].equalsIgnoreCase("capsWarn")) {
            toggleWarnForCaps(sender);
        } else if (args[0].equalsIgnoreCase("floodWarn")) {
            toggleWarnForFlooding(sender);
        } else if (args[0].equalsIgnoreCase("bannedWordsWarn")) {
            toggleWarnForBannedWords(sender);
        } else if (args[0].equalsIgnoreCase("add")) {
            addPunishment(sender, args);
        } else if (args[0].equalsIgnoreCase("remove")) {
            removePunishment(sender, args);
        } else {
            args[0] = "1";
            printHelp(sender, args);
        }
        return true;
    }

    private void toggleWarnForCaps(CommandSender sender) {
        plugin.getSettings().setWarnIfUsingCaps(!plugin.getSettings().isWarnIfUsingCaps());
        sender.sendMessage(ChatColor.GREEN + "Warn if exceeding caps limit now set to " + ChatColor.BOLD + plugin.getSettings().isCapitalizeFirstLetter());
        plugin.saveConfig();
    }

    private void toggleWarnForBannedWords(CommandSender sender) {
        plugin.getSettings().setWarnIfUsingBannedWords(!plugin.getSettings().isWarnIfUsingBannedWords());
        sender.sendMessage(ChatColor.GREEN + "Warn for using banned words now set to " + ChatColor.BOLD + plugin.getSettings().isCapitalizeFirstLetter());
        plugin.saveConfig();
    }

    private void toggleWarnForFlooding(CommandSender sender) {
        plugin.getSettings().setWarnIfFlooding(!plugin.getSettings().isWarnIfFlooding());
        sender.sendMessage(ChatColor.GREEN + "Warn for flooding now set to " + ChatColor.BOLD + plugin.getSettings().isCapitalizeFirstLetter());
        plugin.saveConfig();
    }

    private void warnAll(CommandSender sender) {
        Arrays.stream(Bukkit.getServer().getOfflinePlayers()).forEach(player -> plugin.getWarnController().warnPlayer(player));
        sender.sendMessage(ChatColor.GREEN + "All players have been warned!");
    }

    private void reduceAll(CommandSender sender) {
        Arrays.stream(Bukkit.getServer().getOfflinePlayers()).forEach(player -> plugin.getWarnController().removeWarn(player));
        sender.sendMessage(ChatColor.GREEN + "All players warn points have been reduced by one!");
    }

    private void resetAll(CommandSender sender) {
        Arrays.stream(Bukkit.getServer().getOfflinePlayers()).forEach(player -> plugin.getWarnController().resetWarn(player));
        sender.sendMessage(ChatColor.GREEN + "All players warn points have been reset!");
    }

    private void printHelp(CommandSender sender, String[] args) {
        int index;
        if (args.length < 2) {
            index = 1;
        } else {
            try {
                index = Integer.parseInt(args[1]);
            } catch (Exception e) {
                index = 1;
            }
        }
        sender.sendMessage(ChatColor.GREEN + "------{ " + ChatColor.GOLD + "FWChatControl" + ChatColor.GREEN + " }------");
        switch (index) {
            case 1: {
                sender.sendMessage(ChatColor.GREEN + "/fwcc help [page]");
                sender.sendMessage(ChatColor.GREEN + "/fwcc reload");
                sender.sendMessage(ChatColor.GREEN + "/fwcc warn <player>");
                sender.sendMessage(ChatColor.GREEN + "/fwcc warnAll");
                sender.sendMessage(ChatColor.GREEN + "/fwcc reduce <player>");
                sender.sendMessage(ChatColor.GREEN + "/fwcc reduceAll");
                sender.sendMessage(ChatColor.GREEN + "/fwcc reset <player>");
                sender.sendMessage(ChatColor.GREEN + "/fwcc resetAll");
                sender.sendMessage(ChatColor.GREEN + "/fwcc setCaps <percentage>");
                sender.sendMessage(ChatColor.GREEN + "/fwcc ranking");
                sender.sendMessage(ChatColor.GREEN + "/fwcc info <player>");
                break;
            }
            case 2: {
                sender.sendMessage(ChatColor.GREEN + "/fwcc capitalize");
                sender.sendMessage(ChatColor.GREEN + "/fwcc capsWarn");
                sender.sendMessage(ChatColor.GREEN + "/fwcc floodWarn");
                sender.sendMessage(ChatColor.GREEN + "/fwcc bannedWordsWarn");
                sender.sendMessage(ChatColor.GREEN + "/fwcc punish <player> <warnLevel>");
                sender.sendMessage(ChatColor.GREEN + "/fwcc word ban <word>");
                sender.sendMessage(ChatColor.GREEN + "/fwcc word list <page>");
                sender.sendMessage(ChatColor.GREEN + "/fwcc word unban <word>");
                sender.sendMessage(ChatColor.GREEN + "/fwcc add <warns> <type> <duration> " + ChatColor.GRAY + "Adds a new punishments for specified warns");
                sender.sendMessage(ChatColor.GREEN + "/fwcc remove <warns> " + ChatColor.GRAY + "Removes the punishments for specified warns");
                break;
            }
            default:
                sender.sendMessage(ChatColor.RED + "Invalid help page!");
        }
    }

    private void info(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify the player name!");
        } else {
            try {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                int warns = plugin.getWarnController().getPlayerWarnsCount().get(player.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD+ player.getName() + ChatColor.RESET + ChatColor.GREEN +
                        " has a total of " + ChatColor.BOLD + warns + ChatColor.RESET + ChatColor.GREEN + " warns");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "That player does not exist or doesn't have any warns!");
            }
        }
    }

    private void addPunishment(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Missing parameters. Use /fwcc help for more information");
        } else {
            try {
                Punishment punishment = new Punishment(PunishmentType.valueOf(args[2]), Integer.parseInt(args[3]));
                plugin.getSettings().addPunishment(Integer.parseInt(args[1]), punishment);
                sender.sendMessage(ChatColor.GREEN + "New punishment created!");
                plugin.saveConfig();
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid parameters. Allowed punishments types: KICK, MUTE, BAN. Warns and duration must be integer values.");
            }
        }
    }

    private void removePunishment(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify the warn points for the punishment to be removed!");
        } else {
            try {
                plugin.getSettings().removePunishment(Integer.parseInt(args[1]));
                sender.sendMessage(ChatColor.GREEN + "Punishment removed!");
                plugin.saveConfig();
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Warns must be an integer value!");
            }
        }
    }

    private void banWord(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Missing parameters. Use /fwcc help for more information");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            String wordToBan = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            for (char c : wordToBan.toCharArray()) {
                stringBuilder.append("[").append(c == ' ' ? "\\s" : c).append("]");
            }
            plugin.getSettings().getRegexes().add(Pattern.compile(stringBuilder.toString()));
            plugin.saveConfig();
            sender.sendMessage(ChatColor.GREEN + "The word " + ChatColor.YELLOW + wordToBan + ChatColor.GREEN + " has been banned!");
        }
    }

    private void playerWarnsRanking(CommandSender sender) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        plugin.getWarnController().getPlayerWarnsCount().entrySet().stream().limit(10).forEach(uuidIntegerEntry -> {
            componentBuilder.append(ChatColor.GREEN + Bukkit.getPlayer(uuidIntegerEntry.getKey()).getName() + " " + uuidIntegerEntry.getValue() + " warn points");
        });
        sender.spigot().sendMessage(componentBuilder.create());
    }

    private void forcePunishment(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "You must specify a player and a warn level!");
        } else {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            int warns = Integer.parseInt(args[2]);
            if (player != null) {
                plugin.getWarnController().punishPlayer(player, warns);
                sender.sendMessage(ChatColor.GREEN + player.getName() + " has been punished!");
            } else {
                sender.sendMessage(ChatColor.RED + "That player does not exists!");
            }
        }
    }

    private void toggleFirstLetterCapitalization(CommandSender sender) {
        plugin.getSettings().setCapitalizeFirstLetter(!plugin.getSettings().isCapitalizeFirstLetter());
        sender.sendMessage(ChatColor.GREEN + "First letter capitalization now set to " + ChatColor.BOLD + plugin.getSettings().isCapitalizeFirstLetter());
        plugin.saveConfig();
    }

    private void removeWarn(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a player!");
        } else {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                plugin.getWarnController().removeWarn(player);
                sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + "" + ChatColor.GREEN + " has been warned!");
            } else {
                sender.sendMessage(ChatColor.RED + "That player does not exists!");
            }
        }
    }

    private void resetWarn(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a player!");
        } else {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                plugin.getWarnController().resetWarn(player);
                sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + "" + ChatColor.GREEN + " warns have been reset!");
            } else {
                sender.sendMessage(ChatColor.RED + "That player does not exists!");
            }
        }
    }

    private void warnPlayer(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a player!");
        } else {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                plugin.getWarnController().warnPlayer(player);
            } else {
                sender.sendMessage(ChatColor.RED + "That player does not exists!");
            }
        }
    }

    private void setCapsPercentage(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify the new caps percentage!");
        } else {
            plugin.getSettings().setMaxCapsCharPercentage(Integer.parseInt(args[1]));
            sender.sendMessage(ChatColor.GREEN + "Caps percentage updated!");
            plugin.saveConfig();
        }
    }

    private void reload(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
        plugin.reloadConfig();
    }

    private void list(CommandSender sender, String[] args) {
        int currentPage = args.length < 3 ? 1 : Integer.parseInt(args[2]);
        ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.GREEN + "-------------- Banned words --------------\n");
        TextComponent nextPageComponent = getArrow(">>", "/fwcc word list " + (currentPage + 1));
        TextComponent previousPageComponent = getArrow("<<", "/fwcc word list " + (currentPage - 1));
        List<String> bannedWords = plugin.getSettings().getRegexes().stream().map(Pattern::pattern).collect(Collectors.toList());
        int beginIndex = getPageBeginIndex(currentPage);
        if (beginIndex >= bannedWords.size()) {
            noMoreElementsToShowAlert(sender, componentBuilder, previousPageComponent);
            return;
        }
        int endIndex = beginIndex + elementsPerPage - 1;
        if (endIndex >= bannedWords.size()) {
            endIndex = bannedWords.size() - 1;
        }
        for (int i = beginIndex; i <= endIndex; i++) {
            TextComponent textComponent = new TextComponent(ChatColor.GRAY + "[" + ChatColor.GOLD + "Unban" + ChatColor.GRAY + "]");
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fwcc word unban " + bannedWords.get(i)));
            textComponent.setBold(true);
            componentBuilder.append(ChatColor.YELLOW + bannedWords.get(i)).append("    ").append(textComponent).append("\n");
        }
        componentBuilder.append(ChatColor.GREEN + "-------------- ");
        if (currentPage > 1) {
            componentBuilder.append(previousPageComponent);
        }
        componentBuilder.append(" ").append(nextPageComponent).append(ChatColor.GREEN + " --------------");
        sender.spigot().sendMessage(componentBuilder.create());
    }

    private void unbanWord(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Missing parameters. Use /fwcc help for more information");
        } else {
            plugin.getSettings().getRegexes().removeIf(pattern -> pattern.pattern().equalsIgnoreCase(args[2]));
            sender.sendMessage(ChatColor.GREEN + "Regex " + ChatColor.DARK_GREEN + args[2] + ChatColor.GREEN + " deleted.");
            plugin.saveConfig();
        }
    }

    private TextComponent getArrow(String arrowSymbol, String command) {
        TextComponent textComponent = new TextComponent(arrowSymbol);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        textComponent.setBold(true);
        textComponent.setColor(net.md_5.bungee.api.ChatColor.DARK_GREEN);
        return textComponent;
    }

    private int getPageBeginIndex(int currentPage) {
        return elementsPerPage * (currentPage - 1);
    }

    private void noMoreElementsToShowAlert(CommandSender sender, ComponentBuilder componentBuilder, TextComponent previousPageComponent) {
        componentBuilder.append(ChatColor.YELLOW + "Nothing more to display...\n");
        sender.spigot().sendMessage(componentBuilder.append(ChatColor.GREEN + "-------------- ").append(previousPageComponent).append(ChatColor.GREEN + " --------------").create());
    }

}
