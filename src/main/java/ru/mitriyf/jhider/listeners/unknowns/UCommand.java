package ru.mitriyf.jhider.listeners.unknowns;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.Values;
import ru.mitriyf.jhider.utils.Utils;

public class UCommand implements Listener {
    private final Utils utils;
    private final Values values;

    public UCommand(JHider plugin) {
        this.values = plugin.getValues();
        this.utils = plugin.getUtils();
    }

    @EventHandler
    private void unknownCommand(UnknownCommandEvent e) {
        if (values.getWorld().check(e.getSender())) {
            return;
        }
        if (!values.getHelp().isEmpty()) {
            e.setMessage(null);
            utils.sendMessage(e.getSender(), values.getHelp());
        }
    }
}
