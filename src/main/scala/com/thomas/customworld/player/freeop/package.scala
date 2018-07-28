package com.thomas.customworld

import com.boydti.fawe.FaweAPI
import com.sk89q.worldedit.regions.CuboidRegion
import com.thomas.customworld.messaging.ErrorMsg
import com.thomas.customworld.player.freeop.ProtectionFeature
import com.thomas.customworld.util.Box
import org.bukkit.entity.Player
import org.bukkit.event.{Cancellable, Event}
import org.bukkit.event.block.{BlockBreakEvent, BlockEvent, BlockPlaceEvent}
import com.thomas.customworld.player.isFreeOP
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.{Location, Material}
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.player.{PlayerInteractEntityEvent, PlayerInteractEvent, PlayerMoveEvent}
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector

import scala.collection.JavaConversions._
import scala.collection.mutable

package object freeop {
  var protectedRegions:mutable.Set[Box] = mutable.Set()
  object jumppadSettings {
    var force:Double = 0
    var on = false

    def update(fileConfiguration: FileConfiguration): Unit = {
      force = fileConfiguration.getDouble("freeop.jumppad.force")
      on = fileConfiguration.getBoolean("freeop.jumppad.on")
    }
  }

  def registerProtected(c:Box): Unit = {
    protectedRegions += c.expand(15) //maybe add to config?
  }

  def isProtected (location: Location): Boolean = protectedRegions exists (_.hasXZ(location.getBlockX, location.getBlockZ))
  def protect[T <: Cancellable] (e: T, player: Player): Unit = {
    val loc = e match {case e:BlockEvent => e.getBlock.getLocation(); case e:PlayerInteractEvent => if (e.hasBlock) e.getClickedBlock.getLocation() else e.getPlayer.getLocation; case e:PlayerInteractEntityEvent => e.getRightClicked.getLocation()}
    if (!(!isProtected(loc) || player.hasPermission("spawnbuild"))) {
      ErrorMsg ("tooclose") sendClient player
      e.setCancelled (true)
    }
  }

  def jumppad(event: Event): Unit = {
    if (jumppadSettings.on) {
      val to = event match {
        case e: VehicleMoveEvent => e.getTo
        case e: PlayerMoveEvent => e.getTo
      }
      val entity = event match {
        case e: VehicleMoveEvent => e.getVehicle
        case e: PlayerMoveEvent => e.getPlayer
      }

      if (to.clone().subtract(0, 1, 0).getBlock.getType == Material.EMERALD_BLOCK) {
        val vel = entity.getVelocity.clone()
        entity.setVelocity(new Vector(vel.getX*3, Math.abs(vel.getY)+jumppadSettings.force, vel.getZ*3))
      }
    }
  }

  def ev (event: Event): Unit = {
    event match {
      case e: BlockBreakEvent if isFreeOP (e.getPlayer) => protect(e, e.getPlayer)
      case e: BlockPlaceEvent if isFreeOP (e.getPlayer) => protect(e, e.getPlayer)
      case e: PlayerInteractEvent if isFreeOP(e.getPlayer) => protect(e, e.getPlayer)
      case e: PlayerInteractEntityEvent if isFreeOP(e.getPlayer) => protect(e,e.getPlayer)
      case e: PlayerMoveEvent if isFreeOP(e.getPlayer) => jumppad(e)
      case e: VehicleMoveEvent => e.getVehicle.getPassengers.toList match {case (x:Player)::_ if isFreeOP(x) => jumppad (e); case _ => ()}
      case _ => ()
    }
  }

  def initialize(plugin: Plugin): Unit = {
    FaweAPI.addMaskManager(new ProtectionFeature (plugin))
    plugin.getServer.getWorlds forEach(x => registerProtected (new Box(x, x.getSpawnLocation.toVector, x.getSpawnLocation.toVector) expand 15))

    jumppadSettings.update(plugin.getConfig)
  }
}
