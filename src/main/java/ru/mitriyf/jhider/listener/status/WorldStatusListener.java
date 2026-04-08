package ru.mitriyf.jhider.listener.status;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import ru.mitriyf.jhider.values.Values;

public class WorldStatusListener implements Listener {
    private final Values values;

    public WorldStatusListener(Values values) {
        this.values = values;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        values.getWorldList().add(e.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        values.getWorldList().remove(e.getWorld());
    }
}
