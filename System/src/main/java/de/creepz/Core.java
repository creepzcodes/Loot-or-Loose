package de.creepz;

import de.creepz.util.Timer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.mineacademy.fo.plugin.SimplePlugin;

public class Core extends SimplePlugin {

    @Getter
    private static Core instance;

    @Getter
    private Timer timer;


    @Override
    protected void onPluginStart() {
        instance = this;

        int time = getConfig().getInt("timer.time", 0);
        boolean running = getConfig().getBoolean("timer.running", false);
        this.timer = new Timer(running, time);
    }

    @Override
    protected void onPluginStop() {
        for (World world : Bukkit.getWorlds()) {
            world.save();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.saveData();
        }
    }
}
