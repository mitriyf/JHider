package ru.mitriyf.jhider;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import ru.mitriyf.jhider.utils.actions.Action;
import ru.mitriyf.jhider.utils.actions.ActionType;
import ru.mitriyf.jhider.utils.colors.CHex;
import ru.mitriyf.jhider.utils.colors.CMiniMessage;
import ru.mitriyf.jhider.utils.colors.Colorizer;
import ru.mitriyf.jhider.utils.updater.Updater;
import ru.mitriyf.jhider.utils.worlds.BlackList;
import ru.mitriyf.jhider.utils.worlds.Null;
import ru.mitriyf.jhider.utils.worlds.WhiteList;
import ru.mitriyf.jhider.utils.worlds.World;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Values {
    private final JHider plugin;
    private final Pattern action_pattern = Pattern.compile("\\[(\\w+)] ?(.*)");
    private final Map<String, List<Action>> help = new HashMap<>();
    private final Map<String, List<Action>> aJoin = new HashMap<>();
    private final Map<String, List<Action>> aAchievement = new HashMap<>();
    private final Map<String, List<Action>> aQuit = new HashMap<>();
    private final Map<String, List<Action>> aDeath = new HashMap<>();
    private final Map<String, List<Action>> aRespawn = new HashMap<>();
    private final String[] lcs = new String[]{"de_DE", "en_US", "ru_RU"};
    private boolean unknown, join, quit, death, fastDeath, placeholderAPI, locale, mUnknown, mJoin, mQuit, mDeath, mRespawn, mAchievement;
    private List<String> worlds;
    private Colorizer colorizer;
    private World world;

    public Values(JHider plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        setupSettings();
        setupMessages();
        plugin.registerEvents();
    }

    public void checkUpdates() {
        File cfg = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(cfg);
        String ver = plugin.getConfigVersion();
        if (yml.getString("version") == null || !yml.getString("version").contains(ver)) {
            try {
                yml.set("version", ver);
                yml.save(cfg);
                new Updater(plugin).update("config.yml", cfg);
                plugin.getLogger().info("The config has been successfully updated to version " + ver);
            } catch (IOException e) {
                plugin.getLogger().warning("Update config is failed! Error: " + e);
            }
        }
    }

    private void setupSettings() {
        ConfigurationSection settings = plugin.getConfig().getConfigurationSection("settings");
        String translate = settings.getString("translate").toLowerCase();
        if (translate.equals("minimessage")) {
            colorizer = new CMiniMessage();
        } else {
            colorizer = new CHex();
        }
        locale = settings.getBoolean("locales");
        placeholderAPI = settings.getBoolean("placeholderAPI");
        ConfigurationSection w = settings.getConfigurationSection("worlds");
        String wType = w.getString("type").toLowerCase();
        switch (wType) {
            case "whitelist":
                world = new WhiteList(plugin);
                break;
            case "blacklist":
                world = new BlackList(plugin);
                break;
            default:
                world = new Null();
                break;
        }
        worlds = w.getStringList("worlds");
        if (placeholderAPI && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            plugin.getLogger().warning("The PlaceholderAPI was not detected. This feature will be disabled.");
            placeholderAPI = false;
        }
        ConfigurationSection command = settings.getConfigurationSection("command");
        unknown = command.getBoolean("unknown");
        if (!unknown && plugin.getUCommand() != null | plugin.getUPreCommand() != null) {
            HandlerList.unregisterAll(plugin.getUCommand());
            HandlerList.unregisterAll(plugin.getUPreCommand());
        }
        ConfigurationSection player = settings.getConfigurationSection("player");
        join = player.getBoolean("join");
        quit = player.getBoolean("quit");
        ConfigurationSection d = player.getConfigurationSection("deaths");
        death = d.getBoolean("disabled");
        fastDeath = d.getBoolean("fast");
        ConfigurationSection messages = settings.getConfigurationSection("messages");
        mAchievement = messages.getBoolean("achievement");
        mUnknown = messages.getBoolean("unknown");
        mJoin = messages.getBoolean("join");
        mQuit = messages.getBoolean("quit");
        mDeath = messages.getBoolean("death");
        mRespawn = messages.getBoolean("respawn");
        if (!join && !quit && !death && !mAchievement && plugin.getPlayerStatus() != null) {
            HandlerList.unregisterAll(plugin.getPlayerStatus());
        }
    }

    private void setupMessages() {
        clearMessages();
        setupLocales();
        if (!mUnknown) {
            File file = plugin.getServer().getWorldContainer().getAbsoluteFile();
            YamlConfiguration spigot = YamlConfiguration.loadConfiguration(new File(file, "spigot.yml"));
            help.put("", getActionList(Collections.singletonList(colorizer.colorize(spigot.getString("messages.unknown-command")))));
        }
    }

    private void setupLocales() {
        Map<String, FileConfiguration> locales = new HashMap<>();
        locales.put("", plugin.getConfig());
        if (locale) {
            File file = new File(plugin.getDataFolder(), "locales");
            if (!file.exists()) {
                for (String s : lcs) {
                    plugin.saveResource("locales/" + s + ".yml", false);
                }
            }
            File[] dir = file.listFiles();
            if (dir == null) {
                plugin.getLogger().warning("Locales are empty.");
            } else {
                for (File f : dir) {
                    String name = f.getName();
                    locales.put(name.substring(0, name.indexOf(".")).toLowerCase(), YamlConfiguration.loadConfiguration(f));
                }
            }
        }
        for (Map.Entry<String, FileConfiguration> entry : locales.entrySet()) {
            ConfigurationSection messages = entry.getValue().getConfigurationSection("messages");
            String name = entry.getKey();
            if (mUnknown) {
                help.put(name, getActionList(messages.getStringList("unknown")));
            }
            aAchievement.put(name, getActionList(messages.getStringList("achievement")));
            aJoin.put(name, getActionList(messages.getStringList("join")));
            aQuit.put(name, getActionList(messages.getStringList("quit")));
            aDeath.put(name, getActionList(messages.getStringList("death")));
            aRespawn.put(name, getActionList(messages.getStringList("respawn")));
        }
    }

    private Action fromString(String str) {
        Matcher matcher = action_pattern.matcher(str);
        if (!matcher.matches()) {
            return new Action(ActionType.MESSAGE, str);
        }
        ActionType type;
        try {
            type = ActionType.valueOf(matcher.group(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            type = ActionType.MESSAGE;
            return new Action(type, str);
        }
        return new Action(type, matcher.group(2).trim());
    }

    private List<Action> getActionList(List<String> actionStrings) {
        ImmutableList.Builder<Action> actionListBuilder = ImmutableList.builder();
        for (String actionString : actionStrings) {
            actionListBuilder.add(fromString(actionString));
        }
        return actionListBuilder.build();
    }

    private void clearMessages() {
        help.clear();
        aJoin.clear();
        aQuit.clear();
        aDeath.clear();
        aRespawn.clear();
        aAchievement.clear();
    }
}
