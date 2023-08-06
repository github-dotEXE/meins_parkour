package de.ender.parkour;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class ParkourSession {
    private final Player player;
    private final long startTime;
    private long endTime = -1;
    private final String parkour;
    private Location checkpoint;
    private final BukkitTask bukkitRunnable;
    private final boolean allowFlight;
    private final Location startLocation;
    public ParkourSession(Player player,String parkourName){
        this.player = player;
        startTime = System.currentTimeMillis();
        this.parkour = parkourName;
        allowFlight = player.getAllowFlight();
        player.setAllowFlight(false);
        ParkourUIManager.showToPlayer(player,parkour,1.2f,15,0.1);
        startLocation=ParkourLocationManager.getStartLocation(parkour).clone().setDirection(player.getLocation().getDirection());
        checkpoint = startLocation.clone();
        bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(ParkourSession.this.isCancelled()) cancel();
                player.sendActionBar(ChatColor.GREEN + "Time: " + String.format("%dmin:%ds",
                        TimeUnit.MILLISECONDS.toSeconds(getTimer()) / 60,
                        TimeUnit.MILLISECONDS.toSeconds(getTimer()) % 60));
            }
        }.runTaskTimer(Main.getPlugin(),0,20);
    }
    public void end(){
        if(!isEnded()&&!isCancelled()) {
            endTime = System.currentTimeMillis();
            player.setAllowFlight(allowFlight);
            bukkitRunnable.cancel();
            ParkourUIManager.cancelShowToPlayer(player);
        }
    }
    public void cancel(){
        if(!isCancelled()) {
            bukkitRunnable.cancel();
            ParkourUIManager.cancelShowToPlayer(player);
            player.setAllowFlight(allowFlight);
            endTime = 0;
        }
    }
    public void setCheckpoint(Location location){
        if(ParkourLocationManager.isCheckpointFromParkour(parkour,location))
            checkpoint = location.setDirection(player.getLocation().getDirection());
    }
    public Location getCheckpoint(){
        return checkpoint;
    }
    public Location getStartLocation(){
        return startLocation;
    }
    public boolean isEnded(){
        return endTime!=-1;
    }
    public boolean isCancelled(){
        return endTime==0;
    }
    public long getTimer(){
        if(!(isEnded()||isCancelled())) return System.currentTimeMillis()-startTime;
        return endTime-startTime;
    }
    public Player getPlayer(){
        return player;
    }
    public String getParkour(){
        return parkour;
    }
}
