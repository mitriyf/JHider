package ru.mitriyf.jhider.listener;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.PluginManager;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.listener.achievement.AchievementAwardedListener;
import ru.mitriyf.jhider.listener.achievement.AdvancementDoneListener;
import ru.mitriyf.jhider.listener.command.CommandListener;
import ru.mitriyf.jhider.listener.pass.JPiratesPassListener;
import ru.mitriyf.jhider.listener.status.PlayerStatusListener;
import ru.mitriyf.jhider.listener.status.WorldStatusListener;
import ru.mitriyf.jhider.listener.unknown.UnknownCommandListener;
import ru.mitriyf.jhider.listener.unknown.UnknownPreCommandListener;
import ru.mitriyf.jhider.values.Values;

@Getter
public class ListenerManager {
    private final JHider plugin;
    private final Values values;
    private CommandListener commandListener;
    private WorldStatusListener worldListener;
    private UnknownCommandListener unknownCommandListener;
    private AdvancementDoneListener advancementDoneListener;
    private UnknownPreCommandListener unknownPreCommandListener;
    private AchievementAwardedListener achievementAwardedListener;
    @Setter
    private JPiratesPassListener jPiratesPassListener;
    @Setter
    private PlayerStatusListener playerStatusListener;

    public ListenerManager(JHider plugin, Values values) {
        this.plugin = plugin;
        this.values = values;
    }

    public void setup() {
        PluginManager manager = plugin.getServer().getPluginManager();
        setupServerListeners(manager);
        setupBaseListeners(manager);
        int version = plugin.getVersion();
        setupAchievements(manager, version);
        setupUnknownCommandListeners(manager, version);
    }

    private void setupServerListeners(PluginManager manager) {
        if (worldListener == null) {
            worldListener = new WorldStatusListener(plugin);
            manager.registerEvents(worldListener, plugin);
        }
        if (commandListener == null) {
            commandListener = new CommandListener(plugin);
            manager.registerEvents(commandListener, plugin);
        }
    }

    private void setupBaseListeners(PluginManager manager) {
        if (values.isJoin() | values.isQuit() | values.isDeath() | values.isMessageRespawn()) {
            if (playerStatusListener == null) {
                playerStatusListener = new PlayerStatusListener(plugin);
                manager.registerEvents(playerStatusListener, plugin);
            }
            if (values.isJPirates() && jPiratesPassListener == null && manager.getPlugin("JPirates") != null) {
                jPiratesPassListener = new JPiratesPassListener(plugin);
                manager.registerEvents(jPiratesPassListener, plugin);
            }
        }
    }

    private void setupAchievements(PluginManager manager, int version) {
        if (achievementAwardedListener == null && advancementDoneListener == null && values.isMessageAchievement()) {
            if (version < 12) {
                achievementAwardedListener = new AchievementAwardedListener(plugin);
            } else {
                advancementDoneListener = new AdvancementDoneListener(plugin);
            }
            manager.registerEvents(achievementAwardedListener != null ? achievementAwardedListener : advancementDoneListener, plugin);
        }
    }

    private void setupUnknownCommandListeners(PluginManager manager, int version) {
        if (unknownCommandListener == null && unknownPreCommandListener == null && values.isUnknown()) {
            if (version >= 12) {
                unknownCommandListener = new UnknownCommandListener(plugin);
            } else {
                unknownPreCommandListener = new UnknownPreCommandListener(plugin);
            }
            manager.registerEvents(unknownPreCommandListener != null ? unknownPreCommandListener : unknownCommandListener, plugin);
        }
    }
}
