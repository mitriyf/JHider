package ru.mitriyf.jhider.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.Values;
import ru.mitriyf.jhider.utils.Utils;

public class PlayerStatus implements Listener {
    private final Values values;
    private final Utils utils;

    public PlayerStatus(JHider plugin) {
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
        if (!values.isDeath() || values.getWorld().check(e.getEntity())) {
            return;
        }
        e.setDeathMessage(null);
        if (values.isMDeath()) {
            utils.sendMessage(e.getEntity(), values.getADeath());
        }
    }
}
