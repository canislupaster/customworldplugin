package com.thomas.customworld

import org.bukkit.event.block.{BlockBreakEvent, BlockFadeEvent, BlockPlaceEvent}
import org.bukkit.event.entity._
import org.bukkit.event.player.{PlayerTeleportEvent, _}
import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import com.thomas.customworld.messaging.{ErrorMsg, PlayerJoinMessage, PlayerMessage}
import com.thomas.customworld.player.ip
import com.thomas.customworld.player.rank.Rank
import org.bukkit
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit._
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import org.bukkit.event.vehicle.{VehicleDamageEvent, VehicleEnterEvent, VehicleExitEvent, VehicleMoveEvent}
import com.thomas.customworld.util._
import org.bukkit.event.player

object CustomWorldPlugin {
  val plugin: CustomWorldPluginJava = CustomWorldPluginJava.get()
  var cfg: FileConfiguration = plugin.getConfig
  def dbcons:DBConstructor = () => db.MakeDB(cfg)

  def onEnable(): Unit = {
    cfg.addDefaults(configuration.ConfigDefaults)
    cfg.options.copyDefaults(true)
    plugin.saveConfig()

    val opendb = dbcons()
    val statement = opendb.createStatement()
    db.InitializeDB foreach (statement.addBatch(_))
    statement.executeBatch()
    opendb.close()

    messaging.LoadConfig(cfg)
    minigame.InitializeMinigames(plugin)
    commands.RegisterCommands(plugin, dbcons)
    freeop.initialize(plugin)

    plugin.getLogger.info("Successfully enabled and initialized database!")
    plugin.getServer.getOnlinePlayers forEach (player.join(_))
  }

  def onDisable(): Unit = {
    plugin.getLogger.info("Disabling...")
    minigame.stop()
  }

  def onLogin (event: PlayerLoginEvent): Unit = {
    val db = new PlayerDB()

    db.updateUser(event.getPlayer.getUniqueId, event.getPlayer.getName)

    ip.login(event)
    if ((event.getResult == PlayerLoginEvent.Result.KICK_BANNED)
      && (db.getRank(event.getPlayer.getUniqueId) HasPermission "noban")) {
      event.allow()
    }

    db close()
  }

  def onPreCommand(event: PlayerCommandPreprocessEvent): Unit = {
    messaging.CommandMessage(event.getPlayer.getName, event.getMessage).broadCast(x => x.hasPermission("cmdspy") && x != event.getPlayer)(plugin.getServer)
    ip.ev(event)
  }

  def GetTag (tagplayer: Player): Rank = player.getPlayer(tagplayer).rank

  def onJoin (event: PlayerJoinEvent): Unit = {
    player.join(event.getPlayer)
    event.getPlayer.teleport(event.getPlayer.getWorld.getSpawnLocation)
    event.setJoinMessage(PlayerJoinMessage(join=true, GetTag(event.getPlayer), event.getPlayer.getName).renderMessage)
  }

  def onQuit (event: PlayerQuitEvent): Unit = {
    event.setQuitMessage(PlayerJoinMessage(join=false, GetTag(event.getPlayer), event.getPlayer.getName).renderMessage)
    minigame.leave(event.getPlayer)
    player.leave(event.getPlayer)
  }

  def onChat (event: AsyncPlayerChatEvent): Unit = {
    if (event.getPlayer.hasPermission("talk")) {
      event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage))
      event.setFormat(PlayerMessage(GetTag(event.getPlayer), event.getPlayer).renderMessage)
    } else {
      ErrorMsg("muted") sendClient event.getPlayer
      event.setCancelled(true)
    }
  }

  def PreventInteraction (player:Player): Boolean = {
    !player.hasPermission("blocks")
  }

  def onInteract (event: PlayerInteractEvent): Unit = {
    if ((event.hasBlock && (configuration.BlockedBlocks contains event.getClickedBlock.getType))
        || (event.getMaterial != null && (configuration.BlockedBlocks contains event.getMaterial))) {
      event.setCancelled(PreventInteraction(event.getPlayer))
    }

    if (event.hasBlock && event.getClickedBlock.getType == Material.SIGN_POST) {
      event.getClickedBlock.getState match {
        case x:Sign => minigame.signinteract(plugin, event.getPlayer, x)
        case _ => ()
      }
    }

    freeop.ev(event)
    minigame.ev(event)
    ip.ev(event)
  }

  def onInteractEntity (event: PlayerInteractEntityEvent): Unit = {
    if (configuration.BlockedEntities contains event.getRightClicked.getType) {
      event.setCancelled(PreventInteraction(event.getPlayer))
    }

    freeop.ev(event)
    minigame.ev(event)
    ip.ev(event)
  }

  def onBlockBreak (event: BlockBreakEvent): Unit = {
    minigame.ev(event)
    freeop.ev(event)
  }

  def onBlockPlace (event: BlockPlaceEvent): Unit = {
    minigame.ev(event)
    freeop.ev(event)
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
    minigame.ev(event)
  }

  def onDamage(event: EntityDamageEvent): Unit = {
    if (event.getCause == EntityDamageEvent.DamageCause.VOID && event.getEntity.isInstanceOf[Player]) {
      event.getEntity.setFallDistance(0)
      event.getEntity.teleport(event.getEntity.getWorld.getSpawnLocation)
      event.setCancelled(true)
    }

    minigame.ev(event)
  }

  def onDeath(event: PlayerDeathEvent): Unit = {
    minigame.ev(event)
  }

  def onMove(event: PlayerMoveEvent): Unit = {
    freeop.ev(event)
    minigame.ev(event)
    ip.ev(event)
  }

  def onTp(event: PlayerTeleportEvent): Unit = {
    player.getPlayer(event.getPlayer).beforeTp = Some(event.getFrom)
    minigame.ev(event)
    ip.ev(event)
  }

  def onFoodLevelChange (event: FoodLevelChangeEvent): Unit = {
    event.setCancelled(true)
    event.setFoodLevel(20)
    minigame.ev(event)
  }

  def onItemDamage (event: PlayerItemDamageEvent): Unit = {
    minigame.ev(event)
  }

  def onExplosionPrime (event: ExplosionPrimeEvent): Unit = {
    if (!cfg.getBoolean("freeop.explosions"))
      event.setCancelled(true)
    minigame.ev(event)
  }

  def onVEnter (vehicleEnterEvent: VehicleEnterEvent): Unit = minigame.ev(vehicleEnterEvent)
  def onVDamage (vehicleDamageEvent: VehicleDamageEvent): Unit = minigame.ev(vehicleDamageEvent)
  def onVMove (vehicleMoveEvent: VehicleMoveEvent): Unit = {
    freeop.ev(vehicleMoveEvent)
    minigame.ev(vehicleMoveEvent)
  }
  def onVExit (vehicleExitEvent: VehicleExitEvent): Unit = minigame.ev(vehicleExitEvent)

  //TODO: make all these event handlers a big pattern match
}