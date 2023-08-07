package de.ender.parkour;

import de.ender.core.CConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class ParkourTimeManager {
    public static void setBestTime(Player player, long time, String parkour){
        CConfig cConfig = new CConfig("parkour_times",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();

        config.set(parkour+"."+ player.getUniqueId(),time);

        cConfig.save();
        ParkourLeaderboardManager.reloadLeaderboard();
    }
    public static boolean setIfBetter(Player player, long time, String parkour){
        if(!isBetter(player,time,parkour)) return false;
        setBestTime(player, time,parkour);
        return true;
    }
    public static boolean isBetter(Player player, long time, String parkour){
        long bestTime = getBestTime(player,parkour);
        if(bestTime==0) return true;
        return time<bestTime;
    }
    public static long getBestTime(Player player, String parkour){
        CConfig cConfig = new CConfig("parkour_times",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();
        return config.getLong(parkour+"."+ player.getUniqueId());
    }
    public static String getAsString(long time){
        return String.format("%dmin:%ds:%dms",
                TimeUnit.MILLISECONDS.toSeconds(time)/60,
                TimeUnit.MILLISECONDS.toSeconds(time) % 60,
                time % 1000);
    }
}
