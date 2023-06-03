package de.ender.parkour;

import de.ender.core.CConfig;
import de.ender.core.ItemBuilder;
import de.ender.core.gameManagers.TimedGameManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ParkourManager extends TimedGameManager {
    private static final HashMap<Player,ParkourManager> allInstances = new HashMap<>();
    private final Player player;
    public static final ItemStack checkpointItem = new ItemBuilder(Material.IRON_BLOCK, 1).setName(ChatColor.WHITE + "LastCheckpoint").addLore(ChatColor.WHITE + "LastCheckpoint").build();
    public static final ItemStack startItem = new ItemBuilder(Material.GOLD_BLOCK, 1).setName(ChatColor.GOLD + "Start").addLore(ChatColor.GOLD + "Start").build();
    public static final ItemStack cancelItem = new ItemBuilder(Material.REDSTONE_BLOCK, 1).setName(ChatColor.DARK_RED + "Cancel").addLore(ChatColor.DARK_RED + "Cancel").build();
    private int currentCheckpoint;
    private float currentCheckpointYaw;
    private final String name;
    private final boolean couldFly;
    private final @Nullable ItemStack @NotNull [] playerInventory;
    private float startYaw ;

    public ParkourManager(Player player, String name) {
        this.player = player;
        this.name = name;
        this.playerInventory = player.getInventory().getContents();
        this.startYaw = player.getLocation().getYaw();
        this.currentCheckpoint = -1;
        this.currentCheckpointYaw = player.getLocation().getYaw();

        allInstances.put(player,this);
        couldFly = player.getAllowFlight();
    }

    @Override
    public void start() {
        super.start();
        player.setAllowFlight(false);
        player.getInventory().clear();
        giveItems();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(getTimeManager().ended()) cancel();
                else {
                    long time = getTimeManager().timeSinceStart();
                    player.sendActionBar(ChatColor.GREEN + "Time: " + String.format("%dmin:%ds",
                            TimeUnit.MILLISECONDS.toSeconds(time) / 60,
                            TimeUnit.MILLISECONDS.toSeconds(time) % 60));
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0,20);
    }

    public String getName(){
        return name;
    }

    public Player getPlayer(){
        return player;
    }

    public int getCurrentCheckpoint(){
        return currentCheckpoint;
    }
    public void setCurrentCheckpoint(int newCC){
        if(currentCheckpoint != newCC){
            player.sendActionBar(ChatColor.GREEN + "You've reached a checkpoint!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,2,2);
            currentCheckpoint = newCC;
            currentCheckpointYaw = player.getLocation().getYaw();
        }
    }

    public long getTime(){
        if(getTimeManager().ended()) return getTimeManager().totalTime();
        return getTimeManager().timeSinceStart();
    }

    @Override
    public void end() {
        super.end();
        removeItems();

        long time = getTimeManager().timeSinceStart();
        player.sendActionBar(ChatColor.DARK_GREEN+"You ended the parkour. Your time is "+ String.format("%dmin:%ds:%dmil",
                TimeUnit.MILLISECONDS.toSeconds(time)/60,
                TimeUnit.MILLISECONDS.toSeconds(time) % 60,
                time % 1000));

        player.setAllowFlight(couldFly);
        allInstances.remove(player);
        player.getInventory().setContents(playerInventory);
    }
    public void cancel(boolean msg){
        super.end();
        removeItems();
        if(msg) player.sendActionBar(ChatColor.RED+"The parkour was cancelled!");
        player.setAllowFlight(couldFly);
        allInstances.remove(player);
        player.getInventory().setContents(playerInventory);
    }

    public void giveItems() {
        PlayerInventory inv = player.getInventory();
        inv.setHeldItemSlot(3);
        inv.setItem(3,checkpointItem);
        inv.setItem(4,startItem);
        inv.setItem(5,cancelItem);

    }
    public void tpCurrentCheckpoint(){
        CConfig cConfig = new CConfig("parkourLocations",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();
        Location loc = config.getLocation("checkpoint."+name+"."+currentCheckpoint);
        if(loc == null) {
            loc = config.getLocation("start." + name);
            if(loc == null) return;
            loc.setYaw(startYaw);
        } else loc.setYaw(currentCheckpointYaw);
        loc.setPitch(player.getLocation().getPitch());
        loc.add(0.5,0,0.5);
        player.teleport(loc);
    }
    public void tpStart(){
        CConfig cConfig = new CConfig("parkourLocations",Main.getPlugin());
        FileConfiguration config = cConfig.getCustomConfig();
        Location loc = config.getLocation("start."+name);
        if(loc == null) return;
        loc.setYaw(player.getLocation().getYaw());
        loc.setPitch(player.getLocation().getPitch());
        loc.add(0.5,0,0.5);
        player.teleport(loc);
    }

    public void removeItems() {
        Inventory inv = player.getInventory();
        inv.remove(checkpointItem);
        inv.remove(startItem);
        inv.remove(cancelItem);
    }

    public static HashMap<Player, ParkourManager> getAllInstances() {
        return allInstances;
    }
    public static Set<Player> getAllPlayers() {
        return allInstances.keySet();
    }
}
