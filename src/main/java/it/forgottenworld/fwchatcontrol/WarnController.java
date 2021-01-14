package it.forgottenworld.fwchatcontrol;

import com.earth2me.essentials.Essentials;
import it.forgottenworld.fwchatcontrol.punishment.Punishment;
import it.forgottenworld.fwchatcontrol.punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class WarnController {

    private final Essentials essentials;
    private final Settings settings;
    private final File warnsFile;

    private Map<UUID, Integer> playerWarnsCount;

    public WarnController(FWChatControl chatControl) {
        this.essentials = chatControl.getEssentials();
        this.settings = chatControl.getSettings();
        this.warnsFile = new File(chatControl.getDataFolder(), "warns.dat");
        loadWarns();
    }

    public Map<UUID, Integer> getPlayerWarnsCount() {
        return playerWarnsCount;
    }

    public void warnPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();
        int warns;
        if (playerWarnsCount.containsKey(playerUUID)) {
            warns = playerWarnsCount.get(playerUUID) + 1;
            if (settings.getPunishments().containsKey(warns)) {
                punishPlayer(player, warns);
            }
        } else {
            warns = 1;
        }
        playerWarnsCount.put(playerUUID, warns);
        saveWarns();
        if (warns != 0) {
            player.sendMessage(ChatColor.RED + "You have been warned for using an illegal word!");
            player.sendMessage(ChatColor.RED + "You have a total of " + ChatColor.DARK_RED + warns + ChatColor.RED + " warns.");
        }
    }

    public void punishPlayer(Player player, int warns){
        Punishment punishment = settings.getPunishments().get(warns);
        if(punishment.getType() == PunishmentType.MUTE){
            player.sendMessage(ChatColor.RED + "You have been temporarily muted for having reached " + warns + " warn points!");
            //String muteReason = "Reached " + warns + " warn points";
            mutePlayer(player, punishment.getDuration());
        } else if (punishment.getType() == PunishmentType.KICK) {
            player.kickPlayer("You have been kicked for having reached " + warns + " warn points!");
        }else if(punishment.getType() == PunishmentType.BAN){
            banPlayer(player, warns);
        }
    }

    public void mutePlayer(Player player, long durationInSeconds) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mute " + player.getName() + " " + durationInSeconds + "s");
    }

    public void banPlayer(Player player, int durationInSeconds){
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tempban " + player.getName() + " " + durationInSeconds + "s");
    }

    public void saveWarns() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(warnsFile, false));
            objectOutputStream.writeObject(playerWarnsCount);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while saving warns to warns.dat file");
        }
    }

    public void loadWarns() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(warnsFile))) {
            //noinspection unchecked
            playerWarnsCount = (Map<UUID, Integer>) objectInputStream.readObject();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while loading warns from warns.dat file");
            Bukkit.getLogger().log(Level.WARNING, "Using empty warns map");
            playerWarnsCount = new HashMap<>();
        }
    }
}
