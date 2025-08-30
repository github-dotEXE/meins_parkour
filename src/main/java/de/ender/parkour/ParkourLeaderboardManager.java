package de.ender.parkour;

import com.google.gson.JsonArray;
import de.ender.core.CConfig;
import de.ender.core.floattext.CustomFloatText;
import de.ender.core.floattext.CustomFloatTextManager;
import de.ender.core.floattext.FloatText;
import de.ender.core.floattext.FloatTextManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Display;

import java.util.*;

public class ParkourLeaderboardManager {

    public static void reloadLeaderboard(FloatText floatText) {
        NamespacedKey id = floatText.getId();
        String parkour = id.getKey().replace("leaderboard_","");

        CConfig cConfig = new CConfig("parkour_times", Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();

        HashMap<Long, String> linetimes = new HashMap<>();
        ArrayList<Long> times = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();

        ConfigurationSection parkourpath = config.getConfigurationSection(parkour);
        if (parkourpath == null) return;
        parkourpath.getValues(false).forEach((uuid, time) -> {
            linetimes.put(Long.valueOf((Integer) time), "<aqua>" +
                    Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + "<reset> : <aqua>"
                    + ParkourTimeManager.getAsString(Long.valueOf((Integer) time)));
            times.add(Long.valueOf((Integer) time));
        });
        Collections.sort(times);
        times.forEach((time) -> lines.add(linetimes.get(time)));
        lines.add(0, parkour + ":");
        writeLines(lines, id, floatText);
    }
    private static void writeLines(ArrayList<String> lines,NamespacedKey id,FloatText floatText){
        String text = StringUtils.join(Arrays.copyOf(lines.toArray(),Math.min(10+1,lines.size())),"\n");
        if(FloatTextManager.getByID(id)!=null&&floatText.getEntity()!=null) floatText.setText(text);
    }
    public static void init(){
        ParkourLocationManager.getParkours().forEach((parkour)->{
            NamespacedKey id = new NamespacedKey(Main.getPlugin(),"leaderboard_"+parkour);

            CustomFloatText customFloatText;
            FloatText floatText = FloatTextManager.getByID(id);
            if(floatText!=null&&floatText.getEntity()!=null) {
                reloadLeaderboard(floatText);
                customFloatText = new CustomFloatText(floatText,id);
            }
            else customFloatText = new CustomFloatText("Leaderboard:",id, Display.Billboard.HORIZONTAL,false,false,181,0);
            CustomFloatTextManager.addCustomFloatText(customFloatText);
        });
    }

    public static CustomFloatText getFloatTextByParkour(String parkour) {
        return CustomFloatTextManager.getCustomFloatTextByID(new NamespacedKey(Main.getPlugin(),"leaderboard_"+parkour));
    }
}
