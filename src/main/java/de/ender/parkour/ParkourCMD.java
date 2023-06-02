package de.ender.parkour;

import de.ender.core.CConfig;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ParkourCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) return false;
        Player  player = (Player) sender;
        Location loc = player.getLocation().toBlockLocation();
        loc.setPitch(0);
        loc.setYaw(0);

        CConfig cConfig = new CConfig("parkourLocations",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();

        switch (args[0]){
            case "checkpoint":
                int p = 0;
                for (int i = 0; i < 1000; i++) {
                    if(config.get("checkpoint."+args[1]+"."+i)==null){
                        p=i;
                        break;
                    }
                }
                config.set("checkpoint."+args[1]+"."+p,loc);
                break;
            default:
                config.set(args[0]+"."+args[1],loc);
                break;

            case "del":
                if (args[1].equals("checkpoint")) {
                    Objects.requireNonNull(config.getConfigurationSection("checkpoint")).getValues(true).forEach((namepath, checkpointloc) -> {
                        if (checkpointloc.equals(loc)) config.set(args[1]+"."+args[2]+"."+namepath.substring(namepath.indexOf(".")+1),null);
                    });
                    break;
                }
                config.set(args[1]+"."+args[2],null);
                break;
        }
        cConfig.save();

        return true;
    }
}
