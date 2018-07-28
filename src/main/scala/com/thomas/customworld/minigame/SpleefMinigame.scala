package com.thomas.customworld.minigame

import java.io.File

import com.boydti.fawe.`object`.schematic.Schematic
import com.boydti.fawe.util.EditSessionBuilder
import com.sk89q.worldedit.blocks.BaseBlock
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.function.pattern.{Pattern, RandomPattern}
import com.thomas.customworld.messaging.{ConfigMsg, InfoMsg, RuntimeMsg}
import com.thomas.customworld.util.Box
import org.bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.{GameMode, Location, Material, World}
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class SpleefMinigame (plugin: Plugin, region:Box, spawnLoc: Location, signLoc: Location, template: Schematic) extends Minigame[SimpleMinigamePlayer] (plugin, region, spawnLoc, signLoc) {
  override val name: String = "SPLEEF"
  override def defaultPlayer(inv:Array[ItemStack]) = SimpleMinigamePlayer(inv)
  override val gameTime = 300

  override def end (): Unit = {
    super.end()
    doMinigamePlayers (x => y => if (y.playing) {InfoMsg (RuntimeMsg(x.getDisplayName), ConfigMsg("spleefwon")) globalBroadcast plugin.getServer})
  }

  override def initializeMap(): Unit = {
    super.initializeMap()
    region paste (template, false)
  }

  override def start(): Unit = {
    super.start()
    doPlayers ((x:Player) => {
      val item = new ItemStack(Material.SHEARS)
      item.addEnchantment(Enchantment.DIG_SPEED, 5)
      x.getInventory.setItemInHand(item)
      x.setGameMode(GameMode.SURVIVAL)
    })
  }

  override def playerMove(event: PlayerMoveEvent): Unit = {
    if ((event.getTo.getBlockY < region.getMinimumY) && players(event.getPlayer.getUniqueId).playing) {
      spectate (event.getPlayer)
    } else {
      super.playerMove(event)
    }
  }

  override def blockBreak(event: BlockBreakEvent): Unit = {
    if (event.getBlock.getType == Material.WOOL)
      event.setDropItems(false) else event.setCancelled(true)
  }
}