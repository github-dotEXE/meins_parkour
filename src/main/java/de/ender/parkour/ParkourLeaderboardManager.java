package de.ender.parkour;

import de.ender.core.CConfig;
import de.ender.core.floattext.CustomFloatText;
import de.ender.core.floattext.FloatText;
import de.ender.core.floattext.FloatTextManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ParkourLeaderboardManager {
    public static FloatText floatText;
    public static CustomFloatText customfloatText;
    public static void reloadLeaderboard() {
        CConfig cConfig = new CConfig("parkour_times",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();

        ArrayList<String> lines = new ArrayList<>();

        config.getValues(false).forEach((uuid,time)->
                lines.add((Integer) time, Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()+" : "
                        +ParkourTimeManager.getAsString((Integer) time)));
        writeLines(lines);
    }
    private static void writeLines(ArrayList<String> lines){
        String text = StringUtils.join(Arrays.copyOf(lines.toArray(),10),"\\n");
        if(customfloatText.isSpawned()) floatText.setText(text);
    }
    public static void init(){
        NamespacedKey id = new NamespacedKey(Main.getPlugin(),"leaderboard");
        if(FloatTextManager.getByID(id)!=null) {
            floatText = FloatTextManager.getByID(id);
            return;
        }
        customfloatText = new CustomFloatText("",id, Display.Billboard.VERTICAL,false);
    }
}
