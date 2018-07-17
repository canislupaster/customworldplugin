package com.thomas.customworld.minigame

import com.thomas.customworld.util.Box
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class Minigame (plugin: Plugin, region:Box, spawnLoc: Location, signLoc: Location) extends BukkitRunnable {
  val minPlayers = 2
  var state:GameState = WaitingForPlayers ()

  runTaskTimer(plugin, 50, 1)

  def updateState () :GameState = {
    state.players foreach (_.sendMessage("waiting 4 pleyers"))
    WaitingForPlayers ()
  }

  override def run(): Unit = {
    state = updateState()
  }

  def leave (player: Player): Unit = {
    player.teleport(signLoc)
  }

  def stop() :Unit = {
    state.players foreach leave
    state = WaitingForPlayers()
  }

  def start(): Unit = {

  }

  def join (player: Player): Unit = {
    player.teleport(spawnLoc)
    player +: (state players)

    if (state.players.length > minPlayers) {
      start()
    }
  }

  def tryJoin (player: Player, loc:Location): Unit = {
    if (loc == signLoc) {
      join(player)
    }
  }

  def tryLeave (player:Player): Unit = {
    if (state.players contains player) {
      leave(player)
    }
  }
}