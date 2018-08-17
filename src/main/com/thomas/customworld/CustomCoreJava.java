package com.thomas.customworld;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import scala.com.thomas.customworld.CustomCore;

public class CustomCoreJava extends JavaPlugin implements Listener {
    private static CustomCoreJava instance;

    public CustomCoreJava() {
        if (instance == null) {
            instance = this;
        } else {
            throw new IllegalStateException("You cannot create another instance of the main class");
        }
    }

    public static CustomCoreJava get() {
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        CustomCore.onEnable();
    }

    @Override
    public void onDisable() {
        CustomCore.onDisable();
    }

    // Event Handlers
    //
    // Every method below needs to be annotated with @EventHandler and call CustomCore.onEventName(event).
    // We need this to be in Java because Spigot uses Java's reflection to call events, and Scala
    // would require a different approach to reflection, which Spigot doesn't support

    @EventHandler
    public void onLogin (PlayerLoginEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onJoin (PlayerJoinEvent event) { CustomCore.ev(event); }
    @EventHandler
    public void onQuit (PlayerQuitEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onChat (AsyncPlayerChatEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onInteract (PlayerInteractEvent event) { CustomCore.ev(event); }
    @EventHandler
    public void onInteractEntity (PlayerInteractEntityEvent event) { CustomCore.ev(event); }
    @EventHandler
    public void onInteractAtEntity (PlayerInteractAtEntityEvent event) { CustomCore.ev(event); }
    @EventHandler
    public void onHangingBreakByEntity (HangingBreakByEntityEvent event) { CustomCore.ev(event); }
    @EventHandler
    public void onHangingBreak (HangingBreakEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onAttack (EntityDamageByEntityEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onEntityExplode (EntityExplodeEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onMove (PlayerMoveEvent event) { CustomCore.ev(event); }
    @EventHandler
    public void onTp (PlayerTeleportEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onPreCommand (PlayerCommandPreprocessEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onFoodLevelChange (FoodLevelChangeEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onDamage (EntityDamageEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onDeath (PlayerDeathEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockPlace (BlockPlaceEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockExplode (BlockExplodeEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockMove (BlockFromToEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockPiston (BlockPistonExtendEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockFade (BlockFadeEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockPhysics (BlockPhysicsEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockDispense (BlockDispenseEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockDecay (LeavesDecayEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockBurn (BlockBurnEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockIgnite (BlockIgniteEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockGrow (BlockGrowEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onLeafDecay (LeavesDecayEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onSpongeAbsorb (SpongeAbsorbEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockRedstone (BlockRedstoneEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockSpread (BlockSpreadEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockFertilize (BlockFertilizeEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockXP (BlockExpEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onBlockSignChange (SignChangeEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onExplosionPrime (ExplosionPrimeEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onItemDamage (PlayerItemDamageEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onVEnter (VehicleEnterEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onVDamage (VehicleDamageEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onVMove (VehicleMoveEvent event) { CustomCore.ev(event); }

    @EventHandler
    public void onVExit (VehicleExitEvent event) { CustomCore.ev(event); }
}
