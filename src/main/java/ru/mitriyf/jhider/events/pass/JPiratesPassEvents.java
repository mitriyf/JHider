package ru.mitriyf.jhider.events.pass;

import dev.jdevs.jPirates.api.events.JPiratesJoinEvent;
import dev.jdevs.jPirates.api.events.JPiratesQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

public class JPiratesPassEvents implements Listener {
    private final JHider plugin;
    private final Values values;
    private final Utils utils;

    public JPiratesPassEvents(JHider plugin) {
        this.plugin = plugin;
        values = plugin.getValues();
        utils = plugin.getUtils();
    }

    @EventHandler
    public void onJoin(JPiratesJoinEvent e) {
        Player player = plugin.getServer().getPlayer(e.getUsername());
        if (!values.isJoin() || values.getWorld().check(player)) {
            return;
        }
        if (values.isMJoin() && e.isInWhitelist()) {
            if (player.hasPlayedBefore()) {
                utils.sendMessage(player, values.getAJoin());
            } else {
                utils.sendMessage(player, values.getAFirstJoin());
            }
        }
    }

    @EventHandler
    public void onQuit(JPiratesQuitEvent e) {
        Player player = e.getPlayer();
        if (!values.isQuit() || values.getWorld().check(player)) {
            return;
        }
        if (values.isMQuit() && e.isInWhitelist()) {
            utils.sendMessage(player, values.getAQuit());
        }
    }
}
