package ru.mitriyf.jhider.events.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

public class ADoneEvent implements Listener {
    private final Values values;
    private final Utils utils;

    public ADoneEvent(JHider plugin) {
        values = plugin.getValues();
        utils = plugin.getUtils();
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent e) {
        if (values.getWorld().check(e.getPlayer()) || e.getAdvancement().getKey().getKey().split("/")[0].equals("recipes")) {
            return;
        }
        if (values.isMAchievement()) {
            utils.sendMessage(e.getPlayer(), values.getAAchievement());
        }
    }
}