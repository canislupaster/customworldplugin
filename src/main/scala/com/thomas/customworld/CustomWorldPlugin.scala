package com.thomas.customworld

import org.bukkit.event.block.{BlockBreakEvent, BlockFadeEvent, BlockPlaceEvent}
import org.bukkit.event.entity.{EntityDamageByEntityEvent, EntityDamageEvent, FoodLevelChangeEvent, PlayerDeathEvent}
import org.bukkit.event.player.{PlayerTeleportEvent, _}
import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import com.thomas.customworld.messaging.{PlayerJoinMessage, PlayerMessage}
import com.thomas.customworld.rank.Rank
import org.bukkit
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.{BanList, GameMode, Material, OfflinePlayer}
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause

object CustomWorldPlugin {
  val plugin: CustomWorldPluginJava = CustomWorldPluginJava.get()
  var cfg: FileConfiguration = plugin.getConfig
  def dbcons:DBConstructor = () => db.MakeDB(cfg)

  def onEnable(): Unit = {
    cfg.addDefaults(configuration.ConfigDefaults)
    cfg.options.copyDefaults(true)
    plugin.saveConfig()

    val opendb = dbcons()
    var statement = opendb.createStatement()
    db.InitializeDB foreach (statement.addBatch(_))
    statement.executeBatch()
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
    rank.addPlayer(dbcons, event.getPlayer, plugin)
    event.setJoinMessage(PlayerJoinMessage(join=true, GetTag(event.getPlayer), event.getPlayer.getDisplayName).renderMessage)
  }

  def onQuit (event: PlayerQuitEvent): Unit = {
    rank.removePlayer(dbcons, event.getPlayer)
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

    if (event.hasBlock && event.getClickedBlock.getType == Material.SIGN_POST) {
      event.getClickedBlock.getState match {
        case x:Sign => minigame.signinteract(plugin, event.getPlayer, x)
        case _ => ()
      }
    }
  }

  def onInteractEntity (event: PlayerInteractEntityEvent): Unit = {
    if (configuration.BlockedEntities contains event.getRightClicked.getType) {
      event.setCancelled(PreventInteraction(event.getPlayer))
    }
  }

  def onBlockBreak (event: BlockBreakEvent): Unit = {
    minigame.ev(event)
  }

  def onPlaceBlock (event: BlockPlaceEvent): Unit = { //TODO: add this to java
    minigame.ev(event)
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

    minigame.ev(event)
  }

  def onDeath(event: PlayerDeathEvent): Unit = {
    minigame.ev(event)
  }

  def onMove(event: PlayerMoveEvent): Unit = {
    minigame.ev(event)
  }

  def onTp(event: PlayerTeleportEvent): Unit = {
    minigame.ev(event)
  }

  def onFoodLevelChange (event: FoodLevelChangeEvent): Unit = {
    event.setFoodLevel(20) //TODO: TEST
    event.setCancelled(true)
  }

  def onItemDamage (event: PlayerItemDamageEvent): Unit = {
    minigame.ev(event)
  }
}