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
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private final int maxCapPercentage;
    private final int maxMessagesPerSecond;
    private final Map<UUID, Integer> playerMessagesCount = new HashMap<>();
    private int seconds = 0;

    public ChatListener(FWChatControl plugin) {
        Config config = plugin.getPluginConfig();
        maxMessagesPerSecond = config.getConfig().getInt("maxMessagesPerSecond");
        maxCapPercentage = config.getConfig().getInt("maxCapsCharPercentage");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event){
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
                    return;
                }else{
                    playerMessagesCount.put(playerUUID, messagesCount+1);
                }
            }else{
                playerMessagesCount.put(playerUUID, 1);
            }
        }
        String[] fullMessage = event.getMessage().split(":");
        String message = fullMessage[1];
        float upperChars = 0;
        for(char c : message.toCharArray()){
            if(String.valueOf(c).matches("[A-Z]")){
                upperChars++;
            }
        }
        System.out.println((upperChars / message.length()) * 100);
        if((upperChars / message.length()) * 100 > maxCapPercentage){
            message = message.toLowerCase();
        }
        message = message.substring(0, 1).toUpperCase() + message.substring(1);
        event.setMessage(String.join(fullMessage[0],message));
    }
}
