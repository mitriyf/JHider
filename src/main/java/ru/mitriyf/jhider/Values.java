package ru.mitriyf.jhider;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import ru.mitriyf.jhider.utils.actions.Action;
import ru.mitriyf.jhider.utils.actions.ActionType;
import ru.mitriyf.jhider.utils.colors.CHex;
import ru.mitriyf.jhider.utils.colors.CMiniMessage;
import ru.mitriyf.jhider.utils.colors.Colorizer;
import ru.mitriyf.jhider.utils.worlds.BlackList;
import ru.mitriyf.jhider.utils.worlds.Null;
import ru.mitriyf.jhider.utils.worlds.WhiteList;
import ru.mitriyf.jhider.utils.worlds.World;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Values {
    private final Pattern action_pattern = Pattern.compile("\\[(\\w+)] ?(.*)");
    private final JHider plugin;
    private boolean unknown, join, quit, death, placeholderAPI, mUnknown, mJoin, mQuit, mDeath, mAchievement;
    private List<Action> help, aJoin, aAchievement, aQuit, aDeath;
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

    private void setupSettings() {
        ConfigurationSection settings = plugin.getConfig().getConfigurationSection("settings");
        String translate = settings.getString("translate").toLowerCase();
        if (translate.equals("minimessage")) {
            colorizer = new CMiniMessage();
        } else {
            colorizer = new CHex();
        }
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
        death = player.getBoolean("death");
        ConfigurationSection messages = settings.getConfigurationSection("messages");
        mAchievement = messages.getBoolean("achievement");
        mUnknown = messages.getBoolean("unknown");
        mJoin = messages.getBoolean("join");
        mQuit = messages.getBoolean("quit");
        mDeath = messages.getBoolean("death");
        if (!join && !quit && !death && !mAchievement && plugin.getPlayerStatus() != null) {
            HandlerList.unregisterAll(plugin.getPlayerStatus());
        }
    }

    private void setupMessages() {
        ConfigurationSection messages = plugin.getConfig().getConfigurationSection("messages");
        File file = plugin.getServer().getWorldContainer().getAbsoluteFile();
        YamlConfiguration spigot = YamlConfiguration.loadConfiguration(new File(file, "spigot.yml"));
        help = getActionList(Collections.singletonList(colorizer.colorize(spigot.getString("messages.unknown-command"))));
        if (mUnknown) {
            help = getActionList(messages.getStringList("unknown"));
        }
        aAchievement = getActionList(messages.getStringList("achievement"));
        aJoin = getActionList(messages.getStringList("join"));
        aQuit = getActionList(messages.getStringList("quit"));
        aDeath = getActionList(messages.getStringList("death"));
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
}
