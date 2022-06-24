package it.filippocavallari.chatcontroller.task;

import it.filippocavallari.chatcontroller.ChatController;
import org.bukkit.Bukkit;

public class ReduceWarnTask {

    public ReduceWarnTask(ChatController plugin){
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin,() -> {
            plugin.getWarnController().getPlayerWarnsCount().forEach((uuid, warns) -> {
                plugin.getWarnController().removeWarn(Bukkit.getOfflinePlayer(uuid));
            });
        }, 0, plugin.getSettings().getReduceWarnPeriod());
    }


}
