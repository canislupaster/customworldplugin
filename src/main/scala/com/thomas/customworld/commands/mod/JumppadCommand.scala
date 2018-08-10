package scala.com.thomas.customworld.commands.mod

import scala.com.thomas.customworld.player.freeop
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.util.{Dbl, Int, spaceJoin}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class JumppadCommand(cfg:FileConfiguration) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    (sender, args) match {
      case (x: Player, _) if !x.hasPermission("jumppad") => false
      case (_, Array("on", Dbl(strength))) =>
        cfg.set("freeop.jumppad.on", true)
        cfg.set("freeop.jumppad.force", strength)
        freeop.jumppadSettings.update(cfg)

        SuccessMsg sendClient sender
        true
      case (_, Array("off")) =>
        cfg.set("freeop.jumppad.on", false)
        freeop.jumppadSettings.update(cfg)

        SuccessMsg sendClient sender
        true
      case _ => false
    }
  }
}
