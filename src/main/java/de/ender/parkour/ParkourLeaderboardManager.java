package de.ender.parkour;

import de.ender.core.CConfig;
import de.ender.core.floattext.CustomFloatText;
import de.ender.core.floattext.CustomFloatTextManager;
import de.ender.core.floattext.FloatText;
import de.ender.core.floattext.FloatTextManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Display;

import java.util.*;

public class ParkourLeaderboardManager {
    private static final NamespacedKey id = new NamespacedKey(Main.getPlugin(),"leaderboard");
    public static FloatText floatText;
    public static CustomFloatText customfloatText;
    public static void reloadLeaderboard() {
        CConfig cConfig = new CConfig("parkour_times",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();

        HashMap<Long,String> linetimes = new HashMap<>();
        ArrayList<Long> times = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();

        config.getValues(false).forEach((uuid,time)-> {
            linetimes.put(Long.valueOf((Integer) time),ChatColor.DARK_AQUA+
                    Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()+ChatColor.RESET+" : "+ChatColor.AQUA
                    +ParkourTimeManager.getAsString(Long.valueOf((Integer) time)));
            times.add(Long.valueOf((Integer) time));
        });
        Collections.sort(times);
        times.forEach((time)-> lines.add(linetimes.get(time)));
        lines.add(0, "Leaderboard:");
        writeLines(lines);
    }
    private static void writeLines(ArrayList<String> lines){
        String text = StringUtils.join(Arrays.copyOf(lines.toArray(),Math.min(10+1,lines.size())),"\\n");
        if(FloatTextManager.getByID(id)!=null||customfloatText.isSpawned()) floatText.setText(text);
    }
    public static void init(){
        if(FloatTextManager.getByID(id)!=null) {
            floatText = FloatTextManager.getByID(id);
            reloadLeaderboard();
            return;
        }
        customfloatText = new CustomFloatText("leaderboard",id, Display.Billboard.VERTICAL,false);
        CustomFloatTextManager.addCustomFloatText(customfloatText);
    }
}
