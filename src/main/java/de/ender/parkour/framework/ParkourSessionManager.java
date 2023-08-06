package de.ender.parkour.framework;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.entity.Player;

public class ParkourSessionManager {
    private static final BiMap<Player,ParkourSession> parkourSessions = HashBiMap.create();
    public static void start(Player player,String parkourName, boolean giveItems){
        if(parkourName==null) return;
        parkourSessions.put(player,new ParkourSession(player,parkourName));
        if(giveItems)ParkourUIManager.giveItems(player);
    }
    public static void end(Player player){
        parkourSessions.get(player).end();
        parkourSessions.remove(player);
        ParkourUIManager.removeItems(player);
    }
    public static void cancel(Player player,boolean removeItems){
        if(!isInParkour(player)) return;
        parkourSessions.get(player).cancel();
        parkourSessions.remove(player);
        if(removeItems)ParkourUIManager.removeItems(player);
    }
    public static ParkourSession getSessionFromPlayer(Player player){
        return parkourSessions.get(player);
    }
    public static boolean isInParkour(Player player){
        return getSessionFromPlayer(player)!=null;
    }

    public static void tpToCheckpoint(Player player) {
        player.teleport(ParkourSessionManager.getSessionFromPlayer(player).getCheckpoint());
    }
    public static void tpToStart(Player player) {
        player.teleport(ParkourSessionManager.getSessionFromPlayer(player).getStartLocation());
    }

    public static void cancelAll() {
        parkourSessions.forEach((player,session)->{
            cancel(player,true);
        });
    }
}
