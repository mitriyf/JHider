package ru.mitriyf.jhider.utils.worlds;

import org.bukkit.command.CommandSender;

public interface World {
    default boolean check(CommandSender sender) {
        return false;
    }
}
