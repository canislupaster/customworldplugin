package com.thomas.customworld

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.{Color, Material, OfflinePlayer}
import org.bukkit.configuration.{Configuration, ConfigurationOptions, ConfigurationSection}
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

import scala.collection.JavaConverters

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
      "lang.rankis" -> "Rank:",

      "explosions" -> false,

      "db.hostname" -> "localhost",
      "db.port" -> 5432,
      "db.database" -> "customworld",
      "db.username" -> "postgres",
      "db.password" -> "postgres"
    ) mapValues (_.asInstanceOf[AnyRef])
    JavaConverters.mapAsJavaMap (map)
  }

  val BlockedBlocks = List(Material.TNT, Material.COMMAND, Material.COMMAND_CHAIN, Material.COMMAND_MINECART, Material.COMMAND_REPEATING, Material.COMMAND_CHAIN, Material.STRUCTURE_BLOCK)
  val BlockedEntities = List(EntityType.MINECART_COMMAND, EntityType.MINECART_TNT)
}
