package scala.com.thomas.customworld.player

import net.md_5.bungee.api.chat.{BaseComponent, TextComponent}
import org.bukkit.configuration.file.FileConfiguration

import scala.com.thomas.customworld.CustomCore
import scala.com.thomas.customworld.configuration._
import scala.collection.JavaConverters._

package object rank {
  import org.bukkit.ChatColor

  trait Rank {
    def addPermissions:Set[String] = Set()
    def subPermissions:Set[String] = Set()
    def Color = ChatColor.RED
    def HasPermission (perm:String): Boolean = {
      addPermissions contains perm
    }

    def copy (rank:Rank): Rank = rank

    def Tag:Array[BaseComponent] = {
      TextComponent.fromLegacyText(s"$Color${this.toString}")
    }
  }

  def rankCfg (x:String, cfg:FileConfiguration): Set[String] = {
    cfg.getStringList(s"permission.$x").asScala.toSet
  }

  case object Regular extends Rank {
    override def addPermissions: Set[String] = rankCfg("regular", cfg)
    override def Color = ChatColor.GRAY
  }

  case object Helper extends Rank {
    override def addPermissions: Set[String] = Regular.addPermissions ++ rankCfg("helper", cfg)
    override def Color = ChatColor.BLUE
  }

  case object Builder extends Rank {
    override def addPermissions: Set[String] = Helper.addPermissions ++ rankCfg("builder", cfg)
    override def Color = ChatColor.GREEN
  }

  case object Mod extends Rank {
    override def addPermissions: Set[String] = Builder.addPermissions ++ rankCfg("mod", cfg)
    override def Color = ChatColor.AQUA
  }

  case object Staff extends Rank {
    override def addPermissions: Set[String] = Mod.addPermissions ++ rankCfg("staff", cfg)
    override def Color = ChatColor.GOLD
  }

  case object StaffPlus extends Rank {
    override def addPermissions: Set[String] = Staff.addPermissions ++ rankCfg("staff+", cfg)
    override def Color = ChatColor.GOLD

    override def toString: String = "Staff+"
  }

  case class UnMuted(rank:Rank) extends Rank

  case class Muted(rank:Rank) extends Rank {
    override def addPermissions: Set[String] = rank.addPermissions
    override def subPermissions: Set[String] = rankCfg("muted", cfg) ++ rank.subPermissions
    override def Color = ChatColor.DARK_GRAY

    override def copy(rank: Rank): Rank = {
      rank match {
        case UnMuted (r) => r
        case Muted(r) => Muted(r)
        case r => Muted(r)
      } //toggle
    }

    override def toString: String = s"Muted ${rank.toString}"
  }

  val ranks = List (Regular, Helper, Builder, Mod, Staff, StaffPlus)
}
