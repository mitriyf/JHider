package ru.mitriyf.jhider.listener.unknown;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

public class UnknownCommandListener implements Listener {
    private final Utils utils;
    private final Values values;

    public UnknownCommandListener(JHider plugin) {
        values = plugin.getValues();
        utils = plugin.getUtils();
    }

    @EventHandler
    private void onUnknownCommand(UnknownCommandEvent e) {
        CommandSender sender = e.getSender();
        Player player = sender instanceof Player ? (Player) sender : null;
        if (player != null && values.getWorldsList().notContainsWorld(player.getWorld())) {
            return;
        }
        if (!values.getHelp().isEmpty()) {
            e.setMessage(null);
            if (player != null) {
                utils.sendMessage(player, values.getHelp());
            } else {
                utils.sendMessage(sender, values.getHelp());
            }
        }
    }
}
