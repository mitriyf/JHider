package ru.mitriyf.jhider.listener.achievement;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

public class AdvancementDoneListener implements Listener {
    private final Values values;
    private final Utils utils;

    public AdvancementDoneListener(JHider plugin) {
        values = plugin.getValues();
        utils = plugin.getUtils();
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
        Player player = e.getPlayer();
        if (values.getWorldsList().notContainsWorld(player.getWorld()) || e.getAdvancement().getKey().getKey().split("/")[0].equals("recipes")) {
            return;
        }
        if (values.isMessageAchievement()) {
            utils.sendMessage(player, values.getAAchievement());
        }
    }
}