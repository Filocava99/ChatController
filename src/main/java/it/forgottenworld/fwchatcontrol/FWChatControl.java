package it.forgottenworld.fwchatcontrol;

import it.forgottenworld.fwchatcontrol.config.Config;
import it.forgottenworld.fwchatcontrol.listener.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class FWChatControl extends JavaPlugin {

    private Config config;

    @Override
    public void onEnable() {
        loadConfig();
        registerListeners();
    }

    public Config getPluginConfig() {
        return config;
    }

    private void loadConfig(){
        try {
            config = new Config("config.yml", this);
        }catch (Exception e){
            Bukkit.getLogger().log(Level.SEVERE, "Error loading or creating the plugin config. Disabling " + getName() + ".");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }
}