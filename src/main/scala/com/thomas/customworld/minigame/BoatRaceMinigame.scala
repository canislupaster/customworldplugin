package scala.com.thomas.customworld.minigame

import java.io.File

import com.boydti.fawe.`object`.schematic.Schematic
import com.boydti.fawe.util.EditSessionBuilder
import com.sk89q.worldedit.blocks.{BaseBlock, BlockType}
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.function.pattern.{Pattern, RandomPattern}
import scala.com.thomas.customworld.utility.Box
import org.bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity
import org.bukkit.entity.{Boat, EntityType, Player}
import org.bukkit.event.Event
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.vehicle.{VehicleExitEvent, VehicleMoveEvent}
import org.bukkit.{GameMode, Location, Material, World}
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin

case class BoatRacePlayerData(lap:Int, override val inventory:Inventory) extends MinigamePlayerData
//class BoatRaceMinigame (plugin: Plugin, region:Box, spawnLoc: Location, spawnLocs:List[Location], signLoc: Location) extends Minigame[BoatRacePlayer] (plugin, region, spawnLoc, signLoc) {
//  override val name: String = "BOATRACE"
//
//  override def defaultPlayer(inv: Array[ItemStack]) = BoatRacePlayer (0, inv)
//
//  override def end (): Unit = {
//    super.end()
//    //show winner here
//  }
//
//  override def initializeMap(): Unit = {
//    super.initializeMap()
//  }
//
//  override def start(): Unit = {
//    super.start()
//    getPlayers zip spawnLocs foreach { case (x, y) =>
//      val boat = region.bworld.spawnEntity(y, EntityType.BOAT)
//      boat.addPassenger(x)
//    }
//  }
//
//  override def tryOtherEv: Event => Unit = {
//    case e:VehicleExitEvent if playerInGame(e.getExited) =>
//      e.setCancelled(true)
//    case e:VehicleMoveEvent if e.getVehicle.getPassengers.size() > 0 && playerInGame (e.getVehicle.getPassengers.get(0)) =>
//      val player = e.getVehicle.getPassengers.get(0).asInstanceOf[Player]
//      if (e.getTo.subtract(0,1,0).getBlock.getType == Material.GOLD_BLOCK) {
//        mapPlayer (player.getUniqueId, x => x.copy(lap = x.lap+1))
//      }
//  }
//}