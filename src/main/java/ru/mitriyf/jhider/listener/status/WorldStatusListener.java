package ru.mitriyf.jhider.listener.status;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import ru.mitriyf.jhider.JHider;

public class WorldStatusListener implements Listener {
    private final JHider plugin;

    public WorldStatusListener(JHider plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        updateValues();
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        updateValues();
    }

    private void updateValues() {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getValues().setup(false), 5L);
    }
}
