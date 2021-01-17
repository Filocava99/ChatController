package it.forgottenworld.fwchatcontrol;

import com.earth2me.essentials.Essentials;
import it.forgottenworld.fwchatcontrol.command.AdminCommand;
import it.forgottenworld.fwchatcontrol.config.Config;
import it.forgottenworld.fwchatcontrol.listener.ChatListener;
import it.forgottenworld.fwchatcontrol.punishment.Punishment;
import it.forgottenworld.fwchatcontrol.punishment.PunishmentType;
import it.forgottenworld.fwchatcontrol.task.ReduceWarnTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.regex.Pattern;

public final class FWChatControl extends JavaPlugin {

    private static FWChatControl INSTANCE;

    private final Settings settings = new Settings();
    private WarnController warnController;
    private Essentials essentials;

    @Override
    public void onEnable() {
        INSTANCE = this;
        loadConfig();
        instantiateControllers();
        registerCommands();
        registerListeners();
        registerTasks();
    }

    public static FWChatControl getINSTANCE() {
        return INSTANCE;
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
            e.printStackTrace();
        }
    }

    public void saveConfig(){
        try {
            Config config = new Config("config.yml", this);
            saveData(config);
            config.save();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while saving the plugin config.");
        }
    }

    private void loadData(Config config) {
        settings.setMaxMessagesPerSecond(config.getConfig().getInt("maxMessagesPerSecond"));
        settings.setWarnIfFlooding(config.getConfig().getBoolean("warnIfFlooding"));
        settings.setMaxCapsCharPercentage(config.getConfig().getInt("maxCapsCharPercentage"));
        settings.setWarnIfUsingCaps(config.getConfig().getBoolean("warnIfCapsing"));
        settings.setWarnIfUsingBannedWords(config.getConfig().getBoolean("warnIfUsingBannedWords"));
        settings.setReduceWarnPeriod(config.getConfig().getInt("reduceWarnPeriod"));
        loadPunishments(config);
        loadRegexes(config);
    }

    private void loadRegexes(Config config) {
        config.getConfig().getStringList("bannedWords").forEach(regex -> settings.getRegexes().add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE)));
    }

    private void loadPunishments(Config config){
        config.getConfig().getConfigurationSection("punishments").getKeys(false).forEach(warns -> {
            ConfigurationSection punishmentSection = config.getConfig().getConfigurationSection("punishments."+warns);
            Punishment punishment = new Punishment(PunishmentType.valueOf(punishmentSection.getString("type")),punishmentSection.getInt("duration"));
            settings.addPunishment(Integer.parseInt(warns), punishment);
        });
    }

    private void instantiateControllers(){
        warnController = new WarnController(this);
    }

    private void registerCommands(){
        getCommand("fwcc").setExecutor(new AdminCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    private  void registerTasks(){
        new ReduceWarnTask(this);
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

    private void saveData(Config config){
        FileConfiguration configuration = config.getConfig();
        configuration.set("maxMessagesPerSecond", settings.getMaxMessagesPerSecond());
        configuration.set("warnIfFlooding",settings.isWarnIfFlooding());
        configuration.set("maxCapsCharPercentage", settings.getMaxCapsCharPercentage());
        configuration.set("warnIfCapsing", settings.isWarnIfUsingCaps());
        configuration.set("warnIfUsingBannedWords",settings.isWarnIfUsingBannedWords());
        ConfigurationSection punishmentsSection = configuration.createSection("punishments");
        settings.getPunishments().forEach((warns, punishment) -> {
            ConfigurationSection punishmentSection = punishmentsSection.createSection(String.valueOf(warns));
            punishmentSection.set("type", punishment.getType().toString());
            punishmentSection.set("duration", punishment.getDuration());
        });
        configuration.set("bannedWords", settings.getRegexes().stream().map(regex -> regex.pattern()).toArray(String[]::new));
    }
}