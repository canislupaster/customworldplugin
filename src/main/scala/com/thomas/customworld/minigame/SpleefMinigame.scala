package com.thomas.customworld.minigame

import java.io.File

import com.boydti.fawe.`object`.schematic.Schematic
import com.boydti.fawe.util.EditSessionBuilder
import com.sk89q.worldedit.blocks.BaseBlock
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.function.pattern.{Pattern, RandomPattern}
import com.thomas.customworld.messaging.InfoMsgRev
import com.thomas.customworld.util.Box
import org.bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.{GameMode, Location, Material, World}
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.particle

class SpleefMinigame (plugin: Plugin, region:Box, spawnLoc: Location, signLoc: Location, template: Schematic) extends Minigame (plugin, region, spawnLoc, signLoc) {
  override val name: String = "SPLEEF"

  override def end (): Unit = {
    super.end()
    playerDo (x => y => if (y.playing) {InfoMsgRev ("spleefwon", x.getDisplayName) broadCast (_ => true, plugin.getServer)})
  }

  override def initializeMap(): Unit = {
    super.initializeMap()
    region paste (template, false)
  }

  override def start(): Unit = {
    super.start()
    playerDo ((spleefer:Player) => _ => {
      val woolcrusher = new ItemStack(Material.SHEARS)
      woolcrusher.addEnchantment(Enchantment.DIG_SPEED, 5)
      woolcrusher.setUnbreakable(true)
      woolcrusher.setDisplayName("Wool Crusher")
      spleefer.getInventory.setItemInHand(woolcrusher)
      spleefer.setGameMode(GameMode.SURVIVAL)
    })
  }

  override def playerMove(event: PlayerMoveEvent): Unit = {
    if ((event.getTo.getBlockY < region.getMinimumY) && players(event.getPlayer.getUniqueId).playing) {
      spectate (event.getPlayer)
    } else {
      super.playerMove(event)
    }
  }

  override def blockBreak(block: BlockBreakEvent): Unit = {
    val blockloc = new block.getLocation()
    if (block.getBlock.getType == Material.WOOL)
      block.setDropItems(false) 
    if (block.getBlock.getType == Material.TNT)
      val boom = new world.spawnEntity(blockloc, EntityType.PRIMED_TNT)
      boom.setFuseTicks("5")
    else 
      block.setCancelled(true)
  }
}
