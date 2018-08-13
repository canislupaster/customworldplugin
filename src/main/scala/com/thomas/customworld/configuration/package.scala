package scala.com.thomas.customworld

import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.EntityType
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
      "lang.nobuilds" -> "No builds found!",
      "lang.nobuild" -> "No build found!",
      "lang.nothemes" -> "No themes found!",
      "lang.alreadyvoted" -> "You have already voted!",
      "lang.manyhomes" -> "You have too many homes!",
      "lang.prefix" -> "§aCustomWorld §l>>",
      "lang.homes" -> "Available homes:",
      "lang.tpto" -> "Teleporting to",
      "lang.spleefwon" -> "won a round of spleef!",
      "lang.rankis" -> "Rank:",
      "lang.opped" -> "You have been opped!",
      "lang.noloc" -> "Location not found!",
      "lang.leavegame" -> "Leave the game first with /spawn",
      "lang.hasexec" -> "has executed",
      "lang.noreq" -> "No pending request!",
      "lang.tooclose" -> "You are too close to a protected region!",
      "lang.alreadyexists" -> "That already exists!",
      "lang.unverified" -> "You must get your ip verified first! Perhaps ask staff on the discord ( https://discord.gg/sgMCMrY ).",
      "lang.nobans" -> "There aren't any bans on that player!",
      "lang.appeal" -> "Appeal on the discord: https://discord.gg/sgMCMrY",
      "lang.muted" -> "You are muted!",
      "lang.alreadymuted" -> "That player is already (un)muted!",
      "lang.verified" -> "has been verified by",
      "lang.imposter" -> "is an imposter!",
      "lang.norankyourself" -> "You cannot rank yourself!",
      "lang.norankhigher" -> "You cannot rank higher ups!",
      "lang.norankhigherrank" -> "You cannot rank someone higher than you are ranked!",
      "lang.noperms" -> "You don't have permission to do this!",
      "lang.noselection" -> "You do not have a worldedit selection!",
      "lang.sellarge" -> "Your selection is too large!",
      "lang.noname" -> "You must give a name",
      "lang.page" -> "Page",
      "lang.in" -> "You are in",
      "lang.io" -> "An IO exception occurred, check console.",
      "lang.loading" -> "Please wait...",
      "lang.reloadbot" -> "Bot is not initialized, please restart server.",
      "lang.toolong" -> "Please use a shorter name!",
      "lang.nocmd" -> "Unknown command.",
      "lang.usage" -> "Usage:",

      "lang.help" ->
        """
          |§e----- §aCustomWorld §e-----
          |
          |§aCustomWorld §eis FreeOP Minecraft server located in Europe.
          |§eBefore you will start new journey, we recommend you to read §c/rules &efirst!
          |§eThose rules apply for everything related to CustomWorld 
          |(forums, Discord server (https://discord.gg/sgMCMrY), youtube comments and our server).
          |
          |§eYou can list avalibe commands (and usage) at our website under "Minecraft server" section:
          |§bhttp://www.customworld.ml
          |
          |§eIf you need support from our staff, please visit our Discord as mentioned above.
          |§eSincerely, §axCustomWorld§e, owner of §aCustomWorld§e.
        """.stripMargin,

      "lang.rules" ->
        """
          |Welcome to CustomWorld, a server where minigames, fun, and freeop come together.
          |
          |Here are some rules to brief over to keep our community nice and safe:
          |1) No hacking or getting into other accounts.
          |2) Griefing and trolling are strictly forbidden.
          |3) Hacked clients are forbidden.
          |4) Using alts to bypass bans or to increase power in statistics is strictly forbidden and all alts will get banned.
          |5) Do not ask for staff, use #apply-for-staff instead.
          |6) Do not insult staff members or other people.
          |7) No racism, sexism or other similar things.
          |8) Do not bully others.
          |9) Be nice to others and use common sense.
          |10) Be ethical.
          |11) Be respectful.
          |12) Do not spam in text channels.
          |13) Do not use loud voice or high pitch voices and annoying ones at voice channels.
          |14) Do not annoy others.
          |15) Respect other's privacy.
          |16) Do not advertise in any form.
          |17) These rules can change at anytime. Breaking them might result in a punishment ((temp)mute/kick/(temp)ban).
          |
          |Those rules apply for everything related to CustomWorld (forums, Discord server ( https://discord.gg/sgMCMrY ), youtube comments and of server).
        """.stripMargin,
      
      "minigame.world" -> "world",
      "minigame.spleef.template" -> "./spleef.schematic",
      "minigame.spleef.minRegion" -> new Vector(-155, -2, -56),
      "minigame.spleef.maxRegion" -> new Vector(-184, 15, -92),
      "minigame.spleef.spawnPos" -> new Vector (-170, 10, -73),
      "minigame.spleef.signPos" -> new Vector(-169, 4, -55),
      "minigame.cage" -> "./cage.schematic",

      "db.hostname" -> "localhost",
      "db.port" -> 3306,
      "db.database" -> "customworld",
      "db.username" -> "root",
      "db.password" -> "mysql",

      "permission.regular" -> List("talk", "nick", "spawn", "fawe.CustomCore").asJava,
      "permission.helper" -> List().asJava,
      "permission.builder" -> List("blocks", "spawnbuild").asJava,
      "permission.mod" -> List("tempban", "kick", "fawe.admin").asJava,
      "permission.staff" -> List("setrank", "manageips", "minecraft.command.gamemode", "minecraft.command.kick", "blocks", "ban", "cmdspy").asJava,
      "permission.staff+" -> List("noban", "hell", "config").asJava,

      "world.overworld" -> "world_nether", //nether for overworld on debug env
      "world.flatlands" -> "world",
      "world.competition" -> "world_end",
      "world.default" -> "world",

      "freeop.jumppad.force" -> 0.5,
      "freeop.jumppad.on" -> true,
      "freeop.explosions" -> true,
      "freeop.buildlimit" -> 2000,

      "discord.token" -> "NDc3NjUzMDY3NzUyMjc1OTY5.Dk_Qpw.2VWlsol5q5PcvxRSeiRa5s2ycf8",

      "permission.freeop" -> List("minecraft.command.gamemode", "fawe.permpack.basic", "world", "home", "speed", "heal", "gamemode", "build").asJava,
      "permission.muted" -> List("minecraft.command.me", "minecraft.command.tell", "talk").asJava
    ) mapValues (_.asInstanceOf[AnyRef])
    JavaConverters.mapAsJavaMap (map)
  }

  val BlockedBlocks = List(Material.COMMAND, Material.COMMAND_CHAIN, Material.COMMAND_MINECART, Material.COMMAND_REPEATING, Material.COMMAND_CHAIN, Material.STRUCTURE_BLOCK)
  val BlockedEntities = List(EntityType.MINECART_COMMAND, EntityType.MINECART_TNT)

  val cfg: FileConfiguration = CustomCore.cfg
}
