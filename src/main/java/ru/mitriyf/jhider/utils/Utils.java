package ru.mitriyf.jhider.utils;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.utils.actions.Action;
import ru.mitriyf.jhider.utils.actions.ActionType;
import ru.mitriyf.jhider.utils.actions.ActionUtils;
import ru.mitriyf.jhider.utils.actions.titles.Title;
import ru.mitriyf.jhider.utils.actions.titles.impl.Title10;
import ru.mitriyf.jhider.utils.actions.titles.impl.Title11;
import ru.mitriyf.jhider.utils.common.CommonUtils;
import ru.mitriyf.jhider.utils.locales.Locale;
import ru.mitriyf.jhider.utils.locales.impl.Locale12;
import ru.mitriyf.jhider.utils.locales.impl.Locale13;
import ru.mitriyf.jhider.values.Values;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Getter
public class Utils {
    private final Values values;
    private final Logger logger;
    private final JHider plugin;
    private final CountDownLatch latch;
    private final CommonUtils commonUtils;
    private final ActionUtils actionUtils;
    private final BukkitScheduler scheduler;
    private boolean actionBar = false, bar = false, tit = false;
    private Locale locale;
    private Title title;

    public Utils(JHider plugin) {
        this.plugin = plugin;
        values = plugin.getValues();
        logger = plugin.getLogger();
        latch = new CountDownLatch(1);
        scheduler = plugin.getServer().getScheduler();
        actionUtils = new ActionUtils(this, plugin);
        commonUtils = new CommonUtils(this, plugin);
    }

    public void setup() {
        int version = plugin.getVersion();
        if (version < 13) {
            if (version < 8) {
                tit = true;
            }
            if (version < 9) {
                bar = true;
            }
            locale = new Locale12();
        } else {
            locale = new Locale13();
        }
        if (version < 11) {
            actionBar = true;
            if (!tit) {
                title = new Title10();
            }
        } else {
            title = new Title11();
        }
    }

    public void sendMessage(CommandSender sender, Map<String, List<Action>> actions) {
        scheduler.runTaskAsynchronously(plugin, () -> {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                for (Action action : actions.getOrDefault(locale.player(p), actions.get(""))) {
                    sendPlayer(p, action);
                }
                return;
            }
            for (Action action : actions.get("")) {
                sendSender(sender, action);
            }
        });
    }

    private void sendPlayer(Player p, Action action) {
        ActionType type = action.getType();
        String context = replaceEach(action.getContext(), new String[]{"%player%", "%world%"}, new String[]{p.getName(), p.getWorld().getName()});
        if (values.isPlaceholderAPI()) {
            context = PlaceholderAPI.setPlaceholders(p, context);
        }
        switch (type) {
            case PLAYER: {
                actionUtils.dispatchPlayer(p, context);
                break;
            }
            case TELEPORT: {
                actionUtils.teleportPlayer(p, context);
                break;
            }
            case CONSOLE: {
                commonUtils.dispatchConsole(context);
                break;
            }
            case ACTIONBAR: {
                actionUtils.sendActionBar(p, context);
                break;
            }
            case CONNECT: {
                actionUtils.connect(p, context);
                break;
            }
            case BOSSBAR: {
                actionUtils.sendBossbar(p, context);
                break;
            }
            case BROADCAST: {
                commonUtils.broadcast(context);
                break;
            }
            case TITLE: {
                actionUtils.sendTitle(p, context);
                break;
            }
            case SOUND: {
                actionUtils.playSound(p, context);
                break;
            }
            case EFFECT: {
                actionUtils.giveEffect(p, context);
                break;
            }
            case EXPLOSION: {
                actionUtils.createExplosion(p, context);
                break;
            }
            case LOG: {
                log(context);
                break;
            }
            case DELAY: {
                try {
                    if (latch.await(Integer.parseInt(context) * 50L, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                } catch (Exception ignored) {
                }
                break;
            }
            default: {
                sendMessage(p, context);
                break;
            }
        }
    }

    private void sendSender(CommandSender sender, Action action) {
        ActionType type = action.getType();
        String context = action.getContext();
        switch (type) {
            case CONSOLE: {
                commonUtils.dispatchConsole(context);
                break;
            }
            case BROADCAST: {
                commonUtils.broadcast(context);
                break;
            }
            case LOG: {
                log(context);
                break;
            }
            case DELAY: {
                try {
                    if (latch.await(Integer.parseInt(context) * 50L, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                } catch (Exception ignored) {
                }
                break;
            }
            case PLAYER:
            case TITLE:
            case ACTIONBAR:
            case BOSSBAR:
            case EFFECT:
            case TELEPORT:
            case SOUND:
            case CONNECT:
            case EXPLOSION:
                break;
            default:
                sendMessage(sender, context);
                break;
        }
    }

    private String replaceEach(String text, String[] searchList, String[] replacementList) {
        if (text.isEmpty() || searchList == null || replacementList == null) {
            return text;
        }
        final StringBuilder result = new StringBuilder(text);
        for (int i = 0; i < searchList.length; i++) {
            final String search = searchList[i];
            final String replacement = replacementList[i];
            int start = 0;
            while ((start = result.indexOf(search, start)) != -1) {
                result.replace(start, start + search.length(), replacement);
                start += replacement.length();
            }
        }

        return result.toString();
    }

    private void sendMessage(CommandSender sender, String text) {
        sender.sendMessage(formatString(text));
    }

    private void sendMessage(Player player, String text) {
        player.sendMessage(formatString(text));
    }

    public String formatString(String s) {
        return values.getColorizer().colorize(s);
    }

    public Float formatFloat(String s) {
        return Float.parseFloat(s);
    }

    public int formatInt(String s) {
        return Integer.parseInt(s);
    }

    public double formatDouble(String s) {
        return Double.parseDouble(s);
    }

    public boolean formatBoolean(String s) {
        return Boolean.parseBoolean(s);
    }

    private void log(String log) {
        logger.info(log);
    }
}
