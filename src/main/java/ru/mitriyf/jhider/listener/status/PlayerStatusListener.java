package ru.mitriyf.jhider.listener.status;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.listener.ListenerManager;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

public class PlayerStatusListener implements Listener {
    private final JHider plugin;
    private final Values values;
    private final ListenerManager listenerManager;
    private final Utils utils;

    public PlayerStatusListener(JHider plugin) {
        this.plugin = plugin;
        values = plugin.getValues();
        listenerManager = values.getListenerManager();
        utils = plugin.getUtils();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        boolean isJoin = values.isJoin();
        boolean isMJoin = values.isMessageJoin();
        if (!isJoin & !isMJoin || values.getWorldsList().notContainsWorld(player.getWorld())) {
            return;
        }
        if (isJoin) {
            e.setJoinMessage(null);
        }
        if (isMJoin && listenerManager.getJPiratesPassListener() == null) {
            if (player.hasPlayedBefore()) {
                utils.sendMessage(player, values.getAJoin());
            } else {
                utils.sendMessage(player, values.getAFirstJoin());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        boolean isQuit = values.isQuit();
        boolean isMQuit = values.isMessageQuit();
        if (!isQuit & !isMQuit || values.getWorldsList().notContainsWorld(player.getWorld())) {
            return;
        }
        if (isQuit) {
            e.setQuitMessage(null);
        }
        if (isMQuit && listenerManager.getJPiratesPassListener() == null) {
            utils.sendMessage(player, values.getAQuit());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player entity = e.getEntity();
        boolean isDeath = values.isDeath();
        boolean isMDeath = values.isMessageDeath();
        boolean isFastDeath = values.isFastDeath();
        if (!isFastDeath & !isDeath & !isMDeath || values.getWorldsList().notContainsWorld(entity.getWorld())) {
            return;
        }
        if (isDeath) {
            e.setDeathMessage(null);
        }
        if (isFastDeath) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> entity.spigot().respawn(), 0);
        }
        if (isMDeath) {
            utils.sendMessage(entity, values.getADeath());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (!values.isMessageRespawn() || values.getWorldsList().notContainsWorld(player.getWorld())) {
            return;
        }
        utils.sendMessage(player, values.getARespawn());
    }
}
