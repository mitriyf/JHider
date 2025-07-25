package ru.mitriyf.jhider.listeners.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.Values;
import ru.mitriyf.jhider.utils.Utils;

@SuppressWarnings("deprecation")
public class AAwarded implements Listener {
    private final Values values;
    private final Utils utils;

    public AAwarded(JHider plugin) {
        this.values = plugin.getValues();
        this.utils = plugin.getUtils();
    }

    @EventHandler
    public void onAdvancement(PlayerAchievementAwardedEvent e) {
        if (values.getWorld().check(e.getPlayer())) {
            return;
        }
        if (values.isMAchievement()) {
            utils.sendMessage(e.getPlayer(), values.getAAchievement());
        }
    }
}