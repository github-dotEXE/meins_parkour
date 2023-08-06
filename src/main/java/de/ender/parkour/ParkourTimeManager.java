package de.ender.parkour;

import de.ender.core.CConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class ParkourTimeManager {
    public static void setBestTime(Player player,long time){
        CConfig cConfig = new CConfig("parkour_times",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();

        config.set(player.getUniqueId().toString(),time);

        cConfig.save();
        ParkourLeaderboardManager.reloadLeaderboard();
    }
    public static boolean setIfBetter(Player player, long time){
        if(!isBetter(player,time)) return false;
        setBestTime(player, time);
        return true;
    }
    public static boolean isBetter(Player player,long time){
        long bestTime = getBestTime(player);
        if(bestTime==0) return true;
        return time<bestTime;
    }
    public static long getBestTime(Player player){
        CConfig cConfig = new CConfig("parkour_times",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();
        return config.getLong(player.getUniqueId().toString());
    }
    public static String getAsString(long time){
        return String.format("%dmin:%ds:%dmil",
                TimeUnit.MILLISECONDS.toSeconds(time)/60,
                TimeUnit.MILLISECONDS.toSeconds(time) % 60,
                time % 1000);
    }
}
