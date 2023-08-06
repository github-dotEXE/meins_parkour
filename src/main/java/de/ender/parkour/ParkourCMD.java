package de.ender.parkour;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ParkourCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)||!sender.hasPermission("parkour.command.parkour")) {
            sender.sendMessage(ChatColor.RED+"Something went Wrong!");
            return false;
        }
        Player player = (Player) sender;
        Location loc = ParkourLocationManager.get000(player.getLocation().clone());
        switch(args[0]){
            case "set":
                if(args[1].equals("end")) ParkourLocationManager.addEndLocation(args[2], loc);
                else if(args[1].equals("start")) ParkourLocationManager.addStartLocation(args[2], loc);
                else sender.sendMessage(ChatColor.RED+"Something went Wrong!");
                break;
            case "add":
                if(args[1].equals("checkpoint")) ParkourLocationManager.addCheckpointLocation(args[2], loc);
                else sender.sendMessage(ChatColor.RED+"Something went Wrong!");
                break;
            case "remove":
                switch (args[1]) {
                    case "end":
                        ParkourLocationManager.removeEndLocation(args[2]);
                        break;
                    case "start":
                        ParkourLocationManager.removeStartLocation(args[2]);
                        break;
                    case "checkpoint":
                        ParkourLocationManager.removeCheckpointLocation(args[2], loc);
                        break;
                }
                break;
            case "show":
                if(args[1].equals("cancel")) ParkourUIManager.cancelShowToPlayer(player);
                else ParkourUIManager.showToPlayer(player,args[1],2,10,0.2);
                break;
            case "list":
                player.sendMessage(String.valueOf(ParkourLocationManager.getAllParkourLocations(args[1])));
                break;
            case "colors":
                switch (args[1]){
                    case "setmode":
                        ParkourUIManager.changeColorMode(args[2]);
                        break;
                    case "setcolor":
                        ParkourUIManager.setColor(args[2],1,args[3],
                                Color.fromRGB(Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6])));
                        ParkourUIManager.setColor(args[2],2,args[3],
                                Color.fromRGB(Integer.parseInt(args[7]), Integer.parseInt(args[8]), Integer.parseInt(args[9])));
                        break;
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED+"Something went Wrong!");
                break;
        }
        return true;
    }
}
