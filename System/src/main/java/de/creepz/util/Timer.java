package de.creepz.util;

import lombok.Getter;
import lombok.Setter;
import de.creepz.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {

    @Getter
    @Setter
    private boolean running; // true or false
    @Getter
    @Setter
    private int time;

    private boolean phaseEnded = false;

    public Timer(boolean running, int time) {
        this.running = running;
        this.time = time;

        run();
    }

    private void sendActionBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!running) {
                player.sendActionBar(ChatColor.RED + "Timer ist pausiert");
                continue;
            }

            String formatted = formatTime(time);
            String timeText = ChatColor.AQUA.toString() + ChatColor.BOLD + formatted;

            player.sendActionBar(timeText);
        }
    }


    private String formatTime(int totalSeconds) {
        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String timePart = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        if (days >= 1) {
            return days + " Tage " + timePart;
        } else {
            return timePart;
        }
    }


    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {

                sendActionBar();

                if (!isRunning())
                    return;

                setTime(getTime() + 1);

                // Wenn genau 600 Sekunden erreicht werden
                if (getTime() == 600 && !phaseEnded) {
                    phaseEnded = true;

                    // Title an alle Spieler senden
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(
                                ChatColor.RED + "Zeit ist um!",
                                ChatColor.GRAY + "Du kannst keine Items mehr looten.",
                                10, 70, 20
                        );
                    }
                }
            }
        }.runTaskTimer(Core.getInstance(), 20, 20);
    }


    public void setRunning(boolean running) {
        this.running = running;
        saveToConfig();
    }

    public void setTime(int time) {
        this.time = time;
        saveToConfig();
    }


    private void saveToConfig() {
        Core.getInstance().getConfig().set("timer.running", this.running);
        Core.getInstance().getConfig().set("timer.time", this.time);

        World world = Bukkit.getWorld("world");
        if (world != null) {
            double borderSize = world.getWorldBorder().getSize();
            Core.getInstance().getConfig().set("border.size", borderSize);
        }

        Core.getInstance().saveConfig();
    }


}
