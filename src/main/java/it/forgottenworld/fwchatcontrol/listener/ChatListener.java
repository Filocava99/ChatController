package it.forgottenworld.fwchatcontrol.listener;

import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import it.forgottenworld.fwchatcontrol.FWChatControl;
import it.forgottenworld.fwchatcontrol.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private final int maxCapPercentage;
    private final int maxMessagesPerSecond;
    private final List<String> regexes;
    private final Map<UUID, Integer> playerMessagesCount = new HashMap<>();
    private int seconds = 0;

    public ChatListener(FWChatControl plugin) {
        Config config = plugin.getPluginConfig();
        maxMessagesPerSecond = config.getConfig().getInt("maxMessagesPerSecond");
        maxCapPercentage = config.getConfig().getInt("maxCapsCharPercentage");
        regexes = config.getConfig().getStringList("bannedWords");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event){
        floodCheck(event);
        if(event.isCancelled()) return;
        event.setMessage(removeBannedWords(capitalizeFirstLetter(capsCheck(event.getMessage()))));
    }

    private void floodCheck(AsyncPlayerChatEvent event){
        int newSeconds = LocalDateTime.now().getSecond();
        if(newSeconds != seconds){
            playerMessagesCount.clear();
            seconds = newSeconds;
        }else{
            UUID playerUUID = event.getPlayer().getUniqueId();
            if(playerMessagesCount.containsKey(playerUUID)){
                int messagesCount = playerMessagesCount.get(playerUUID) + 1;
                if(messagesCount > maxMessagesPerSecond){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "Stop flooding the chat!");
                }else{
                    playerMessagesCount.put(playerUUID, messagesCount+1);
                }
            }else{
                playerMessagesCount.put(playerUUID, 1);
            }
        }
    }

    private String capsCheck(String message){
        float upperChars = 0;
        for(char c : message.toCharArray()){
            if((int)c >= (int)'A' && (int)c<=(int)'Z'){
                upperChars++;
            }
        }
        if((upperChars / message.length()) * 100 > maxCapPercentage){
            message = message.toLowerCase();
        }
        return message;
    }

    private String removeBannedWords(String message){
        final String replacementCharacters = "*****";
        for(String regex : regexes){
            message = message.replaceAll(regex, replacementCharacters);
        }
        return message;
    }

    private String capitalizeFirstLetter(String message){
        return message.substring(0, 1).toUpperCase() + message.substring(1);
    }
}
