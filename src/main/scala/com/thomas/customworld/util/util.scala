package com.thomas.customworld

import java.sql.Timestamp
import java.util.{Calendar, UUID}
import java.util.regex.Pattern

import com.boydti.fawe.`object`.schematic.Schematic
import com.boydti.fawe.util.EditSessionBuilder
import com.sk89q.worldedit
import com.sk89q.worldedit.blocks.BaseBlock
import com.sk89q.worldedit.regions.{CuboidRegion, Region}
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import org.bukkit.{Location, World}

import scala.collection.LinearSeq
import scala.language.implicitConversions
import scala.util.matching.Regex

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

    def this(world:World, region:Region) {
      this(world, region.getMinimumPoint.toBlockVector, region.getMaximumPoint.toBlockVector)
    }

    def copyBox():Schematic = {
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

    def expand(x:Int): Box = {
      this.copy(min=min.subtract(x,x,x).toBlockVector, max=max.add(x,x,x).toBlockVector)
    }

    def hasXZ (x:Int,z:Int): Boolean = this.contains(new worldedit.Vector(x,min.getY+1,z))
  }

  implicit def fromUUID (x:UUID): String = x.toString
  implicit def toUUID (x:String): UUID = UUID.fromString(x)
  implicit def fromBool (x:Boolean): Int = if (x) 1 else 0
  implicit def toBool (x:Int): Boolean = if (x>0) true else false

  def spaceJoin(x:List[String]): String = {
    if (x.nonEmpty) x reduce[String] { case (y, z) => s"$y $z" }
    else ""
  }

  object Int {
    def unapply (x:String): Option[Int] = {
      try Some(Integer.parseInt(x))
      catch {
        case _:java.lang.NumberFormatException => None
      }
    }
  }

  object Dbl {
    def unapply (x:String): Option[Double] = {
      try Some(x.toDouble)
      catch {
        case _:java.lang.NumberFormatException => None
      }
    }
  }

  object TimeParser {
    def unapply(arg: String): Option[Timestamp] = {
      val mins = "(\\d+)m".r
      val hours = "(\\d+)h".r
      val days = "(\\d+)d".r

      def parser (regex: Regex):Int = {
        arg match {
          case regex(Int(x)) => x
          case _ => 0
        }
      }

      List(parser(mins), parser(hours)*60, parser(days)*1440) match {
        case List(0,0,0) => None
        case x =>
          val date = Calendar.getInstance()
          date.add(Calendar.MINUTE, x sum)
          Some(new Timestamp(date.getTime.getTime))
      }
    }
  }
}