package de.ender.parkour;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.ender.core.CConfig;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParkourLocationManager {
    private static final BiMap<String, Location> startLocations = HashBiMap.create();
    private static final HashMap<String,Location> endLocations = new HashMap<>();
    private static final HashMap<String, List<Location>> checkpointLocations = new HashMap<>();
    public static void init(){
        CConfig cconfig = new CConfig("parkour",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();
        ConfigurationSection start = config.getConfigurationSection("start");
        ConfigurationSection end = config.getConfigurationSection("end");
        ConfigurationSection checkpoint = config.getConfigurationSection("checkpoint");

        if(start==null||end==null) return;

        start.getValues(false).forEach((parkourName,startLocation)->{
            startLocations.put(parkourName, (Location) startLocation);
        });
        end.getValues(false).forEach((parkourName,endLocation)->{
            endLocations.put(parkourName, (Location) endLocation);
        });
        if(checkpoint!=null) checkpoint.getValues(false).forEach((parkourName,stff)->{
            List<Location> locs = new ArrayList<>();
            checkpoint.getConfigurationSection(parkourName).getValues(false).forEach(
                    (index,location)-> locs.add((Location) location));
            checkpointLocations.put(parkourName, locs);
        });
    }

    public static void addStartLocation(String parkourName,Location location){
        startLocations.put(parkourName,get000(location));
        CConfig cconfig = new CConfig("parkour",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();
        config.set("start."+parkourName,location);
        cconfig.save();
    }
    public static Location getStartLocation(String parkourName){
        return startLocations.get(parkourName);
    }
    public static void addEndLocation(String parkourName,Location location){
        endLocations.put(parkourName,get000(location));
        CConfig cconfig = new CConfig("parkour",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();
        config.set("end."+parkourName,location);
        cconfig.save();
    }
    public static void addCheckpointLocation(String parkourName,Location location){
        List<Location> locs = checkpointLocations.getOrDefault(parkourName,new ArrayList<>());
        locs.add(get000(location));
        checkpointLocations.put(parkourName,locs);
        configList("checkpoint."+parkourName,locs);
    }
    private static void configList(String path,List<Location> list){
        CConfig cconfig = new CConfig("parkour",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        for (int i = 0; i < list.size(); i++) config.set(path + "." + i, list.get(i));

        cconfig.save();
    }
    public static void removeStartLocation(String parkourName){
        startLocations.remove(parkourName);
        CConfig cconfig = new CConfig("parkour",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();
        config.set("start."+parkourName,null);
        cconfig.save();
    }
    public static void removeEndLocation(String parkourName){
        endLocations.remove(parkourName);
        CConfig cconfig = new CConfig("parkour",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();
        config.set("end."+parkourName,null);
        cconfig.save();
    }
    public static void removeCheckpointLocation(String parkourName,Location location){
        List<Location> locs = checkpointLocations.get(parkourName);
        locs.remove(get000(location));
        checkpointLocations.put(parkourName,locs);
        CConfig cconfig = new CConfig("parkour",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();
        config.set("checkpoint."+parkourName,null);
        cconfig.save();
    }
    public static HashMap<Location, String> getAllParkourLocations(String parkourName){
        HashMap<Location,String> locations = new HashMap<>();
        locations.put(startLocations.get(parkourName), "start");
        locations.put(endLocations.get(parkourName), "end");
        List<Location> checkpoints = checkpointLocations.get(parkourName);
        if(checkpoints!=null) for (Location loc : checkpoints) locations.put(loc, "checkpoint");

        return locations;
    }

    public static boolean isStartLoc(Location loc){
        return startLocations.inverse().containsKey(get000(loc));
    }
    public static boolean isCheckpointFromParkour(String parkourName,Location location){
        List<Location> checkpoints = checkpointLocations.get(parkourName);
        if(checkpoints!=null) return checkpoints.contains(get000(location));
        return false;
    }
    public static boolean isEndFromParkour(String parkourName,Location location){
        return endLocations.get(parkourName).equals(get000(location));
    }
    public static boolean isStartFromParkour(String parkourName,Location location){
        return startLocations.get(parkourName).equals(get000(location));
    }
    public static String getParkour(Location loc){
        return startLocations.inverse().get(get000(loc));
    }
    public static Location get000(Location loc){
        loc.setYaw(0);
        loc.setPitch(0);
        loc.set(loc.getBlockX()+0.5,loc.getBlockY()+0.0,loc.getBlockZ()+0.5);
        return loc;
    }

    public static ArrayList<String> getParkours() {
        return new ArrayList<>(startLocations.keySet());
    }
}
