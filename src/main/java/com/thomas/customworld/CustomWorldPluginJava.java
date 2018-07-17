package com.thomas.customworld;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
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
    public void onDamage (EntityDamageEvent event) { CustomWorldPlugin.onDamage(event); }
}
