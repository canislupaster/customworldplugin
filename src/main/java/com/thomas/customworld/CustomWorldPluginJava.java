package com.thomas.customworld;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomWorldPluginJava extends JavaPlugin implements Listener {
    private static CustomWorldPluginJava instance;

    public CustomWorldPluginJava() {
        if (instance == null) {
            instance = this;
        } else {
            throw new IllegalStateException("You cannot create another instance of the main class");
        }
    }

    public static CustomWorldPluginJava get() {
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        CustomWorldPlugin.onEnable();
    }

    @Override
    public void onDisable() {
        CustomWorldPlugin.onDisable();
    }

    // Event Handlers
    //
    // Every method below needs to be annotated with @EventHandler and call CustomWorldPlugin.onEventName(event).
    // We need this to be in Java because Spigot uses Java's reflection to call events, and Scala
    // would require a different approach to reflection, which Spigot doesn't support

    @EventHandler
    public void onLogin (PlayerLoginEvent event) { CustomWorldPlugin.onLogin(event); }

    @EventHandler
    public void onJoin (PlayerJoinEvent event) { CustomWorldPlugin.onJoin(event); }
    @EventHandler
    public void onQuit (PlayerQuitEvent event) { CustomWorldPlugin.onQuit(event); }

    @EventHandler
    public void onChat (AsyncPlayerChatEvent event) { CustomWorldPlugin.onChat(event); }

    @EventHandler
    public void onInteract (PlayerInteractEvent event) { CustomWorldPlugin.onInteract(event); }
    @EventHandler
    public void onInteractEntity (PlayerInteractEntityEvent event) { CustomWorldPlugin.onInteractEntity(event); }

    @EventHandler
    public void onAttack (EntityDamageByEntityEvent event) { CustomWorldPlugin.onAttack(event); }

    @EventHandler
    public void onMove (PlayerMoveEvent event) { CustomWorldPlugin.onMove(event); }
    @EventHandler
    public void onTp (PlayerTeleportEvent event) { CustomWorldPlugin.onTp(event); }

    @EventHandler
    public void onPreCommand (PlayerCommandPreprocessEvent event) { CustomWorldPlugin.onPreCommand(event); }

    @EventHandler
    public void onFoodLevelChange (FoodLevelChangeEvent event) { CustomWorldPlugin.onFoodLevelChange(event); }

    @EventHandler
    public void onDamage (EntityDamageEvent event) { CustomWorldPlugin.onDamage(event); }

    @EventHandler
    public void onDeath (PlayerDeathEvent event) { CustomWorldPlugin.onDeath(event); }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) { CustomWorldPlugin.onBlockBreak(event); }

    @EventHandler
    public void onBlockPlace (BlockPlaceEvent event) { CustomWorldPlugin.onBlockPlace(event); }

    @EventHandler
    public void onExplosionPrime (ExplosionPrimeEvent event) { CustomWorldPlugin.onExplosionPrime(event); }

    @EventHandler
    public void onItemDamage (PlayerItemDamageEvent event) { CustomWorldPlugin.onItemDamage(event); }

    @EventHandler
    public void onVEnter (VehicleEnterEvent event) { CustomWorldPlugin.onVEnter(event); }

    @EventHandler
    public void onVDamage (VehicleDamageEvent event) { CustomWorldPlugin.onVDamage(event); }

    @EventHandler
    public void onVMove (VehicleMoveEvent event) { CustomWorldPlugin.onVMove(event); }

    @EventHandler
    public void onVExit (VehicleExitEvent event) { CustomWorldPlugin.onVExit(event); }
}
