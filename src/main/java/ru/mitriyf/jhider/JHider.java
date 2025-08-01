package ru.mitriyf.jhider;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.mitriyf.jhider.cmd.CJHider;
import ru.mitriyf.jhider.listeners.Command;
import ru.mitriyf.jhider.listeners.PlayerStatus;
import ru.mitriyf.jhider.listeners.achievements.AAwarded;
import ru.mitriyf.jhider.listeners.achievements.ADone;
import ru.mitriyf.jhider.listeners.unknowns.UCommand;
import ru.mitriyf.jhider.listeners.unknowns.UPreCommand;
import ru.mitriyf.jhider.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class JHider extends JavaPlugin {
    private final ThreadLocalRandom rnd = ThreadLocalRandom.current();
    private final String configVersion = "1.1";
    private int version;
    private Values values;
    private Utils utils;
    private Command command;
    private UCommand uCommand;
    private UPreCommand uPreCommand;
    private PlayerStatus playerStatus;
    private AAwarded aAwarded;
    private ADone aDone;

    @Override
    public void onEnable() {
        registerSettings();
    }

    private void registerSettings() {
        version = getVer();
        values = new Values(this);
        utils = new Utils(this);
        values.checkUpdates();
        values.setup();
    }

    public void registerEvents() {
        PluginManager mgr = getServer().getPluginManager();
        if (command == null) {
            getCommand("jhider").setExecutor(new CJHider(this));
            command = new Command(this);
            mgr.registerEvents(command, this);
        }
        if (uCommand == null && uPreCommand == null && values.isUnknown()) {
            if (version >= 12) {
                uCommand = new UCommand(this);
            } else {
                uPreCommand = new UPreCommand(this);
            }
            mgr.registerEvents(uPreCommand != null ? uPreCommand : uCommand, this);
        }
        if (playerStatus == null && values.isJoin() | values.isQuit() | values.isDeath() | values.isMRespawn()) {
            playerStatus = new PlayerStatus(this);
            mgr.registerEvents(playerStatus, this);
        }
        if (aAwarded == null && aDone == null && values.isMAchievement()) {
            if (version < 12) {
                aAwarded = new AAwarded(this);
            } else {
                aDone = new ADone(this);
            }
            mgr.registerEvents(aAwarded != null ? aAwarded : aDone, this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("To get support for the plugin, write to the discussion from where you got it or write here: vk.com/jdevs");
    }

    private int getVer() {
        String[] ver = getServer().getBukkitVersion().split("\\.");
        if (ver[1].length() >= 2) {
            return Integer.parseInt(ver[1].substring(0, 2));
        } else {
            return Integer.parseInt(ver[1]);
        }
    }
}
