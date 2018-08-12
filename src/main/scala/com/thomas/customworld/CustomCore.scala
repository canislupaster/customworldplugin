package scala.com.thomas.customworld

import com.thomas.customworld.CustomCoreJava
import org.bukkit.event.block.{BlockBreakEvent, BlockFadeEvent, BlockPlaceEvent}
import org.bukkit.event.entity._
import org.bukkit.event.player.{PlayerTeleportEvent, _}

import scala.com.thomas.customworld.db.{DBConstructor, PlayerDB}
import scala.com.thomas.customworld.messaging.{ErrorMsg, PlayerJoinMessage, PlayerMessage}
import scala.com.thomas.customworld.player._
import scala.com.thomas.customworld.player.rank.Rank
import org.bukkit
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit._
import org.bukkit.entity.{EntityType, Player}
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import org.bukkit.event.vehicle._

import scala.collection.JavaConverters._
import scala.com.thomas.customworld.util._
import org.bukkit.event.{Cancellable, Event, player}

import scala.com.thomas.customworld.minigame.minigameEventModule
import scala.com.thomas.customworld.player.freeop.freeopEventModule
import scala.com.thomas.customworld.player.mute.muteEventModule
import scala.com.thomas.customworld.player.nick.nickEventModule

object CustomCore {
  val plugin: CustomCoreJava = CustomCoreJava.get()
  def cfg: FileConfiguration = plugin.getConfig
  def server: Server = plugin.getServer
  def dbcons:DBConstructor = () => db.MakeDB(cfg)
  var eventModules = Array(discord.DiscordBot, playerEventModule, freeopEventModule, minigameEventModule, ipEventModule, muteEventModule, nickEventModule)

  def onEnable(): Unit = {
    cfg.addDefaults(configuration.ConfigDefaults)
    cfg.options.copyDefaults(true)
    plugin.saveConfig()

    val opendb = dbcons()
    val statement = opendb.createStatement()
    db.InitializeDB foreach statement.addBatch
    statement.executeBatch()
    opendb.close()

    messaging.LoadConfig(cfg)
    minigame.InitializeMinigames(plugin)
    commands.RegisterCommands(plugin, dbcons)

    eventModules foreach (_.enable(plugin))

    plugin.getServer.getOnlinePlayers forEach (player => eventModules foreach (_.join(player)))
    plugin.getLogger.info("Successfully enabled and initialized database!")
  }

  def onDisable(): Unit = {
    plugin.getLogger.info("Disabling...")
    eventModules foreach (_.disable(plugin))
  }

  def PreventInteraction (player:Player): Boolean = {
    !player.hasPermission("blocks")
  }

  def GetTag (tagplayer: Player): Rank = player.getPlayer(tagplayer).rank

  def ev(event:Event) : Unit = {
    def mod (x: EventModule => Unit): Unit = eventModules foreach x
    mod (_.ev (event))
    event match {
      case event: PlayerJoinEvent =>
        mod (_.join(event.getPlayer))
        event.setJoinMessage(PlayerJoinMessage(join=true, GetTag(event.getPlayer), event.getPlayer.getName).formattedText)

      case event: PlayerQuitEvent =>
        event.setQuitMessage(PlayerJoinMessage(join=false, GetTag(event.getPlayer), event.getPlayer.getName).formattedText)
        mod (_.leave(event.getPlayer))

      case event: PlayerEvent with Cancellable =>
        mod (_.playerEv(event, event.getPlayer))
        event match {
          case event: PlayerTeleportEvent =>
            player.getPlayer(event.getPlayer).beforeTp = Some(event.getFrom)

          case event: PlayerCommandPreprocessEvent =>
            messaging.CommandMessage(event.getPlayer.getName, event.getMessage).broadCast(x => x.hasPermission("cmdspy") && x != event.getPlayer)(plugin.getServer)

          case event: PlayerInteractEntityEvent =>
            if (configuration.BlockedEntities contains event.getRightClicked.getType) {
              event.setCancelled(PreventInteraction(event.getPlayer))
            }

          case event: PlayerInteractEvent =>
            if ((event.hasBlock && (configuration.BlockedBlocks contains event.getClickedBlock.getType))
              || (event.getMaterial != null && (configuration.BlockedBlocks contains event.getMaterial))) {
              event.setCancelled(PreventInteraction(event.getPlayer))
            }

          case event: AsyncPlayerChatEvent =>
            if (event.getPlayer.hasPermission("talk")) {
              event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage))
              event.setFormat(PlayerMessage(GetTag(event.getPlayer), event.getPlayer).formattedText)
            } else {
              ErrorMsg("muted") sendClient event.getPlayer
              event.setCancelled(true)
            }

          case _ => ()
        }

      case event: VehicleEvent =>
        event.getVehicle.getPassengers.asScala foreach {
          case x:Player => mod (_.playerEv(event, x)); case _ => ()
          case _ => ()
        }

      case event: BlockBreakEvent => mod (_.playerEv(event, event.getPlayer))
      case event: BlockPlaceEvent => mod (_.playerEv(event, event.getPlayer))

      case event: EntityEvent with Cancellable if event.getEntityType == EntityType.PLAYER =>
        val player = event.getEntity.asInstanceOf[Player]
        mod (_.playerEv(event, player))

        event match {
          case event: ExplosionPrimeEvent =>
            if (!cfg.getBoolean("freeop.explosions"))
              event.setCancelled(true)

          case event: EntityDamageByEntityEvent =>
            event.getDamager match {
              case player: Player => mod(_.playerEv(event, player))
              case _ =>
            }

            (event.getDamager, event.getEntity) match {
              case (x:Player, y:Player) if x.getGameMode == GameMode.CREATIVE =>
                if (!x.hasPermission ("creativepvp")) {
                  x.sendMessage("Creative PvP is not allowed!")
                  event.setCancelled(true)
                }
              case _ => ()
            }
          case _ => ()
        }

      case event: PlayerLoginEvent =>
        val db = new PlayerDB()

        db.upsertPlayer(event.getPlayer.getUniqueId, event.getPlayer.getName)

        if ((event.getResult == PlayerLoginEvent.Result.KICK_BANNED)
        && (db.getPlayer(event.getPlayer.getUniqueId).rank HasPermission "noban")) {
          event.allow()
        }

        db close()

      case _ => ()
    }
  }
}