package de.ender.parkour;

import de.ender.core.CConfig;
import de.ender.core.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class ParkourUIManager {
    private static final HashMap<Player,ItemStack[]> inventorys = new HashMap<>();
    private static final ItemStack checkpointItem = new ItemBuilder(Material.IRON_BLOCK,1).setName("<white>Checkpoint").build();
    private static final ItemStack startItem = new ItemBuilder(Material.GOLD_BLOCK,1).setName("<gold>Start").build();
    private static final ItemStack cancelItem = new ItemBuilder(Material.REDSTONE_BLOCK,1).setName("<dark_red>Cancel").build();
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    public static void giveItems(Player player, boolean changeSlot){
        inventorys.put(player,player.getInventory().getContents().clone());
        player.getInventory().clear();
        if (changeSlot) player.getInventory().setHeldItemSlot(4);
        player.getInventory().setItem(4,checkpointItem);
        player.getInventory().setItem(3,startItem);
        player.getInventory().setItem(5,cancelItem);
    }
    public static void removeItems(Player player){
        player.getInventory().setContents(inventorys.get(player));
        inventorys.remove(player);
    }
    public static boolean getIsParkourItem(ItemStack item){
        if(item == null) return false;
        return item.equals(checkpointItem)||item.equals(startItem)||item.equals(cancelItem);
    }
    public static void useParkourItem(Player player,ItemStack item){
        if(item.equals(checkpointItem)) {
            ParkourSession session = ParkourSessionManager.getSessionFromPlayer(player);
            if (ParkourLocationManager.isCheckpointFromParkour(session.getParkour(), player.getLocation())) return;
            ParkourSessionManager.tpToCheckpoint(player);
            ParkourUIManager.checkpointEffects(player);
        }
        else if(item.equals(startItem)) {
            ParkourSession session = ParkourSessionManager.getSessionFromPlayer(player);
            if (ParkourLocationManager.isStartFromParkour(session.getParkour(), player.getLocation())) return;
            ParkourSessionManager.cancel(player,true);
            ParkourSessionManager.tpToStart(player,session);
            //ParkourUIManager.startEffects(player,ParkourSessionManager.getSessionFromPlayer(player).getParkour());
        }
        else if(item.equals(cancelItem)) {
            ParkourUIManager.cancelEffects(player,ParkourSessionManager.getSessionFromPlayer(player).getParkour());
            ParkourSessionManager.cancel(player,true);
        }
    }

    private static final HashMap<Player, BukkitTask> tasks = new HashMap<>();

    public static boolean cancelShowToPlayer(Player player){
        if(tasks.containsKey(player)) {
            tasks.get(player).cancel();
            tasks.remove(player);
            return true;
        }
        return false;
    }
    public static void showToPlayer(Player player,String parkourName,float size,int count,double offset) {
        cancelShowToPlayer(player);
        tasks.put(player, new BukkitRunnable() {
            @Override
            public void run() {
                ParkourLocationManager.getAllParkourLocations(parkourName).forEach((location,type)->{
                    player.spawnParticle(Particle.DUST_COLOR_TRANSITION, location, count, offset, offset, offset,
                            new Particle.DustTransition(getColor(type,1), getColor(type,2),size));
                });
            }
        }.runTaskTimer(Main.getPlugin(),0,5));
    }
    public static void changeColorMode(String mode){
        CConfig cconfig = new CConfig("parkour_config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        config.set("mode",mode);

        cconfig.save();
    }
    public static ArrayList<String> getAllColorModes(){
        CConfig cconfig = new CConfig("parkour_config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();
        return new ArrayList<>(config.getConfigurationSection("start").getValues(false).keySet());
    }
    public static String getColorMode(){
        CConfig cconfig = new CConfig("parkour_config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        return config.getString("mode","1");
    }
    public static Color getColor(String type,int index){
        CConfig cconfig = new CConfig("parkour_config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        return config.getColor(type+"."+getColorMode()+"."+index,Color.WHITE);
    }
    public static void setColor(String type,int index,String colormode,Color color){
        CConfig cconfig = new CConfig("parkour_config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        config.set(type+"."+colormode+"."+index,color);

        cconfig.save();
    }

    public static void startEffects(Player player,String parkourName){
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1.5f);
        player.sendMessage(miniMessage.deserialize("<green>Started parkour '"+parkourName+"'"));
    }
    public static void endEffects(Player player,ParkourSession session){
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,0.5f);
        player.sendMessage(miniMessage.deserialize("<dark_green>Ended parkour '"+session.getParkour()+"' in "
                +ParkourTimeManager.getAsString(session.getTimer())));
    }
    public static void checkpointEffects(Player player){
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,2);
    }
    public static void cancelEffects(Player player,String parkourName){
        player.playSound(player.getLocation(), Sound.ENTITY_CAT_HURT,1,1);
        player.sendMessage(miniMessage.deserialize("<red>Canceled parkour '"+parkourName+"'"));
    }

}
