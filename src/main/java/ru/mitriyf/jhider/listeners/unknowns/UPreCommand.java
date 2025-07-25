package ru.mitriyf.jhider.listeners.unknowns;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.Values;
import ru.mitriyf.jhider.utils.Utils;

public class UPreCommand implements Listener {
    private final JHider plugin;
    private final Utils utils;
    private final Values values;

    public UPreCommand(JHider plugin) {
        this.plugin = plugin;
        this.values = plugin.getValues();
        this.utils = plugin.getUtils();
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (values.getWorld().check(e.getPlayer())) {
            return;
        }
        String msg = e.getMessage();
        int i = msg.indexOf(":");
        String[] cmd = (i >= 0 ? msg.substring(i + 1) : msg.substring(1)).split(" ");
        if (plugin.getServer().getHelpMap().getHelpTopic("/" + cmd[0]) != null) {
            return;
        }
        if (!values.getHelp().isEmpty()) {
            e.setCancelled(true);
            utils.sendMessage(e.getPlayer(), values.getHelp());
        }
    }
}
