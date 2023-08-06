package de.ender.parkour;

import de.ender.core.Log;
import de.ender.core.TabCompleter;
import de.ender.core.UpdateChecker;
import de.ender.parkour.framework.ParkourCMD;
import de.ender.parkour.framework.ParkourListener;
import de.ender.parkour.framework.ParkourLocationManager;
import de.ender.parkour.framework.ParkourUIManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class Main extends JavaPlugin {
    private static Main plugin;

    @Override
    public void onEnable() {
        Log.enable(this);
        new UpdateChecker(this,"master").check().downloadLatestMeins();
        plugin = this;

        getServer().getPluginManager().registerEvents(new ParkourListener(),this);
        getCommand("parkour").setExecutor(new ParkourCMD());
        getCommand("parkour").setTabCompleter(new TabCompleter()
                .addCompI(1,"set","add","remove","show","list","colors")
                .addPathedComp("set","start","end")
                .addPathedComp("add","checkpoint")
                .addMultiPathedComp(new String[]{"checkpoint", "start", "end"},"remove","colors.setcolor")
                .addMultiPathedComp(ParkourLocationManager::getParkours,
                        "set.x","add.checkpoint","remove.x","show","list")
                .addMultiPathedComp(ParkourUIManager::getAllColorModes,"colors.setmode","colors.setcolor.x")
                .addPathedComp("colors","setmode","setcolor")
                .addMultiPathedComp(new String[]{"0","255"}, "colors.setcolor.x.x","colors.setcolor.x.x.x",
                        "colors.setcolor.x.x.x.x","colors.setcolor.x.x.x.x.x","colors.setcolor.x.x.x.x.x.x",
                        "colors.setcolor.x.x.x.x.x.x.x") //colors.setcolor."type"."mode"."r1"."g1"."b1"."r2"."g2"."b2"
                .addPathedComp("show","cancel")
        );
        ParkourLocationManager.init();


    }

    @Override
    public void onDisable() {
        Log.disable(this);

        ParkourListener.disable();
    }
    public static Main getPlugin(){
        return plugin;
    }
}
