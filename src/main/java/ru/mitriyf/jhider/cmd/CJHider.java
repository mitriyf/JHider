package ru.mitriyf.jhider.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

public class CJHider implements CommandExecutor {
    private final Values values;
    private final Utils utils;

    public CJHider(JHider plugin) {
        values = plugin.getValues();
        utils = plugin.getUtils();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender.hasPermission("jhider.use")) {
            values.setup(false);
            sender.sendMessage("§aThe config has been reloaded.");
            return false;
        }
        utils.sendMessage(sender, values.getHelp());
        return false;
    }
}
