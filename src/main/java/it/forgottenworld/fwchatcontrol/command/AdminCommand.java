package it.forgottenworld.fwchatcontrol.command;

import it.forgottenworld.fwchatcontrol.FWChatControl;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
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
        }else if(args[1].equalsIgnoreCase("reload")){
            reload();
        }else{
            printHelp(sender);
        }
        return true;
    }

    private void printHelp(CommandSender sender){
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

    private void reload(){
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
