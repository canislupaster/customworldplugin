package scala.com.thomas.customworld.player.freeop

import com.boydti.fawe.`object`.FawePlayer
import com.boydti.fawe.regions.FaweMaskManager.MaskType
import com.boydti.fawe.regions.general.plot.FaweChunkManager
import com.boydti.fawe.regions.general.{CuboidRegionFilter, RegionFilter}
import com.boydti.fawe.regions.{FaweMask, FaweMaskManager, SimpleRegion}
import com.boydti.fawe.util.WEManager
import com.boydti.fawe.wrappers
import com.sk89q.worldedit.Vector2D
import com.sk89q.worldedit.regions.{CuboidRegion, Region}

import scala.com.thomas.customworld.util.{Box, toUUID}
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import com.sk89q.worldedit.Vector
import org.bukkit.plugin.Plugin

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.com.thomas.customworld.CustomCore
import scala.com.thomas.customworld.player.freeop.protectedRegions

class ProtectionFeature[T](plugin: Plugin) extends FaweMaskManager[T](plugin.getName) {
  def isAllowed(player: FawePlayer[T]): Boolean = player.hasPermission("spawnbuild")

  override def getMask(fp: FawePlayer[T], `type`: MaskType): FaweMask = {
    val player = fp.parent.asInstanceOf[Player]

    val world = fp.getWorldForEditing

    new FaweMask(new SimpleRegion(world, world.getMinimumPoint, world.getMaximumPoint) {
      val allowedSel: Boolean = Option (fp.getSelection) match {
        case Some (sel) =>
          val selBox = new Box(CustomCore.server.getWorld(world.getName), sel)
          !(
            protectedRegions exists ( x => (selBox intersectXZ x.region)
            && (!(x.owner contains toUUID(player.getUniqueId))) )
            )

        case None => true
      }

      var firstblock = true //i dunno what im doing rn fawe is really weird or seomthing api hareedddd

      def containsXZ(x: Int, z:Int): Boolean = {
        if (firstblock) {
          firstblock = false
          allowedSel
        } else (!isProtectedPlayer(x, z, player)) || isAllowed (fp)
      }

      override def contains(x: Int, z: Int): Boolean = containsXZ(x,z)
      override def contains(x: Int, y: Int, z: Int): Boolean = containsXZ(x,z)
    }, plugin.getName)
  }

  override def getFilter(world: String): RegionFilter = {
    new CuboidRegionFilter () {
      override def calculateRegions(): Unit = {
        protectedRegions foreach (x => this.add(x.region.min.toVector2D, x.region.max.toVector2D))
      }
    }
  }
}