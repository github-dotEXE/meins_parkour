package de.ender.parkour;

import de.ender.core.events.PlayerInventoryChangeEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ParkourListener implements Listener {
    @EventHandler
    public void parkour(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Location location = ParkourLocationManager.get000(event.getTo().clone());
        ParkourSession session = ParkourSessionManager.getSessionFromPlayer(player);
        String parkour = null;
        if(session!=null) parkour = session.getParkour();

        if(ParkourLocationManager.isStartLoc(location)) {
            boolean wasInParkour = ParkourSessionManager.isInParkour(player);
            ParkourSessionManager.cancel(player,event.hasChangedBlock()||!wasInParkour);
            ParkourSessionManager.start(player, ParkourLocationManager.getParkour(location),event.hasChangedBlock()||!wasInParkour);

            if(!wasInParkour) ParkourUIManager.startEffects(player,ParkourSessionManager.getSessionFromPlayer(player).getParkour());
        }
        else if(event.hasChangedPosition()&&ParkourSessionManager.isInParkour(player)&& ParkourLocationManager.isEndFromParkour(
                parkour,location)) {
            ParkourUIManager.endEffects(player,session);
            ParkourSessionManager.end(player);
        }
        else if(ParkourSessionManager.isInParkour(player)&& ParkourLocationManager.isCheckpointFromParkour(
                parkour,location)) {
            if(event.hasChangedBlock()&& !ParkourLocationManager.get000(session.getCheckpoint()).equals(location) )
                ParkourUIManager.checkpointEffects(player);
            session.setCheckpoint(location.clone().setDirection(player.getLocation().getDirection()));
        }
    }
    @EventHandler
    public void parkourItems(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if(ParkourUIManager.getIsParkourItem(item)) {
            event.setCancelled(true);
            ParkourUIManager.useParkourItem(player, item);
        }
    }
    public static void disable(){
        ParkourSessionManager.cancelAll();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        ParkourSessionManager.cancel(player,true);
    }
    @EventHandler
    public void onInventoryChange(PlayerInventoryChangeEvent event) {
        if(ParkourSessionManager.isInParkour(event.getPlayer()))
            event.setCancelled(true);
    }
}
