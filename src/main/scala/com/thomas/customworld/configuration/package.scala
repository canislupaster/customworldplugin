package com.thomas.customworld

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.{Color, Material, OfflinePlayer}
import org.bukkit.configuration.{Configuration, ConfigurationOptions, ConfigurationSection}
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

import scala.collection.JavaConverters
import scala.collection.JavaConverters._

package object configuration {
  val ConfigDefaults:java.util.Map[String, AnyRef] = {
    val map = Map (
      "lang.success" -> "Success!",
      "lang.noconsole" -> "You cannot do this from console!",
      "lang.invalidarg" -> "Invalid arguments!",
      "lang.noplayer" -> "Player not found!",
      "lang.nohome" -> "Home not found!",
      "lang.nohomes" -> "No homes found!",
      "lang.manyhomes" -> "You have too many homes!",
      "lang.prefix" -> "§aCustomWorld §l>>",
      "lang.homes" -> "Available homes:",
      "lang.tpto" -> "Teleporting to",
      "lang.spleefwon" -> "won a round of spleef!",
      "lang.rankis" -> "Rank:",
      "lang.opped" -> "You have been opped!",
      "lang.noloc" -> "Location not found!",
      "lang.leavegame" -> "Leave the game first with /spawn",

      "minigame.world" -> "world",
      "minigame.spleef.template" -> "./spleef.schematic",
      "minigame.spleef.minRegion" -> new Vector(-155, -2, -92),
      "minigame.spleef.maxRegion" -> new Vector(-184, 15, -56),
      "minigame.spleef.spawnPos" -> new Vector (-170, 10, -73),
      "minigame.spleef.signPos" -> new Vector(-169, 4, -55),
      "minigame.cage" -> "./cage.schematic",

      "db.hostname" -> "localhost",
      "db.port" -> 3306,
      "db.database" -> "customworld",
      "db.username" -> "root",
      "db.password" -> "mysql",

      "permission.regular" -> List().asJava,
      "permission.helper" -> List().asJava,
      "permission.builder" -> List("blocks", "spawnbuild").asJava,
      "permission.mod" -> List("tempban", "kick", "fawe.admin").asJava,
      "permission.staff" -> List("setrank", "minecraft.command.gamemode", "minecraft.command.kick", "blocks", "ban").asJava,
      "permission.staff+" -> List("noban", "hell", "config")
    ) mapValues (_.asInstanceOf[AnyRef])
    JavaConverters.mapAsJavaMap (map)
  }

  val BlockedBlocks = List(Material.TNT, Material.COMMAND, Material.COMMAND_CHAIN, Material.COMMAND_MINECART, Material.COMMAND_REPEATING, Material.COMMAND_CHAIN, Material.STRUCTURE_BLOCK)
  val BlockedEntities = List(EntityType.MINECART_COMMAND, EntityType.MINECART_TNT)
}
