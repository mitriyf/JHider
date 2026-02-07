package ru.mitriyf.jhider.utils.worlds.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.worlds.World;
import ru.mitriyf.jhider.values.Values;

public class BlackList implements World {
    private final Values values;

    public BlackList(JHider plugin) {
        values = plugin.getValues();
    }

    @Override
    public boolean check(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String world = p.getWorld().getName();
            for (String w : values.getWorlds()) {
                if (world.equals(w)) {
                    return true;
                }
            }
        }
        return false;
    }
}
