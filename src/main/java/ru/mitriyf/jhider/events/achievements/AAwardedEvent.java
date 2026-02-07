package ru.mitriyf.jhider.events.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

@SuppressWarnings("deprecation")
public class AAwardedEvent implements Listener {
    private final Values values;
    private final Utils utils;

    public AAwardedEvent(JHider plugin) {
        values = plugin.getValues();
        utils = plugin.getUtils();
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