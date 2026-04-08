package ru.mitriyf.jhider.listener.pass;

import dev.jdevs.jPirates.api.events.JPiratesJoinEvent;
import dev.jdevs.jPirates.api.events.JPiratesQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

public class JPiratesPassListener implements Listener {
    private final JHider plugin;
    private final Values values;
    private final Utils utils;

    public JPiratesPassListener(JHider plugin) {
        this.plugin = plugin;
        values = plugin.getValues();
        utils = plugin.getUtils();
    }

    @EventHandler
    public void onJPiratesJoin(JPiratesJoinEvent e) {
        Player player = plugin.getServer().getPlayer(e.getUsername());
        if (!values.isJoin() || values.getWorldsList().notContainsWorld(player.getWorld())) {
            return;
        }
        if (values.isMessageJoin() && e.isInWhitelist()) {
            if (player.hasPlayedBefore()) {
                utils.sendMessage(player, values.getAJoin());
            } else {
                utils.sendMessage(player, values.getAFirstJoin());
            }
        }
    }

    @EventHandler
    public void onJPiratesQuit(JPiratesQuitEvent e) {
        Player player = e.getPlayer();
        if (!values.isQuit() || values.getWorldsList().notContainsWorld(player.getWorld())) {
            return;
        }
        if (values.isMessageQuit() && e.isInWhitelist()) {
            utils.sendMessage(player, values.getAQuit());
        }
    }
}
