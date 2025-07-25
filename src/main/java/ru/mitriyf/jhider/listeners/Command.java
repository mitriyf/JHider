package ru.mitriyf.jhider.listeners;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.Values;

public class Command implements Listener {
    private final Values values;

    public Command(JHider plugin) {
        this.values = plugin.getValues();
    }

    @EventHandler
    public void listenerCommand(PlayerCommandPreprocessEvent e) {
        process(e.getPlayer(), e.getMessage());
    }

    @EventHandler
    public void listenerCommand(ServerCommandEvent e) {
        process(e.getSender(), e.getCommand());
    }

    private void process(CommandSender sender, String command) {
        if (sender.isOp() && command.replace("/", "").equalsIgnoreCase("spigot reload")) {
            values.setup();
        }
    }
}
