package de.creepz.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mineacademy.fo.annotation.AutoRegister;

@AutoRegister
public final class PickupListener implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;

        //Player player = (Player) entity;
        //Timer timer = Core.getInstance().getTimer();
        event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null);
    }
}
