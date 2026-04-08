package ru.mitriyf.jhider.listener.command;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.values.Values;

public class CommandListener implements Listener {
    private final Values values;

    public CommandListener(JHider plugin) {
        values = plugin.getValues();
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        process(e.getPlayer(), e.getMessage());
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent e) {
        process(e.getSender(), e.getCommand());
    }

    private void process(CommandSender sender, String command) {
        if (sender.isOp() && command.replace("/", "").equalsIgnoreCase("spigot reload")) {
            values.setup(false);
        }
    }
}
