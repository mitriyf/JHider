package ru.mitriyf.jhider.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import ru.mitriyf.jhider.JHider;
import ru.mitriyf.jhider.Values;
import ru.mitriyf.jhider.utils.actions.Action;
import ru.mitriyf.jhider.utils.actions.ActionType;

import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class Utils {
    private final BukkitScheduler scheduler;
    private final JHider plugin;
    private final Values values;

    public Utils(JHider plugin) {
        this.plugin = plugin;
        this.values = plugin.getValues();
        this.scheduler = plugin.getServer().getScheduler();
    }

    public void sendMessage(CommandSender sender, Map<String, List<Action>> actions) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String locale;
            if (plugin.getVersion() <= 12) {
                locale = p.spigot().getLocale().toLowerCase();
            } else {
                locale = p.getLocale().toLowerCase();
            }
            for (Action action : actions.getOrDefault(locale, actions.get(""))) {
                sendPlayer(p, action);
            }
            return;
        }
        for (Action action : actions.get("")) {
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
                dispatchPlayer(p, context);
                break;
            case TELEPORT:
                teleport(p, context);
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
            case EFFECT:
                giveEffect(p, context);
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
            case EFFECT:
            case TELEPORT:
            case SOUND:
                break;
            default:
                sendMessage(sender, context);
                break;
        }
    }

    private void playSound(Player p, String s) {
        scheduler.runTaskAsynchronously(plugin, () -> {
            String[] spl = s.split(";");
            if (spl.length == 0 || spl.length > 4) {
                plugin.getLogger().warning("Invalid sound. [sound;volume;pitch;delay], error: " + s);
                return;
            }
            int later = spl.length == 4 ? fInt(spl[3]) : 0;
            Sound sound = Sound.valueOf(spl[0]);
            float volume = spl.length >= 2 ? fFloat(spl[1]) : 1.0F;
            float pitch = spl.length >= 3 ? fFloat(spl[2]) : 1.0F;
            scheduler.runTaskLaterAsynchronously(plugin, () -> p.playSound(p.getLocation(), sound, volume, pitch), later);
        });
    }

    private void giveEffect(Player p, String s) {
        scheduler.runTaskAsynchronously(plugin, () -> {
            String[] spl = s.split(";");
            if (spl.length == 0 || spl.length > 4) {
                plugin.getLogger().warning("Invalid effect. [type;duration;amplifier;delay], error: " + s);
                return;
            }
            int later = spl.length == 4 ? fInt(spl[3]) : 0;
            PotionEffectType type = PotionEffectType.getByName(spl[0]);
            int duration = spl.length >= 2 ? fInt(spl[1]) : 1;
            int amplifier = spl.length >= 3 ? fInt(spl[2]) : 1;
            PotionEffect effect = new PotionEffect(type, duration, amplifier);
            scheduler.runTaskLater(plugin, () -> p.addPotionEffect(effect), later);
        });
    }

    private void teleport(Player p, String s) {
        scheduler.runTaskAsynchronously(plugin, () -> {
            String[] spl = s.split(";");
            if (spl.length == 0 || spl.length > 7) {
                plugin.getLogger().warning("Invalid location. [world;x;y;z;yaw;pitch;delay], error: " + s);
                return;
            }
            int later = spl.length == 7 ? fInt(spl[6]) : 0;
            World w = Bukkit.getWorld(spl[0]);
            if (w == null) {
                w = Bukkit.getWorld("world");
            }
            double x = spl.length >= 2 ? fDouble(spl[1]) : 0;
            double y = spl.length >= 3 ? fDouble(spl[2]) : 80;
            double z = spl.length >= 4 ? fDouble(spl[3]) : 0;
            float yaw = spl.length >= 5 ? fFloat(spl[4]) : 180;
            float pitch = spl.length >= 6 ? fFloat(spl[5]) : 0;
            Location loc = new Location(w, x, y, z, yaw, pitch);
            scheduler.runTaskLater(plugin, () -> p.teleport(loc), later);
        });
    }

    private void dispatchConsole(String cmd) {
        scheduler.runTaskLater(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd), 0);
    }

    private void dispatchPlayer(Player p, String cmd) {
        scheduler.runTaskLater(plugin, () -> p.performCommand(cmd), 0);
    }

    private void sendActionBar(Player p, String bar) {
        scheduler.runTaskAsynchronously(plugin, () -> {
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
        scheduler.runTaskAsynchronously(plugin, () -> {
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
        scheduler.runTaskAsynchronously(plugin, () -> {
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
            boolean update = b[0].contains("%time%");
            String text = fStr(b[0].replace("%time%", time + ""));
            BossBar bossBar = flag == null ? plugin.getServer().createBossBar(text, color, style) : plugin.getServer().createBossBar(text, color, style, flag);
            bossBar.addPlayer(p);
            if (type.equals("stop")) {
                scheduler.runTaskLaterAsynchronously(plugin, () -> {
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
                        if (update) {
                            bossBar.setTitle(fStr(b[0].replace("%time%", left + "")));
                        }
                        bossBar.setProgress(1 - ((double) t / ticks));
                    }
                }.runTaskTimerAsynchronously(plugin, 1, 1);
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

    private double fDouble(String s) {
        return Double.parseDouble(s);
    }
}