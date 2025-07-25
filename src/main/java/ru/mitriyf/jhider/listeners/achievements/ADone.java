package ru.mitriyf.jhider.listeners.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.Values;
import ru.mitriyf.jhider.utils.Utils;

public class ADone implements Listener {
    private final Values values;
    private final Utils utils;

    public ADone(JHider plugin) {
        this.values = plugin.getValues();
        this.utils = plugin.getUtils();
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