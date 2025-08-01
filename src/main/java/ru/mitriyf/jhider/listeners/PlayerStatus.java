package ru.mitriyf.jhider.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.Values;
import ru.mitriyf.jhider.utils.Utils;

public class PlayerStatus implements Listener {
    private final JHider plugin;
    private final Values values;
    private final Utils utils;

    public PlayerStatus(JHider plugin) {
        this.plugin = plugin;
        this.values = plugin.getValues();
        this.utils = plugin.getUtils();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!values.isJoin() || values.getWorld().check(e.getPlayer())) {
            return;
        }
        e.setJoinMessage(null);
        if (values.isMJoin()) {
            utils.sendMessage(e.getPlayer(), values.getAJoin());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!values.isQuit() || values.getWorld().check(e.getPlayer())) {
            return;
        }
        e.setQuitMessage(null);
        if (values.isMQuit()) {
            utils.sendMessage(e.getPlayer(), values.getAQuit());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!values.isFastDeath() & !values.isDeath() || values.getWorld().check(e.getEntity())) {
            return;
        }
        if (values.isDeath()) {
            e.setDeathMessage(null);
        }
        if (values.isFastDeath()) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> e.getEntity().spigot().respawn(), 0);
        }
        if (values.isMDeath()) {
            utils.sendMessage(e.getEntity(), values.getADeath());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!values.isMRespawn() || values.getWorld().check(e.getPlayer())) {
            return;
        }
        utils.sendMessage(e.getPlayer(), values.getARespawn());
    }
}
