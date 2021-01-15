package it.forgottenworld.fwchatcontrol.task;

import it.forgottenworld.fwchatcontrol.FWChatControl;
import org.bukkit.Bukkit;

public class ReduceWarnTask {

    public ReduceWarnTask(FWChatControl plugin){
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin,() -> {
            plugin.getWarnController().getPlayerWarnsCount().forEach((uuid, warns) -> {
                plugin.getWarnController().removeWarn(Bukkit.getOfflinePlayer(uuid));
            });
        }, 0, plugin.getSettings().getReduceWarnPeriod());
    }


}
