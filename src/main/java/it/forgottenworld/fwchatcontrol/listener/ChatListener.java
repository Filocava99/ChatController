package it.forgottenworld.fwchatcontrol.listener;

import it.forgottenworld.fwchatcontrol.FWChatControl;
import it.forgottenworld.fwchatcontrol.Settings;
import it.forgottenworld.fwchatcontrol.WarnController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final Settings settings;
    private final WarnController warnController;
    private final Map<UUID, Integer> playerMessagesCount = new HashMap<>();
    private int seconds = 0;

    public ChatListener(FWChatControl chatControl) {
        this.settings = chatControl.getSettings();
        this.warnController = chatControl.getWarnController();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        controlMessage(event);
    }

    private void controlMessage(AsyncPlayerChatEvent event){
        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId());
        floodCheck(event);
        if (event.isCancelled()) return;
        String message = event.getMessage();
        if (containsBannedWords(message)) {
            Bukkit.getScheduler().runTask(FWChatControl.getINSTANCE(),()->{
                warnController.warnPlayer(player);
            });
        }
        if(checkIfViolatingCapsRules(message)){
            if(settings.isWarnIfUsingCaps()){
                Bukkit.getScheduler().runTask(FWChatControl.getINSTANCE(),()->{
                    warnController.warnPlayer(player);
                });
            }
            message = message.toLowerCase();
        }
        event.setMessage(capitalizeFirstLetter(removeBannedWords(message)));
    }

    private void floodCheck(AsyncPlayerChatEvent event) {
        int newSeconds = LocalDateTime.now().getSecond();
        if (newSeconds != seconds) {
            playerMessagesCount.clear();
            seconds = newSeconds;
        } else {
            UUID playerUUID = event.getPlayer().getUniqueId();
            if (playerMessagesCount.containsKey(playerUUID)) {
                int messagesCount = playerMessagesCount.get(playerUUID) + 1;
                if (messagesCount > settings.getMaxMessagesPerSecond()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "Stop flooding the chat!");
                    if(settings.isWarnIfFlooding())warnController.warnPlayer(event.getPlayer());
                } else {
                    playerMessagesCount.put(playerUUID, messagesCount + 1);
                }
            } else {
                playerMessagesCount.put(playerUUID, 1);
            }
        }
    }

    private boolean checkIfViolatingCapsRules(String message) {
        float upperChars = 0;
        for (char c : message.toCharArray()) {
            if ((int) c >= (int) 'A' && (int) c <= (int) 'Z') {
                upperChars++;
            }
        }
        return (upperChars / message.length()) * 100 > settings.getMaxCapsCharPercentage();
    }

    private boolean containsBannedWords(String message) {
        for (Pattern regex : settings.getRegexes()) {
            if (regex.matcher(message).find()) return true;
        }
        return false;
    }

    private String removeBannedWords(String message) {
        final String replacementCharacters = "*****";
        for (Pattern regex : settings.getRegexes()) {
            message = regex.matcher(message).replaceAll(replacementCharacters);
        }
        return message;
    }

    private String capitalizeFirstLetter(String message) {
        return settings.isCapitalizeFirstLetter() ? message.substring(0, 1).toUpperCase() + message.substring(1) : message;

    }
}
