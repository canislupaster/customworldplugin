package com.thomas.customworld

import java.util.regex.Pattern

import com.boydti.fawe.`object`.schematic.Schematic
import com.boydti.fawe.util.EditSessionBuilder
import com.sk89q.worldedit
import com.sk89q.worldedit.blocks.BaseBlock
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import org.bukkit.{Location, World}

package object util {
  def QuoteSurround (str:AnyRef) :String = {
    val replaced = str.toString replaceAll("""\\""", """\""") replaceAll("%", """\%""") replaceAll("_", """\_""") replaceAll("""\[""", """\[""")
    '"' + replaced + '"'
  }

  def WEVec(x: Vector) = new worldedit.Vector(x.getBlockX, x.getBlockY, x.getBlockZ)

  case class Box (bworld:World, min:worldedit.BlockVector, max:worldedit.BlockVector) extends CuboidRegion(min, max) {
    def hasLoc (x:Location): Boolean = {
      this.contains (WEVec (x.toVector))
    }

    def this(world:World, min:Vector, max:Vector) {
      this(world, WEVec(min).toBlockVector, WEVec(max).toBlockVector)
    }

    def copy ():Schematic = {
      val session = new EditSessionBuilder(bworld.getName).build()
      new Schematic(session.lazyCopy(this))
    }

    def copyFromWorld (world2:World): Unit = {
      val copyWorld = new EditSessionBuilder(world2.getName).autoQueue(false).build()
      val pasteWorld = new EditSessionBuilder(bworld.getName).build()

      val copied = copyWorld lazyCopy this

      val schem = new Schematic(copied)
      schem.paste(pasteWorld, this.getMinimumPoint, true)
      pasteWorld.flushQueue()
    }

    def paste (schem: Schematic, air :Boolean): Unit = {
      schem.getClipboard.setOrigin(schem.getClipboard.getRegion.getMinimumPoint)
      val pasteWorld = new EditSessionBuilder(bworld.getName).build()
      schem.paste(pasteWorld, this.getMinimumPoint, air)
      pasteWorld.flushQueue()
    }

    def fillBox (id: Int): Unit = {
      val session = new EditSessionBuilder(bworld.getName).build()
      session.setBlocks (this, new BaseBlock(0))
      session.flushQueue()
    }
  }
}