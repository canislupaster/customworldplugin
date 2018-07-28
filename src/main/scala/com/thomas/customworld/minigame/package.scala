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
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

import scala.collection.mutable.ArrayBuffer

package object minigame {
  type Inventory = Array[ItemStack]
  class MinigamePlayer () {
    val inventory: Inventory = Array()
    var playing = false
  }
  case class SimpleMinigamePlayer(override val inventory:Inventory) extends MinigamePlayer

  trait GameState
  case class WaitingForPlayers () extends GameState {override def toString = s"${ChatColor.GRAY}Waiting for players..."}
  case class Countdown (timeLeft:Int) extends GameState {override def toString = s"${ChatColor.YELLOW}$timeLeft until start!"}
  case class Playing (timeLeft:Int) extends GameState {override def toString = s"${ChatColor.GREEN}$timeLeft until game end!"}

  var Minigames:ArrayBuffer[Minigame[SimpleMinigamePlayer]] = ArrayBuffer()
  var CageSchematic:Schematic = _

  def InitializeMinigames (plugin: Plugin): Unit = {
    Minigames = ArrayBuffer()

    val cfg = plugin.getConfig
    val world = plugin.getServer.getWorld(cfg.getString("minigame.world"))

    CageSchematic = ClipboardFormat.SCHEMATIC.load(new File(cfg.getString("minigame.cage")))

    Minigames += new SpleefMinigame(plugin, new Box(world, cfg.getVector("minigame.spleef.minRegion"), cfg.getVector("minigame.spleef.maxRegion")),
      cfg.getVector("minigame.spleef.spawnPos").toLocation(world),
      cfg.getVector("minigame.spleef.signPos").toLocation(world),
      ClipboardFormat.SCHEMATIC.load(new File(cfg.getString("minigame.spleef.template"))))

    Minigames foreach (_.runTaskTimer(plugin, 20, 20))
  }

  def leave (player: Player): Unit = {
    Minigames foreach (_.tryLeave(player))
  }

  def ev (event: Event): Unit = {
    Minigames foreach (_.tryEv(event))
  }

  def stop (): Unit ={
    Minigames foreach (_.cancel())
  }

  def signinteract (plugin: Plugin, player:Player, sign: Sign): Unit = {
    Minigames foreach (_.tryJoin(player, sign.getLocation()))
  }
}