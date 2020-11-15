package it.forgottenworld.fwchatcontrol;

import com.earth2me.essentials.Essentials;
import it.forgottenworld.fwchatcontrol.config.Config;
import it.forgottenworld.fwchatcontrol.listener.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.regex.Pattern;

public final class FWChatControl extends JavaPlugin {

    private final Settings settings = new Settings();
    private WarnController warnController;
    private Essentials essentials;

    @Override
    public void onEnable() {
        loadConfig();
        warnController = new WarnController(this);
        registerListeners();
    }

    public Settings getSettings() {
        return settings;
    }

    public WarnController getWarnController() {
        return warnController;
    }

    public Essentials getEssentials() {
        return essentials;
    }

    private void loadConfig() {
        try {
            Config config = new Config("config.yml", this);
            loadData(config);
            hookEssentials();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error loading or creating the plugin config. Disabling " + getName() + ".");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadData(Config config) {
        settings.setMaxMessagesPerSecond(config.getConfig().getInt("maxMessagesPerSecond"));
        settings.setWarnIfFlooding(config.getConfig().getBoolean("warnIfFlooding"));
        settings.setMaxCapsCharPercentage(config.getConfig().getInt("maxCapsCharPercentage"));
        settings.setWarnIfCapsing(config.getConfig().getBoolean("warnIfCapsing"));
        settings.setWarnIfUsingBannedWords(config.getConfig().getBoolean("warnIfUsingBannedWords"));
        loadRegexes(config);
    }

    private void loadRegexes(Config config) {
        config.getConfig().getStringList("bannedWords").forEach(regex -> settings.getRegexes().add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE)));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    private void hookEssentials() {
        try {
            essentials = getPlugin(Essentials.class);
            getLogger().log(Level.INFO, "Hooked into Essentials");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error while hooking into Essentials. Disabling FWChatControl...");
            getPluginLoader().disablePlugin(this);
        }
    }
}