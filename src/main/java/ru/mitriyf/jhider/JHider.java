package ru.mitriyf.jhider;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.mitriyf.jhider.cmd.CJHider;
import ru.mitriyf.jhider.utils.Utils;
import ru.mitriyf.jhider.values.Values;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class JHider extends JavaPlugin {
    private final ThreadLocalRandom rnd = ThreadLocalRandom.current();
    private final String configsVersion = "1.2";
    private int version = 13;
    private Values values;
    private Utils utils;

    @Override
    public void onEnable() {
        getLogger().info("Support: https://vk.com/jdevs");
        getVer();
        values = new Values(this);
        utils = new Utils(this);
        utils.setup();
        values.setup(true);
        getCommand("jhider").setExecutor(new CJHider(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        getLogger().info("To get support for the plugin, write to the discussion from where you got it or write here: https://vk.com/jdevs");
    }

    private void getVer() {
        String ver = getServer().getBukkitVersion().split("-")[0].split("\\.")[1];
        if (ver.length() >= 2) {
            version = Integer.parseInt(ver.substring(0, 2));
        } else {
            version = Integer.parseInt(ver);
        }
    }
}
