package scala.com.thomas.customworld.commands.mod

import scala.com.thomas.customworld.commands.base.CommandPart
import scala.com.thomas.customworld.commands.base.{OnlinePlayerArg, PermissionCommand}
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.{player, utility}
import org.bukkit.GameMode
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import scala.com.thomas.customworld.utility.SomeArr

class SmiteCommand extends PermissionCommand("smite", (sender, cmd, label, args) => {
      args.toList match {
        case OnlinePlayerArg(tplayer: Player) :: x if x.nonEmpty =>
          player.joinFreeOP(tplayer)

          tplayer.setGameMode(GameMode.SURVIVAL)
          tplayer.getWorld.strikeLightningEffect(tplayer.getLocation)
          tplayer.damage(500)
          tplayer.getInventory.clear()
          PunishMsg (tplayer, sender, "smited", utility.spaceJoin(x)) globalBroadcast sender.getServer

          SomeArr(SuccessMsg)
        case _ :: _ => SomeArr(ErrorMsg("invalidarg"))
        case _ => None
      }
    })