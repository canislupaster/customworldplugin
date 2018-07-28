package com.thomas.customworld.player

import com.thomas.customworld.CustomWorldPlugin
import scala.collection.JavaConverters._

package object rank {
  import org.bukkit.ChatColor

  trait Rank {
    def permissions:Set[String] = Set()
    def Color = ChatColor.RED
    def HasPermission (perm:String): Boolean = {
      permissions contains perm
    }
    def Tag:String = {
      s"$Color${this.toString}"
    }
  }

  def rankCfg (x:String): Set[String] = {
    CustomWorldPlugin.plugin.getConfig.getStringList(s"permission.$x").asScala.toSet
  }

  case object Regular extends Rank {
    override def permissions: Set[String] = rankCfg("regular")
    override def Color = ChatColor.GRAY
  }

  case object Helper extends Rank {
    override def permissions: Set[String] = Regular.permissions ++ rankCfg("helper")
    override def Color = ChatColor.BLUE
  }

  case object Builder extends Rank {
    override def permissions: Set[String] = Helper.permissions ++ rankCfg("builder")
    override def Color = ChatColor.GREEN
  }

  case object Mod extends Rank {
    override def permissions: Set[String] = Builder.permissions ++ rankCfg("mod")
    override def Color = ChatColor.AQUA
  }

  case object Staff extends Rank {
    override def permissions: Set[String] = Mod.permissions ++ rankCfg("staff")
    override def Color = ChatColor.GOLD
  }

  case object StaffPlus extends Rank {
    override def permissions: Set[String] = Staff.permissions ++ rankCfg("staff+")
    override def Color = ChatColor.GOLD

    override def toString: String = "Staff+"
  }

  case object Muted extends Rank {
    override def permissions: Set[String] = rankCfg("muted")
    override def Color = ChatColor.DARK_GRAY

    override def toString: String = "Muted"
  }

  val ranks = List (Regular, Helper, Builder, Mod, Staff, StaffPlus)
}
