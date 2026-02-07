package ru.mitriyf.jhider.events.status;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.events.Events;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

public class PlayerStatusEvents implements Listener {
    private final JHider plugin;
    private final Values values;
    private final Events events;
    private final Utils utils;

    public PlayerStatusEvents(JHider plugin) {
        this.plugin = plugin;
        values = plugin.getValues();
        events = values.getEvents();
        utils = plugin.getUtils();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!values.isJoin() || values.getWorld().check(player)) {
            return;
        }
        e.setJoinMessage(null);
        if (values.isMJoin() && events.getJPiratesPassEvents() == null) {
            if (player.hasPlayedBefore()) {
                utils.sendMessage(player, values.getAJoin());
            } else {
                utils.sendMessage(player, values.getAFirstJoin());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (!values.isQuit() || values.getWorld().check(player)) {
            return;
        }
        e.setQuitMessage(null);
        if (values.isMQuit() && events.getJPiratesPassEvents() == null) {
            utils.sendMessage(player, values.getAQuit());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player entity = e.getEntity();
        if (!values.isFastDeath() & !values.isDeath() || values.getWorld().check(entity)) {
            return;
        }
        if (values.isDeath()) {
            e.setDeathMessage(null);
        }
        if (values.isFastDeath()) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> entity.spigot().respawn(), 0);
        }
        if (values.isMDeath()) {
            utils.sendMessage(entity, values.getADeath());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (!values.isMRespawn() || values.getWorld().check(player)) {
            return;
        }
        utils.sendMessage(player, values.getARespawn());
    }
}
