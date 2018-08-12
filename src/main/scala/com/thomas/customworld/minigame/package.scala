package scala.com.thomas.customworld

import java.io.File
import java.util.UUID

import com.boydti.fawe.`object`.schematic.Schematic
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat

import scala.com.thomas.customworld.util.Box
import org.bukkit.{ChatColor, Location, Material}
import org.bukkit.block.{BlockState, Sign}
import org.bukkit.entity.Player
import org.bukkit.event.{Cancellable, Event}
import org.bukkit.event.player.{PlayerInteractEvent, PlayerMoveEvent}
import org.bukkit.plugin.Plugin

import scala.com.thomas.customworld.util.WEVec
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

import scala.collection.mutable.ArrayBuffer

package object minigame {
  type Inventory = Array[ItemStack]
  class MinigamePlayerData() {
    val inventory: Inventory = Array()
    var playing = false
  }
  case class SimpleMinigamePlayerData(override val inventory:Inventory) extends MinigamePlayerData

  trait GameState
  case class WaitingForPlayers () extends GameState {override def toString = s"${ChatColor.GRAY}Waiting for players..."}
  case class Countdown (timeLeft:Int) extends GameState {override def toString = s"${ChatColor.YELLOW}$timeLeft until start!"}
  case class Playing (timeLeft:Int) extends GameState {override def toString = s"${ChatColor.GREEN}$timeLeft until game end!"}

  var Minigames:ArrayBuffer[Minigame[SimpleMinigamePlayerData]] = ArrayBuffer()
  var CageSchematic:Schematic = _

  def InitializeMinigames (plugin: Plugin): Unit = {
    Minigames = ArrayBuffer()

    val cfg = plugin.getConfig
    val world = plugin.getServer.getWorld(cfg.getString("minigame.world"))

    CageSchematic = ClipboardFormat.SCHEMATIC.load(new File(cfg.getString("minigame.cage")))

    Minigames += new SpleefMinigame(plugin,
      new Box(world, cfg.getVector("minigame.spleef.minRegion"), cfg.getVector("minigame.spleef.maxRegion")),
      cfg.getVector("minigame.spleef.spawnPos").toLocation(world),
      cfg.getVector("minigame.spleef.signPos").toLocation(world),
      ClipboardFormat.SCHEMATIC.load(new File(cfg.getString("minigame.spleef.template"))))

    Minigames foreach (_.runTaskTimer(plugin, 20, 20))
  }

  object minigameEventModule extends EventModule {
    def signinteract (player:Player, sign: Sign): Unit = {
      Minigames foreach (_.tryJoin(player, sign.getLocation()))
    }

    override def playerEv(event: Event, player: Player): Unit = {
      event match {
        case event: PlayerInteractEvent =>
          if (event.hasBlock && event.getClickedBlock.getType == Material.SIGN_POST) {
              event.getClickedBlock.getState match {
              case x:Sign => signinteract(player, x)
              case _ => ()
            }
          }; case _ => ()
      }
    }

    override def disable(plugin: Plugin): Unit = {
      Minigames foreach (_.cancel())
    }
  }
}