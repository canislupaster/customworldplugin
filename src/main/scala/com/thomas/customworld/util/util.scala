package scala.com.thomas.customworld

import java.sql.Timestamp
import java.util
import java.util.{Calendar, UUID}
import java.util.regex.Pattern

import com.boydti.fawe.FaweAPI
import com.boydti.fawe.`object`.FawePlayer
import com.boydti.fawe.`object`.schematic.Schematic
import com.boydti.fawe.util.EditSessionBuilder
import com.github.takezoe.scala.jdbc._
import com.sk89q.worldedit
import com.sk89q.worldedit.blocks.BaseBlock
import com.sk89q.worldedit.extension.platform.Platform
import com.sk89q.worldedit.internal.ServerInterfaceAdapter
import com.sk89q.worldedit.regions.{CuboidRegion, Region}
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import org.bukkit.{Location, World, WorldCreator}

import scala.collection.LinearSeq
import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.matching.Regex

package object util {
  def QuoteSurround (str:AnyRef) :String = {
    val replaced = str.toString replaceAll("""\\""", """\""") replaceAll("%", """\%""") replaceAll("_", """\_""") replaceAll("""\[""", """\[""")
    '"' + replaced + '"'
  }

  def SomeArr[A:ClassTag](x:A) = Some(Array(x))
  def Now() = new Timestamp(new java.util.Date().getTime)
  def WEVec(x: Vector) = new worldedit.Vector(x.getBlockX, x.getBlockY, x.getBlockZ)

  case class Paginator (page:Int, limit:Int) {
    def from: Int = (page-1)*limit
    def to: Int = page*limit
  }

  case class Box (bworld: World, min:worldedit.BlockVector, max:worldedit.BlockVector) extends CuboidRegion(FaweAPI.getWorld(bworld.getName), min, max) {
    def hasLoc (x:Location): Boolean = {
      this.contains (WEVec (x.toVector))
    }

    def intersectXZ(box2:Box): Boolean = {
      val (x,y,a,b,x1,y1,a1,b1) = (min.getBlockX, min.getBlockZ, max.getBlockX, max.getBlockZ, box2.min.getBlockX, box2.min.getBlockZ, box2.max.getBlockX, box2.max.getBlockZ)
      !(a<x1 || a1<x || b<y1 || b1<y) // credit goes to stackoverflow: https://stackoverflow.com/questions/13390333/two-rectangles-intersection
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

    def expand(x:Vector): Box = {
      this.copy(min=min.subtract(x.getBlockX,x.getBlockY,x.getBlockZ).toBlockVector, max=max.add(x.getBlockX,x.getBlockY,x.getBlockZ).toBlockVector)
    }

    def hasXZ (x:Int,z:Int): Boolean = {
      x >= min.getBlockX && x <= max.getBlockX && z >= min.getBlockZ && z <= max.getBlockZ
    }
  }

  def fromBool (x:Boolean): Int = if (x) 1 else 0
  def toBool (x:Int): Boolean = if (x>0) true else false
  def fromOption: Option[String] => String = {case None => "NULL"; case Some(x) => x}

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

  case class UUID (x:String) {
    def UUID: java.util.UUID = java.util.UUID.fromString(x)
  }

  implicit def toUUID (x:java.util.UUID): UUID = {
    UUID(x.toString)
  }

  def getSelection (player:Player): Option[Region] = {
    Option(FawePlayer.wrap(player).getSelection)
  }
}