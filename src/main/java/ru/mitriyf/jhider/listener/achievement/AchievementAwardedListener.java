package ru.mitriyf.jhider.listener.achievement;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

@SuppressWarnings("deprecation")
public class AchievementAwardedListener implements Listener {
    private final Values values;
    private final Utils utils;

    public AchievementAwardedListener(JHider plugin) {
        values = plugin.getValues();
        utils = plugin.getUtils();
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent e) {
        Player player = e.getPlayer();
        if (values.getWorldsList().notContainsWorld(player.getWorld())) {
            return;
        }
        if (values.isMessageAchievement()) {
            utils.sendMessage(player, values.getAAchievement());
        }
    }
}