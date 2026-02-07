package ru.mitriyf.jhider.utils.common;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;

public class CommonUtils {
    private final BukkitScheduler scheduler;
    private final JHider plugin;
    private final Utils utils;

    public CommonUtils(Utils utils, JHider plugin) {
        this.utils = utils;
        this.plugin = plugin;
        scheduler = plugin.getServer().getScheduler();
    }

    public void broadcast(String message) {
        String text = utils.formatString(message);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(text);
        }
    }

    public void dispatchConsole(String cmd) {
        scheduler.runTaskLater(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd), 0);
    }
}
