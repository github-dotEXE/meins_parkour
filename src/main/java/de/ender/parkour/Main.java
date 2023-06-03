package de.ender.parkour;

import de.ender.core.Log;
import de.ender.core.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main plugin;

    @Override
    public void onEnable() {
        Log.log("Enabling Meins Parkour...");
        getServer().getPluginManager().registerEvents(new ParkourListener(),this);
        getCommand("parkour").setExecutor(new ParkourCMD());
        new UpdateChecker(this,"master").check().downloadLatestMeins();
        plugin = this;
    }

    @Override
    public void onDisable() {
        Log.log("Disabling Meins Parkour...");
        ParkourManager.getAllInstances().values().forEach(parkourManager -> parkourManager.cancel(true));
    }
    public static Main getPlugin(){
        return plugin;
    }
}
