package ru.mitriyf.jhider.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.PluginManager;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.events.achievements.AAwardedEvent;
import ru.mitriyf.jhider.events.achievements.ADoneEvent;
import ru.mitriyf.jhider.events.command.CommandEvents;
import ru.mitriyf.jhider.events.pass.JPiratesPassEvents;
import ru.mitriyf.jhider.events.status.PlayerStatusEvents;
import ru.mitriyf.jhider.events.unknowns.UnknownCommandEvent;
import ru.mitriyf.jhider.events.unknowns.UnknownPreCommandEvent;
import ru.mitriyf.jhider.values.Values;

@Getter
public class Events {
    private final JHider plugin;
    private final Values values;
    private ADoneEvent aDoneEvent;
    private AAwardedEvent aAwardedEvent;
    private CommandEvents commandEvents;
    private UnknownCommandEvent unknownCommandEvent;
    private UnknownPreCommandEvent unknownPreCommandEvent;
    @Setter
    private JPiratesPassEvents jPiratesPassEvents;
    @Setter
    private PlayerStatusEvents playerStatusEvents;

    public Events(JHider plugin, Values values) {
        this.plugin = plugin;
        this.values = values;
    }

    public void setup() {
        PluginManager mgr = plugin.getServer().getPluginManager();
        if (commandEvents == null) {
            commandEvents = new CommandEvents(plugin);
            mgr.registerEvents(commandEvents, plugin);
        }
        int version = plugin.getVersion();
        if (unknownCommandEvent == null && unknownPreCommandEvent == null && values.isUnknown()) {
            if (version >= 12) {
                unknownCommandEvent = new UnknownCommandEvent(plugin);
            } else {
                unknownPreCommandEvent = new UnknownPreCommandEvent(plugin);
            }
            mgr.registerEvents(unknownPreCommandEvent != null ? unknownPreCommandEvent : unknownCommandEvent, plugin);
        }
        if (values.isJoin() | values.isQuit() | values.isDeath() | values.isMRespawn()) {
            if (playerStatusEvents == null) {
                playerStatusEvents = new PlayerStatusEvents(plugin);
                mgr.registerEvents(playerStatusEvents, plugin);
            }
            if (values.isJPirates() && jPiratesPassEvents == null && mgr.getPlugin("JPirates") != null) {
                jPiratesPassEvents = new JPiratesPassEvents(plugin);
                mgr.registerEvents(jPiratesPassEvents, plugin);
            }
        }
        if (aAwardedEvent == null && aDoneEvent == null && values.isMAchievement()) {
            if (version < 12) {
                aAwardedEvent = new AAwardedEvent(plugin);
            } else {
                aDoneEvent = new ADoneEvent(plugin);
            }
            mgr.registerEvents(aAwardedEvent != null ? aAwardedEvent : aDoneEvent, plugin);
        }
    }
}
