package com.thomas.customworld

import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.entity.{EntityDamageByEntityEvent, EntityDamageEvent}
import org.bukkit.event.player._
import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import com.thomas.customworld.messaging.{PlayerJoinMessage, PlayerMessage}
import com.thomas.customworld.rank.Rank
import org.bukkit
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.{BanList, GameMode, Material, OfflinePlayer}
import org.bukkit.entity.Player

object CustomWorldPlugin {
  val plugin: CustomWorldPluginJava = CustomWorldPluginJava.get()
  var cfg: FileConfiguration = plugin.getConfig
  def dbcons:DBConstructor = () => db.MakeDB(cfg)

  def onEnable(): Unit = {
    cfg.addDefaults(configuration.ConfigDefaults)
    cfg.options.copyDefaults(true)
    plugin.saveConfig()

    val opendb = dbcons()
    opendb.createStatement().execute(db.InitializeDB)
    opendb.close()

    messaging.LoadConfig(cfg)
    minigame.InitializeMinigames(plugin)

    commands.RegisterCommands(plugin, dbcons)

    plugin.getLogger.info("Successfully enabled and initialized database!")
  }

  def onDisable(): Unit = {
    plugin.getLogger.info("Disabling...")
  }

  def onLogin (event: PlayerLoginEvent): Unit = {
    val db = new PlayerDB(dbcons())

    db.UpdateUser(event.getPlayer.getUniqueId, event.getPlayer.getName)

    if (event.getPlayer.isBanned && (db.GetRank(event.getPlayer.getUniqueId) HasPermission "noban")) {
      plugin.getServer.getBanList(BanList.Type.NAME).pardon(event.getPlayer.getName)
      event.allow()
    }

    db close()
  }

  def CommandPreprocess(): Unit = {
    //TODO: CMDSPY
  }

  def GetTag (player: Player): Rank = {
    new PlayerDB(dbcons()) autoClose(_ GetRank player.getUniqueId)
  }

  def onJoin (event: PlayerJoinEvent): Unit = {
    rank.AddPlayer(dbcons, event.getPlayer, plugin)
    event.setJoinMessage(PlayerJoinMessage(join=true, GetTag(event.getPlayer), event.getPlayer.getDisplayName).renderMessage)
  }

  def onQuit (event: PlayerQuitEvent): Unit = {
    rank.RemovePlayer(dbcons, event.getPlayer)
    minigame.leave(event.getPlayer)
    event.setQuitMessage(PlayerJoinMessage(join=false, GetTag(event.getPlayer), event.getPlayer.getDisplayName).renderMessage)
  }

  def onChat (event: AsyncPlayerChatEvent): Unit = {
    PlayerMessage(GetTag(event.getPlayer), event.getPlayer.getDisplayName, event.getMessage).broadCast(_ => true, plugin.getServer)
    event.setCancelled(true)
  }

  def PreventInteraction (player:Player): Boolean = {
    !player.hasPermission("blocks")
  }

  def onInteract (event: PlayerInteractEvent): Unit = {
    if ((event.hasBlock && (configuration.BlockedBlocks contains event.getClickedBlock.getType))
        || (event.getMaterial != null && (configuration.BlockedBlocks contains event.getMaterial))) {
      event.setCancelled(PreventInteraction(event.getPlayer))
    }

    event.getClickedBlock match {
      case x:Sign => minigame.interact(plugin, event.getPlayer, x)
      case _ => ()
    }
  }

  def onInteractEntity (event: PlayerInteractEntityEvent): Unit = {
    if (configuration.BlockedEntities contains event.getRightClicked.getType) {
      event.setCancelled(PreventInteraction(event.getPlayer))
    }
  }

  def onAttack(event: EntityDamageByEntityEvent): Unit = {
    (event.getDamager, event.getEntity) match {
      case (x:Player, y:Player) if x.getGameMode == GameMode.CREATIVE =>
        if (!x.hasPermission ("creativepvp")) {
          x.sendMessage("Creative PvP is not allowed!")
          event.setCancelled(true)
        }
      case _ => ()
    }
  }

  def onDamage(event: EntityDamageEvent): Unit = {
    if (event.getCause == EntityDamageEvent.DamageCause.VOID) {
      event.getEntity.setFallDistance(0)
      event.getEntity.teleport(event.getEntity.getWorld.getSpawnLocation)
      event.setCancelled(true)
    }
  }
}