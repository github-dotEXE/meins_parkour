package de.ender.parkour;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ParkourSessionManager {
    private static final BiMap<Player,ParkourSession> parkourSessions = HashBiMap.create();
    public static void start(Player player,String parkourName, boolean giveItems, boolean changeSlot){
        if(parkourName==null) return;
        parkourSessions.put(player,new ParkourSession(player,parkourName));
        if(giveItems)ParkourUIManager.giveItems(player, changeSlot);
    }
    public static void end(Player player){
        ParkourSession session = parkourSessions.get(player);
        session.end();
        if(ParkourTimeManager.isBetter(player,session.getTimer(),parkourSessions.get(player).getParkour())){
            ParkourTimeManager.setBestTime(player, session.getTimer(),parkourSessions.get(player).getParkour());
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE,1,0.5f);
        }
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
    public static void tpToStart(Player player,ParkourSession session) {
        player.teleport(session.getStartLocation());
    }

    public static void cancelAll() {
        parkourSessions.forEach((player,session)-> cancel(player,true));
    }
}
