package ru.mitriyf.jhider.values;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.events.Events;
import ru.mitriyf.jhider.utils.actions.Action;
import ru.mitriyf.jhider.utils.actions.ActionType;
import ru.mitriyf.jhider.utils.colors.Colorizer;
import ru.mitriyf.jhider.utils.colors.impl.LegacyColorizer;
import ru.mitriyf.jhider.utils.colors.impl.MiniMessageColorizer;
import ru.mitriyf.jhider.utils.worlds.World;
import ru.mitriyf.jhider.utils.worlds.impl.BlackList;
import ru.mitriyf.jhider.utils.worlds.impl.WhiteList;
import ru.mitriyf.jhider.values.updater.Updater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Values {
    private final JHider plugin;
    private final Events events;
    private final Logger logger;
    private final Updater updater;
    private final File dataFolder;
    private final File configFile;
    private final Pattern action_pattern = Pattern.compile("\\[(\\w+)] ?(.*)");
    private final Map<String, List<Action>> help = new HashMap<>();
    private final Map<String, List<Action>> aJoin = new HashMap<>();
    private final Map<String, List<Action>> aFirstJoin = new HashMap<>();
    private final Map<String, List<Action>> aAchievement = new HashMap<>();
    private final Map<String, List<Action>> aQuit = new HashMap<>();
    private final Map<String, List<Action>> aDeath = new HashMap<>();
    private final Map<String, List<Action>> aRespawn = new HashMap<>();
    private final String[] lcs = new String[]{"de_DE", "en_US", "ru_RU"};
    private boolean miniMessage, unknown, join, quit, death, fastDeath, placeholderAPI, jPirates, locale, mUnknown, mJoin, mQuit, mDeath, mRespawn, mAchievement;
    private boolean updaterEnabled = true, required = true, release = false;
    private ConfigurationSection settings;
    private FileConfiguration config;
    private List<String> worlds;
    private Colorizer colorizer;
    private World world;

    public Values(JHider plugin) {
        this.plugin = plugin;
        updater = new Updater(plugin, this);
        dataFolder = plugin.getDataFolder();
        configFile = new File(dataFolder, "config.yml");
        logger = plugin.getLogger();
        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            miniMessage = true;
        } catch (Exception e) {
            miniMessage = false;
        }
        events = new Events(plugin, this);
    }

    public void setup(boolean onlineUpdates) {
        getConfigurations();
        updater.checkUpdates(onlineUpdates);
        loadConfigurations();
        clear();
        setupSettings();
        setupMessages();
        events.setup();
    }

    private void setupSettings() {
        String translate = settings.getString("translate").toLowerCase();
        if (miniMessage && translate.equalsIgnoreCase("minimessage")) {
            colorizer = new MiniMessageColorizer();
        } else {
            colorizer = new LegacyColorizer();
        }
        locale = settings.getBoolean("locales");
        ConfigurationSection supports = settings.getConfigurationSection("supports");
        placeholderAPI = supports.getBoolean("placeholderAPI");
        jPirates = supports.getBoolean("jPirates");
        ConfigurationSection w = settings.getConfigurationSection("worlds");
        String wType = w.getString("type").toLowerCase();
        if (wType.equals("whitelist")) {
            world = new WhiteList(plugin);
        } else {
            world = new BlackList(plugin);
        }
        worlds = w.getStringList("worlds");
        if (placeholderAPI && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            plugin.getLogger().warning("The PlaceholderAPI was not detected. This feature will be disabled.");
            placeholderAPI = false;
        }
        ConfigurationSection command = settings.getConfigurationSection("command");
        unknown = command.getBoolean("unknown");
        if (!unknown && events.getUnknownCommandEvent() != null | events.getUnknownPreCommandEvent() != null) {
            HandlerList.unregisterAll(events.getUnknownCommandEvent());
            HandlerList.unregisterAll(events.getUnknownPreCommandEvent());
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
        if (!join && !quit && !death && !mAchievement) {
            if (events.getPlayerStatusEvents() != null) {
                HandlerList.unregisterAll(events.getPlayerStatusEvents());
                events.setPlayerStatusEvents(null);
            }
        }
        if (events.getPlayerStatusEvents() == null || (!jPirates && events.getJPiratesPassEvents() != null)) {
            HandlerList.unregisterAll(events.getJPiratesPassEvents());
            events.setJPiratesPassEvents(null);
        }
    }

    private void setupMessages() {
        setupLocales();
        if (!mUnknown) {
            File file = plugin.getServer().getWorldContainer().getAbsoluteFile();
            YamlConfiguration spigot = YamlConfiguration.loadConfiguration(new File(file, "spigot.yml"));
            help.put("", getActionList(Collections.singletonList(colorizer.colorize(spigot.getString("messages.unknown-command")))));
        }
    }

    private void setupLocales() {
        Map<String, FileConfiguration> locales = new HashMap<>();
        locales.put("", config);
        if (locale) {
            File file = new File(dataFolder, "locales");
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
                    if (f.isFile()) {
                        String name = f.getName();
                        locales.put(name.substring(0, name.indexOf(".")).toLowerCase(), YamlConfiguration.loadConfiguration(f));
                    }
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
            aFirstJoin.put(name, getActionList(messages.getStringList("firstjoin")));
            aJoin.put(name, getActionList(messages.getStringList("join")));
            aQuit.put(name, getActionList(messages.getStringList("quit")));
            aDeath.put(name, getActionList(messages.getStringList("death")));
            aRespawn.put(name, getActionList(messages.getStringList("respawn")));
        }
    }

    private void getConfigurations() {
        saveConfig(configFile);
        loadConfigurations();
        if (settings == null) {
            return;
        }
        ConfigurationSection updater = settings.getConfigurationSection("updater");
        if (updater == null) {
            return;
        }
        updaterEnabled = updater.getBoolean("enabled");
        ConfigurationSection updaterSettings = updater.getConfigurationSection("settings");
        if (updaterSettings == null) {
            return;
        }
        required = updaterSettings.getBoolean("required");
        release = updaterSettings.getBoolean("release");
    }

    private void loadConfigurations() {
        config = YamlConfiguration.loadConfiguration(configFile);
        settings = config.getConfigurationSection("settings");
    }

    private void saveConfig(File file) {
        if (file.exists()) {
            return;
        }
        try {
            plugin.saveResource(file.getName(), true);
        } catch (Exception e) {
            logger.warning("Error save configurations. Error: " + e);
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

    public void deleteDirectory(File f) {
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    delete(file);
                }
            }
            delete(f);
        }
    }

    public void backupConfig(String parentPath, File file, String oldVersion) throws IOException {
        File copied = new File(dataFolder, parentPath + "backups/" + file.getName() + "-" + oldVersion + ".backup");
        Path copiedPath = copied.toPath();
        Files.createDirectories(copied.getParentFile().toPath());
        Files.deleteIfExists(copiedPath);
        Files.copy(file.toPath(), copiedPath);
    }

    private List<Action> getActionList(List<String> actionStrings) {
        ImmutableList.Builder<Action> actionListBuilder = ImmutableList.builder();
        for (String actionString : actionStrings) {
            actionListBuilder.add(fromString(actionString));
        }
        return actionListBuilder.build();
    }

    private void clear() {
        for (Map<String, List<Action>> map : Arrays.asList(help, aJoin, aQuit, aDeath, aRespawn, aFirstJoin, aAchievement)) {
            map.clear();
        }
    }

    public void delete(File f) {
        try {
            Files.delete(f.toPath());
        } catch (IOException ignored) {
        }
    }
}
