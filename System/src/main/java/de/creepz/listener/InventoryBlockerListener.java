package de.creepz.listener;

import de.creepz.Core;
import de.creepz.util.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.mineacademy.fo.annotation.AutoRegister;

@AutoRegister
public final class InventoryBlockerListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        Timer timer = Core.getInstance().getTimer();

        // Nur blockieren, wenn 10 Minuten um sind
        if (timer.getTime() >= 600) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        Timer timer = Core.getInstance().getTimer();

        if (timer.getTime() >= 600) {
            event.setCancelled(true);
        }
    }
}
