package com.thomas.customworld.freeop

import com.boydti.fawe.`object`.FawePlayer
import com.boydti.fawe.regions.FaweMaskManager.MaskType
import com.boydti.fawe.regions.general.{CuboidRegionFilter, RegionFilter}
import com.boydti.fawe.regions.{FaweMask, FaweMaskManager, SimpleRegion}
import com.boydti.fawe.wrappers
import com.sk89q.worldedit.Vector2D
import com.sk89q.worldedit.regions.CuboidRegion
import com.thomas.customworld.util.Box
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import com.sk89q.worldedit.Vector
import org.bukkit.plugin.Plugin

class ProtectionFeature[T](plugin: Plugin) extends FaweMaskManager[T](plugin.getName) {
  def isAllowed(player: FawePlayer[T]): Boolean = player.hasPermission("spawnbuild")

  override def getMask(fp: FawePlayer[T], `type`: MaskType): FaweMask = {
    val player = fp.parent.asInstanceOf[Player]

    val name = player.getName

    new FaweMask(new SimpleRegion(fp.getWorld, new Vector(-2147483648, -2147483648, -2147483648), new Vector(2147483647, 2147483647, 2147483647)) {
      override def contains(x: Int, y: Int, z: Int): Boolean = {
        contains(x,z)
      }

      override def contains(x: Int, z: Int): Boolean = {
        (!(protectedRegions exists (_.hasXZ(x, z)))) || isAllowed(fp)
      }
    }, plugin.getName)
  }

  override def getFilter(world: String): RegionFilter = {
    new CuboidRegionFilter () {
      override def calculateRegions(): Unit = {
        protectedRegions foreach (x => this.add(x.min.toVector2D, x.max.toVector2D))
      }
    }
  }
}