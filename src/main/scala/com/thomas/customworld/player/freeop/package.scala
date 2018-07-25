package com.thomas.customworld

import com.boydti.fawe.FaweAPI
import com.sk89q.worldedit.regions.CuboidRegion
import com.thomas.customworld.messaging.ErrorMsg
import com.thomas.customworld.util.Box
import org.bukkit.entity.Player
import org.bukkit.event.{Cancellable, Event}
import org.bukkit.event.block.{BlockBreakEvent, BlockEvent, BlockPlaceEvent}
import com.thomas.customworld.player.isFreeOP
import org.bukkit.Location
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.player.{PlayerInteractEntityEvent, PlayerInteractEvent}
import org.bukkit.plugin.Plugin

import scala.collection.mutable

package object freeop {
  var protectedRegions:mutable.Set[Box] = mutable.Set()

  def registerProtected(c:Box): Unit = {
    protectedRegions += c.expand(15) //maybe add to config?
  }

  def isProtected (location: Location): Boolean = protectedRegions exists (_.hasXZ(location.getBlockX, location.getBlockZ))
  def protect[T <: Cancellable] (e: T, player: Player): Unit = {
    val loc = e match {case e:BlockEvent => e.getBlock.getLocation(); case e:PlayerInteractEvent => e.getClickedBlock.getLocation(); case e:PlayerInteractEntityEvent => e.getRightClicked.getLocation()}
    if (!(!isProtected(loc) || player.hasPermission("spawnbuild"))) {
      ErrorMsg ("tooclose") sendClient player
      e.setCancelled (true)
    }
  }

  def ev (event: Event): Unit = {
    event match {
      case e: BlockBreakEvent if isFreeOP (e.getPlayer) => protect(e, e.getPlayer)
      case e: BlockPlaceEvent if isFreeOP (e.getPlayer) => protect(e, e.getPlayer)
      case e: PlayerInteractEvent if isFreeOP(e.getPlayer) => protect(e, e.getPlayer)
      case e: PlayerInteractEntityEvent if isFreeOP(e.getPlayer) => protect(e,e.getPlayer)
      case _ => ()
    }
  }

  def initialize(plugin: Plugin): Unit = {
    FaweAPI.addMaskManager(new ProtectionFeature (plugin))
    plugin.getServer.getWorlds forEach(x => registerProtected (new Box(x, x.getSpawnLocation.toVector, x.getSpawnLocation.toVector) expand 15))
  }
}
