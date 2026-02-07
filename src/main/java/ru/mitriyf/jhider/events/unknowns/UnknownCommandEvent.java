package ru.mitriyf.jhider.events.unknowns;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

public class UnknownCommandEvent implements Listener {
    private final Utils utils;
    private final Values values;

    public UnknownCommandEvent(JHider plugin) {
        values = plugin.getValues();
        utils = plugin.getUtils();
    }

    @EventHandler
    private void unknownCommand(org.bukkit.event.command.UnknownCommandEvent e) {
        if (values.getWorld().check(e.getSender())) {
            return;
        }
        if (!values.getHelp().isEmpty()) {
            e.setMessage(null);
            utils.sendMessage(e.getSender(), values.getHelp());
        }
    }
}
