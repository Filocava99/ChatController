package it.forgottenworld.fwchatcontrol.command;

import it.forgottenworld.fwchatcontrol.FWChatControl;
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
        if(args.length == 0){
            printHelp(sender);
        }else if(args[0].equalsIgnoreCase("word")){
            if(args[1].equalsIgnoreCase("ban")){
                banWord(sender, args);
            }else if(args[1].equalsIgnoreCase("list")){
                list(sender, args);
            }else if(args[1].equalsIgnoreCase("unban")){
                unbanWord(sender, args);
            }else{
                printHelp(sender);
            }
        }else if(args[1].equalsIgnoreCase("setCaps")){
            setCapsPercentage(sender, args);
        }else if(args[1].equalsIgnoreCase("reload")){
            reload(sender);
        }else if(args[1].equalsIgnoreCase("warn")){
            warnPlayer(sender, args);
        }else if(args[1].equalsIgnoreCase("removeWarn")){
           removeWarn(sender, args);
        }else if(args[1].equalsIgnoreCase("reset")){
            resetWarn(sender, args);
        }else if(args[1].equalsIgnoreCase("punish")){
            forcePunishment(sender, args);
        }else if(args[1].equalsIgnoreCase("ranking")){
            playerWarnsRanking(sender);
        }else if(args[1].equalsIgnoreCase("capitalize")){
            toggleFirstLetterCapitalization(sender);
        }else{
            printHelp(sender);
        }
        return true;
    }

    private void printHelp(CommandSender sender){
        sender.sendMessage(ChatColor.GREEN + "/fww reload");
        sender.sendMessage(ChatColor.GREEN + "/fww warn <player>");
        sender.sendMessage(ChatColor.GREEN + "/fww removeWarn <player>");
        sender.sendMessage(ChatColor.GREEN + "/fww reset <player>");
        sender.sendMessage(ChatColor.GREEN + "/fww setCaps <percentage>");
        sender.sendMessage(ChatColor.GREEN + "/fww capitalize");
        sender.sendMessage(ChatColor.GREEN + "/fww punish <player> <warnLevel>");
        sender.sendMessage(ChatColor.GREEN + "/fwcc word ban <word>");
        sender.sendMessage(ChatColor.GREEN + "/fwcc word list <page>");
        sender.sendMessage(ChatColor.GREEN + "/fwcc word unban <word>");
    }

    private void banWord(CommandSender sender, String[] args){
        if(args.length < 3){
            printHelp(sender);
        }else{
            StringBuilder stringBuilder = new StringBuilder();
            String wordToBan = String.join(" ",Arrays.copyOfRange(args,2,args.length));
            for(char c : wordToBan.toCharArray()){
                stringBuilder.append("[").append(c == ' ' ? "\\s" : c).append("]");
            }
            plugin.getSettings().getRegexes().add(Pattern.compile(stringBuilder.toString()));
            plugin.saveConfig();
        }
    }

    private void playerWarnsRanking(CommandSender sender){
        ComponentBuilder componentBuilder = new ComponentBuilder();
        plugin.getWarnController().getPlayerWarnsCount().entrySet().stream().limit(10).forEach(uuidIntegerEntry -> {
            componentBuilder.append(ChatColor.GREEN + Bukkit.getPlayer(uuidIntegerEntry.getKey()).getName() + " " + uuidIntegerEntry.getValue() + " warn points");
        });
        sender.spigot().sendMessage(componentBuilder.create());
    }

    private void forcePunishment(CommandSender sender, String[] args){
        if(args.length < 4){
            sender.sendMessage(ChatColor.RED + "You must specify a player and a warn level!");
        }else{
            OfflinePlayer player = Bukkit.getPlayer(args[2]);
            int warns = Integer.parseInt(args[3]);
            if(player != null){
                plugin.getWarnController().punishPlayer(player, warns);
                sender.sendMessage(ChatColor.GREEN + player.getName() + " has been punished!");
            }else{
                sender.sendMessage(ChatColor.RED + "That player does not exists!");
            }
        }
    }

    private void toggleFirstLetterCapitalization(CommandSender sender){
        plugin.getSettings().setCapitalizeFirstLetter(!plugin.getSettings().isCapitalizeFirstLetter());
        sender.sendMessage(ChatColor.GREEN + "First letter capitalization now set to " + ChatColor.BOLD + plugin.getSettings().isCapitalizeFirstLetter());
        plugin.saveConfig();
    }

    private void removeWarn(CommandSender sender, String[] args){
        if(args.length < 3){
            sender.sendMessage(ChatColor.RED + "You must specify a player!");
        }else{
            OfflinePlayer player = Bukkit.getPlayer(args[2]);
            if(player != null){
                plugin.getWarnController().removeWarn(player);
            }else{
                sender.sendMessage(ChatColor.RED + "That player does not exists!");
            }
        }
    }

    private void resetWarn(CommandSender sender, String[] args){
        if(args.length < 3){
            sender.sendMessage(ChatColor.RED + "You must specify a player!");
        }else{
            OfflinePlayer player = Bukkit.getPlayer(args[2]);
            if(player != null){
                plugin.getWarnController().resetWarn(player);
            }else{
                sender.sendMessage(ChatColor.RED + "That player does not exists!");
            }
        }
    }

    private void warnPlayer(CommandSender sender, String[] args){
        if(args.length < 3){
            sender.sendMessage(ChatColor.RED + "You must specify a player!");
        }else{
            OfflinePlayer player = Bukkit.getPlayer(args[2]);
            if(player != null){
                plugin.getWarnController().warnPlayer(player);
            }else{
                sender.sendMessage(ChatColor.RED + "That player does not exists!");
            }
        }
    }

    private void setCapsPercentage(CommandSender sender, String[] args){
        if(args.length < 3){
            sender.sendMessage(ChatColor.RED + "You must specify the new caps percentage!");
        }else{
            plugin.getSettings().setMaxCapsCharPercentage(Integer.parseInt(args[2]));
            sender.sendMessage(ChatColor.GREEN + "Caps percentage updated!");
            plugin.saveConfig();
        }
    }

    private void reload(CommandSender sender){
        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
        plugin.reloadConfig();
    }

    private void list(CommandSender sender, String[] args){
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

    private void unbanWord(CommandSender sender, String[] args){
        if(args.length < 3){
            printHelp(sender);
        }else{
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
