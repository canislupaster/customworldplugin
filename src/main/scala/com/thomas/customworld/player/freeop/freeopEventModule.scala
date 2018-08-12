package scala.com.thomas.customworld.player.freeop

import com.boydti.fawe.FaweAPI
import org.bukkit.block.Block
import org.bukkit.event.{Cancellable, Event}
import org.bukkit.event.block._
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.plugin.Plugin

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.com.thomas.customworld.EventModule
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.player.freeop
import scala.com.thomas.customworld.util.Box

object freeopEventModule extends EventModule { //not caused by player events
  override def ev(event: Event): Unit = {
    event match {
      case _: BlockPlaceEvent => ()
      case _: BlockBreakEvent => ()
      case _: BlockPhysicsEvent => ()
      case x: BlockFromToEvent if freeop.isProtected(x.getToBlock.getLocation()) && !freeop.isProtected(x.getBlock.getLocation()) => x.setCancelled(true)
      case x: BlockExplodeEvent => x.blockList().removeIf(x => freeop.isProtected(x.getLocation()))
      case x: BlockPistonExtendEvent if x.getBlocks.asScala exists (b => freeop.isProtected(b.getLocation())) =>
        if (!isProtected(x.getBlock.getLocation)) x.setCancelled(true)
      case x: EntityExplodeEvent => x.blockList().removeIf(x => freeop.isProtected(x.getLocation()))
      case x: BlockEvent with Cancellable if freeop.isProtected(x.getBlock.getLocation()) => x.setCancelled(true)
      case _ => ()
    }
  }

  override def enable(plugin: Plugin): Unit = {
    protectedRegions = mutable.Set()

    FaweAPI.addMaskManager(new ProtectionFeature (plugin))

    plugin.getServer.getWorlds forEach(x => registerProtected (ProtectedRegion(new Box(x, x.getSpawnLocation.toVector, x.getSpawnLocation.toVector) expand 30, List())))
    val builds = new BuildDB().autoClose(_.getAllBuilds)
    builds foreach (x => registerProtected(x protectedRegion))

    jumppadSettings.update(plugin.getConfig)
  }
}
