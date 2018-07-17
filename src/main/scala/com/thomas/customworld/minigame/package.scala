package com.thomas.customworld

import com.thomas.customworld.util.Box
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

package object minigame {
  trait GameState {var players:Array[Player] = Array()}
  case class WaitingForPlayers () extends GameState
  case class Countdown (timeLeft:Int) extends GameState
  case class Playing (timeLeft:Int) extends GameState

  //TODO: MINIGAME WORLD IN CONFIG
  var Spleef:Minigame = null

  def InitializeMinigames (plugin: Plugin): Unit = {
    Spleef = new Minigame(plugin, Box(182, 3, -58, -157, 15, -90),
              new Location (plugin.getServer.getWorld("world"), -167, 6, -75),
              new Location (plugin.getServer.getWorld("world"),-168,4,-55))
  }

  def leave (player: Player): Unit = {
    Spleef.tryLeave(player)
  }

  def interact (plugin: Plugin, player:Player, sign: Sign): Unit = {
    Spleef.tryJoin(player, sign.getLocation())
    //this might stop everything tho
  }
}