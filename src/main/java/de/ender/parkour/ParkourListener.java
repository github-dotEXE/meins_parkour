package de.ender.parkour;

import de.ender.core.CConfig;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class ParkourListener implements Listener {
    @EventHandler
    public void onMovement(PlayerMoveEvent event) {
        CConfig cConfig = new CConfig("parkourLocations",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();

        Location loc = event.getTo().toBlockLocation();
        loc.setPitch(0);
        loc.setYaw(0);
        Player player = event.getPlayer();

        if(!config.getValues(false).keySet().containsAll(Arrays.asList("start","checkpoint","end"))) return;
        Map<String, Object> checkpoints = config.getConfigurationSection("checkpoint").getValues(true);
        Map<String, Object> starts = config.getConfigurationSection("start").getValues(false);
        Map<String, Object> ends = config.getConfigurationSection("end").getValues(false);

        if(checkpoints.containsValue(loc)){
            ParkourManager pm = ParkourManager.getAllInstances().get(player);
            if(pm == null) return;
            checkpoints.forEach((namepath, checkpointloc) -> {
                if (checkpointloc.equals(loc))
                    pm.setCurrentCheckpoint(Integer.parseInt(namepath.substring(namepath.indexOf(".") + 1)));
            });
        } else if(starts.containsValue(loc)){
            for (Map.Entry<String, Object> entry : starts.entrySet()) {
                if (entry.getValue().equals(loc)) {
                    if (ParkourManager.getAllPlayers().contains(player))
                        ParkourManager.getAllInstances().get(player).cancel(false);
                    new ParkourManager(player, entry.getKey()).start();
                    break;
                }
            }
        } else if(ends.containsValue(loc)){
            ParkourManager pm = ParkourManager.getAllInstances().get(player);
            if(pm == null) return;
            ends.forEach((name, endloc) -> {
                if (endloc.equals(loc)&& pm.getName().equals(name)) pm.end();
            });
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        ParkourManager pm = ParkourManager.getAllInstances().get(player);

        if(item==null||pm == null) return;

        if(item.equals(ParkourManager.checkpointItem)) {
            pm.tpCurrentCheckpoint();
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,2,2);
            event.setCancelled(true);
        } else if(item.equals(ParkourManager.cancelItem)) {
            pm.cancel(true);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
            event.setCancelled(true);
        } else if(item.equals(ParkourManager.startItem)) {
            pm.tpStart();
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,3,3);
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        ParkourManager pm = ParkourManager.getAllInstances().get(player);
        if(pm == null) return;
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
        pm.cancel(false);
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(event.getItemDrop().getItemStack().equals(ParkourManager.startItem) ||
                event.getItemDrop().getItemStack().equals(ParkourManager.checkpointItem) ||
                event.getItemDrop().getItemStack().equals(ParkourManager.cancelItem))
            event.setCancelled(true);
    }
    @EventHandler
    public void onInventoryInteraction(InventoryClickEvent event){
        if(Objects.equals(event.getCurrentItem(), ParkourManager.startItem) ||
                Objects.equals(event.getCurrentItem(), ParkourManager.cancelItem) ||
                Objects.equals(event.getCurrentItem(), ParkourManager.checkpointItem))
            event.setCancelled(true);
    }
}
