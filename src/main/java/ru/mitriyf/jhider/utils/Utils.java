package ru.mitriyf.jhider.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.Values;
import ru.mitriyf.jhider.utils.actions.Action;
import ru.mitriyf.jhider.utils.actions.ActionType;

import java.util.List;

@SuppressWarnings("deprecation")
public class Utils {
    private final JHider plugin;
    private final Values values;

    public Utils(JHider plugin) {
        this.plugin = plugin;
        this.values = plugin.getValues();
    }

    public void sendMessage(CommandSender sender, List<Action> actions) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            for (Action action : actions) {
                sendPlayer(p, action);
            }
            return;
        }
        for (Action action : actions) {
            sendSender(sender, action);
        }
    }

    private void sendPlayer(Player p, Action action) {
        ActionType type = action.getType();
        String context = action.getContext().replace("%player%", p.getName());
        if (values.isPlaceholderAPI()) {
            context = PlaceholderAPI.setPlaceholders(p, context);
        }
        switch (type) {
            case PLAYER:
                plugin.getServer().dispatchCommand(p, context);
                break;
            case CONSOLE:
                dispatchConsole(context);
                break;
            case ACTIONBAR:
                sendActionBar(p, context);
                break;
            case BOSSBAR:
                sendBossbar(p, context);
                break;
            case BROADCAST:
                broadcast(context);
                break;
            case TITLE:
                title(p, context);
                break;
            case SOUND:
                playSound(p, context);
                break;
            case LOG:
                log(context);
                break;
            default:
                sendMessage(p, context);
                break;
        }
    }

    private void sendSender(CommandSender sender, Action action) {
        ActionType type = action.getType();
        String context = action.getContext();
        switch (type) {
            case CONSOLE:
                dispatchConsole(context);
                break;
            case BROADCAST:
                broadcast(context);
                break;
            case LOG:
                log(context);
                break;
            case PLAYER:
            case TITLE:
            case ACTIONBAR:
            case BOSSBAR:
            case SOUND:
                break;
            default:
                sendMessage(sender, context);
                break;
        }
    }

    private void playSound(Player p, String s) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String[] spl = s.split(";");
            if (spl.length == 0 || spl.length > 3) {
                plugin.getLogger().warning("Invalid sound. [sound;volume;pitch], error: " + s);
                return;
            }
            Sound sound = Sound.valueOf(spl[0]);
            float volume = spl.length >= 2 ? fFloat(spl[1]) : 1.0F;
            float pitch = spl.length == 3 ? fFloat(spl[2]) : 1.0F;
            p.playSound(p.getLocation(), sound, volume, pitch);
        });
    }

    private void dispatchConsole(String command) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
    }

    private void sendActionBar(Player p, String bar) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (plugin.getVersion() <= 10) {
                plugin.getLogger().warning("Invalid actionbar. [message]. For 1.11+, error: " + bar);
                return;
            }
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(fStr(bar)));
        });
    }

    private void broadcast(String message) {
        String formatted = fStr(message);
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage(formatted);
        }
    }

    private void title(Player p, String title) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String[] t = title.split(";");
            if (t.length == 0 || t.length > 5 || plugin.getVersion() < 8) {
                plugin.getLogger().warning("Invalid title. [title;subtitle;int;int1;int2]. For 1.8+, error: " + title);
                return;
            }
            String title1 = fStr(t[0]);
            String subtitle = t.length >= 2 ? fStr(t[1]) : "";
            if (plugin.getVersion() >= 11) {
                int fadeIn = t.length >= 3 ? fInt(t[2]) : 10;
                int stay = t.length >= 4 ? fInt(t[3]) : 60;
                int fadeOut = t.length == 5 ? fInt(t[4]) : 20;
                p.sendTitle(title1, subtitle, fadeIn, stay, fadeOut);
            } else {
                p.sendTitle(title1, subtitle);
            }
        });

    }

    private void sendBossbar(Player p, String bossbar) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String[] b = bossbar.split(";");
            if (b.length == 0 || b.length > 6 || plugin.getVersion() < 9) {
                plugin.getLogger().warning("Invalid bossbar. [message;color;type;time;style;flag]. For 1.9+, error: " + bossbar);
                return;
            }
            BarColor color = b.length >= 2 ? BarColor.valueOf(b[1].toUpperCase()) : BarColor.WHITE;
            String type = b.length >= 3 ? b[2].toLowerCase() : "time";
            long time = b.length >= 4 ? fInt(b[3]) : 5;
            long ticks = time * 20L;
            BarStyle style = b.length >= 5 ? BarStyle.valueOf(b[4].toUpperCase()) : BarStyle.SEGMENTED_6;
            BarFlag flag = b.length == 6 ? BarFlag.valueOf(b[5].toUpperCase()) : null;
            String text = fStr(b[0].replace("%time%", ticks + ""));
            BossBar bossBar = flag == null ? plugin.getServer().createBossBar(text, color, style) : plugin.getServer().createBossBar(text, color, style, flag);
            bossBar.addPlayer(p);
            if (type.equals("stop")) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    bossBar.removeAll();
                    bossBar.setVisible(false);
                }, ticks);
            } else {
                new BukkitRunnable() {
                    private int t = 0;

                    @Override
                    public void run() {
                        t++;
                        if (t == ticks) {
                            bossBar.removeAll();
                            bossBar.setVisible(false);
                            cancel();
                            return;
                        }
                        int left = (int) (time - Math.floor((double) t / 20));
                        bossBar.setTitle(fStr(b[0].replace("%time%", left + "")));
                        bossBar.setProgress(1 - ((double) t / ticks));
                    }
                }.runTaskTimer(plugin, 1, 1);
            }
        });
    }

    private void sendMessage(CommandSender s, String text) {
        s.sendMessage(fStr(text));
    }

    private void log(String log) {
        plugin.getLogger().info(log);
    }

    private String fStr(String s) {
        return values.getColorizer().colorize(s);
    }

    private Float fFloat(String s) {
        return Float.parseFloat(s);
    }

    private int fInt(String s) {
        return Integer.parseInt(s);
    }
}