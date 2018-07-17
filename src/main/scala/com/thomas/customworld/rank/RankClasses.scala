package com.thomas.customworld.rank

import org.bukkit.ChatColor

trait Rank {
  def Permissions:Set[String] = Set()
  def Color = ChatColor.RED
  def HasPermission (perm:String): Boolean = {
    Permissions contains perm
  }
  def Tag:String = {
    s"$Color${this.toString}"
  }
}

case object Regular extends Rank {
  override def Color = ChatColor.GRAY
}

case object Helper extends Rank {
  override def Permissions: Set[String] = Regular.Permissions + ("minecraft.command.gamemode")
  override def Color = ChatColor.BLUE
}

case object Builder extends Rank {
  override def Permissions: Set[String] = Helper.Permissions + "spawnbuild"
  override def Color = ChatColor.GREEN
}

case object Mod extends Rank {
  override def Permissions: Set[String] = Builder.Permissions + ("setrank", "minecraft.command.kick", "blocks", "noban", "minecraft.command.ban", "minecraft.command.ban-ip")
  override def Color = ChatColor.AQUA
}

case object Staff extends Rank {
  override def Permissions: Set[String] = Mod.Permissions + ("creativepvp")
  override def Color = ChatColor.GOLD
}

case object StaffPlus extends Rank {
  override def Permissions: Set[String] = Staff.Permissions + ("hell")
  override def Color = ChatColor.GOLD

  override def toString: String = "Staff+"
}