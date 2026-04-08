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
    private WorldStatusListener worldStatusListener;
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
        PluginManager mgr = plugin.getServer().getPluginManager();
        if (worldStatusListener == null) {
            worldStatusListener = new WorldStatusListener(values);
            mgr.registerEvents(worldStatusListener, plugin);
        }
        if (commandListener == null) {
            commandListener = new CommandListener(plugin);
            mgr.registerEvents(commandListener, plugin);
        }
        int version = plugin.getVersion();
        if (unknownCommandListener == null && unknownPreCommandListener == null && values.isUnknown()) {
            if (version >= 12) {
                unknownCommandListener = new UnknownCommandListener(plugin);
            } else {
                unknownPreCommandListener = new UnknownPreCommandListener(plugin);
            }
            mgr.registerEvents(unknownPreCommandListener != null ? unknownPreCommandListener : unknownCommandListener, plugin);
        }
        if (values.isJoin() | values.isQuit() | values.isDeath() | values.isMessageRespawn()) {
            if (playerStatusListener == null) {
                playerStatusListener = new PlayerStatusListener(plugin);
                mgr.registerEvents(playerStatusListener, plugin);
            }
            if (values.isJPirates() && jPiratesPassListener == null && mgr.getPlugin("JPirates") != null) {
                jPiratesPassListener = new JPiratesPassListener(plugin);
                mgr.registerEvents(jPiratesPassListener, plugin);
            }
        }
        if (achievementAwardedListener == null && advancementDoneListener == null && values.isMessageAchievement()) {
            if (version < 12) {
                achievementAwardedListener = new AchievementAwardedListener(plugin);
            } else {
                advancementDoneListener = new AdvancementDoneListener(plugin);
            }
            mgr.registerEvents(achievementAwardedListener != null ? achievementAwardedListener : advancementDoneListener, plugin);
        }
    }
}
