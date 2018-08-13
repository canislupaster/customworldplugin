package scala.com.thomas.customworld.commands.mod

import scala.com.thomas.customworld.player.freeop
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.utility.{Dbl, Int, spaceJoin}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

import scala.com.thomas.customworld.commands.base.PermissionCommand
import scala.com.thomas.customworld.utility

class JumppadCommand(cfg:FileConfiguration) extends PermissionCommand("jumppad", (sender,cmd,label,args) => {
    args match {
      case Array("on", Dbl(strength)) =>
        cfg.set("freeop.jumppad.on", true)
        cfg.set("freeop.jumppad.force", strength)
        freeop.jumppadSettings.update(cfg)

        utility.SomeArr(SuccessMsg)
      case Array("off") =>
        cfg.set("freeop.jumppad.on", false)
        freeop.jumppadSettings.update(cfg)

        utility.SomeArr(SuccessMsg)
      case _ => None
    }
  })
