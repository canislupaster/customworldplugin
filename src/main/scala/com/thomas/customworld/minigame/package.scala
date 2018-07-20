package com.thomas.customworld

import java.io.File
import java.util.UUID

import com.boydti.fawe.`object`.schematic.Schematic
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.thomas.customworld.util.Box
import org.bukkit.{ChatColor, Location}
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.{PlayerInteractEvent, PlayerMoveEvent}
import org.bukkit.plugin.Plugin
import com.thomas.customworld.util.WEVec
import org.bukkit.util.Vector

import scala.collection.mutable.ArrayBuffer

package object minigame {
  trait GameState
  case class WaitingForPlayers () extends GameState {override def toString = s"${ChatColor.GRAY}Waiting for players..."}
  case class Countdown (timeLeft:Int) extends GameState {override def toString = s"${ChatColor.YELLOW}$timeLeft until start!"}
  case class Playing (timeLeft:Int) extends GameState {override def toString = s"${ChatColor.GREEN}$timeLeft until game end!"}

  //TODO: MINIGAME WORLD IN CONFIG
  var Spleef:Minigame = _
  var CageSchematic:Schematic = _

  def InitializeMinigames (plugin: Plugin): Unit = {
    val cfg = plugin.getConfig
    val world = plugin.getServer.getWorld(cfg.getString("minigame.world"))

    CageSchematic = ClipboardFormat.SCHEMATIC.load(new File(cfg.getString("minigame.cage")))

    Spleef = new SpleefMinigame(plugin, new Box(world, cfg.getVector("minigame.spleef.minRegion"), cfg.getVector("minigame.spleef.maxRegion")),
      cfg.getVector("minigame.spleef.spawnPos").toLocation(world),
      cfg.getVector("minigame.spleef.signPos").toLocation(world),
      ClipboardFormat.SCHEMATIC.load(new File(cfg.getString("minigame.spleef.template"))))
  }

  def leave (player: Player): Unit = {
    Spleef.tryLeave(player)
  }

  def ev (event: Event): Unit = {
    Spleef.tryEv(event)
  }

  def signinteract (plugin: Plugin, player:Player, sign: Sign): Unit = {
    Spleef.tryJoin(player, sign.getLocation())
  }
}