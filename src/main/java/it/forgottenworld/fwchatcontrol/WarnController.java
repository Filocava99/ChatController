package it.forgottenworld.fwchatcontrol;

import it.forgottenworld.fwchatcontrol.punishment.Punishment;
import it.forgottenworld.fwchatcontrol.punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class WarnController {

    private final Settings settings;
    private final File warnsFile;

    private Map<UUID, Integer> playerWarnsCount;

    public WarnController(FWChatControl chatControl) {
        this.settings = chatControl.getSettings();
        this.warnsFile = new File(chatControl.getDataFolder(), "warns.dat");
        loadWarns();
    }

    public Map<UUID, Integer> getPlayerWarnsCount() {
        return playerWarnsCount;
    }

    public void resetWarn(OfflinePlayer player) {
        playerWarnsCount.remove(player.getUniqueId());
    }

    public void removeWarn(OfflinePlayer player) {
        UUID playerUUID = player.getUniqueId();
        Integer warns = playerWarnsCount.get(playerUUID);
        if (warns != null) {
            warns = Math.max(0, warns - 1);
            playerWarnsCount.put(playerUUID, warns);
        }
    }

    public void warnPlayer(OfflinePlayer player) {
        UUID playerUUID = player.getUniqueId();
        Integer warns = playerWarnsCount.get(playerUUID);
        warns = warns == null ? 1 : warns + 1;
        Player onlinePlayer = Bukkit.getPlayer(playerUUID);
        if (onlinePlayer != null) {
            onlinePlayer.sendMessage(ChatColor.RED + "Sei stato warnato per aver usato una parola vietata!");
            onlinePlayer.sendMessage(ChatColor.RED + "You have a total of " + ChatColor.DARK_RED + warns + ChatColor.RED + " warns.");
        }
        if (settings.getPunishments().containsKey(warns)) {
            punishPlayer(player, warns);
        }
        playerWarnsCount.put(playerUUID, warns);
    }

    public void punishPlayer(OfflinePlayer player, int warns) {
        Punishment punishment = settings.getPunishments().get(warns);
        if (punishment.getType() == PunishmentType.MUTE) {
            Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
            if (onlinePlayer != null) {
                onlinePlayer.sendMessage(ChatColor.RED + "Sei stato mutato temporaneamene per aver raggiunto " + warns + " warn points!");
            }
            mutePlayer(player, punishment.getDuration());
        } else if (punishment.getType() == PunishmentType.KICK) {
            if (player.isOnline()) {
                Bukkit.getPlayer(player.getUniqueId()).kickPlayer("Sei stato kickato per aver raggiunto " + warns + " warn points!");
            }
        } else if (punishment.getType() == PunishmentType.BAN) {
            banPlayer(player, warns);
        }
    }

    private void mutePlayer(OfflinePlayer player, long durationInSeconds) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mute " + player.getName() + " " + durationInSeconds + "s");
    }

    private void banPlayer(OfflinePlayer player, int durationInSeconds) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tempban " + player.getName() + " " + durationInSeconds + "s");
    }

    public void saveWarns() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(warnsFile, false))) {
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
