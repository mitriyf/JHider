package ru.mitriyf.jhider.filter.world.impl;

import org.bukkit.World;
import ru.mitriyf.jhider.filter.world.WorldsList;
import ru.mitriyf.jhider.values.Values;

public class AllowedWorlds implements WorldsList {
    private final Values values;

    public AllowedWorlds(Values values) {
        this.values = values;
    }

    @Override
    public boolean notContainsWorld(World world) {
        return !values.getWorldList().contains(world);
    }
}
