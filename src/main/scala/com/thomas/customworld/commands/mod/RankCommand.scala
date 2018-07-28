package com.thomas.customworld.commands.mod

import java.util.UUID

import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import com.thomas.customworld.messaging._
import com.thomas.customworld.player
import com.thomas.customworld.player.rank
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import com.thomas.customworld.util._

class RankCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    val udb = new PlayerDB()

    def GetPlayerRank (x:UUID) {
      InfoMsg(ConfigMsg("rankis"), RuntimeMsg((udb getRank x).toString)) sendClient sender
      udb close()
    }

    args match {
      case Array() =>
        sender match {
          case x:Player =>
            GetPlayerRank(x.getUniqueId)
          case _ => ErrorMsg("noconsole").asInstanceOf[Message] sendClient sender
        }
        true

      case Array(pname) =>
        udb.getUUIDFromName(pname) match {
          case None => ErrorMsg("noplayer").asInstanceOf[Message] sendClient sender
          case Some(x) => GetPlayerRank(x)
        }

        true

      case Array(pname, newrankstr) if sender.hasPermission("setrank") =>

        val currank = sender match {
          case x: Player if x.getName.toLowerCase == pname.toLowerCase =>
            ErrorMsg("norankyourself") sendClient sender
            None
          case x: Player => Some(rank.ranks indexOf (udb getRank x.getUniqueId))
          case _ => Some(Integer.MAX_VALUE)
        }

        currank match {
          case Some(r) => (udb.getUUIDFromName(pname) match {
            case Some(x: String) if rank.ranks.indexOf(udb getRank x) > r => ErrorMsg("norankhigher")
            case Some(x: String) =>
              val newrank = rank.ranks find (x => x.toString.toLowerCase == newrankstr.toLowerCase)
              newrank match {
                case Some(y) if rank.ranks.indexOf(y) > r => ErrorMsg("norankhigherrank")
                case Some(y) =>
                    sender.getServer.getPlayer(toUUID(x)) match {
                      case rankplayer: Player =>
                        player.getPlayer(rankplayer).updateRank(y)
                      player.kickPlayer("&eYou got promoted to new rank, please rejoin!")
                      case null => ()
                    }

                    udb setRank(x, y)
                    udb close()
                    SuccessMsg
                  case _ => ErrorMsg("invalidarg")
                }
              case None => ErrorMsg("noplayer")
            }) sendClient sender
          case _ => () }
        true
      case _ => false
    }
  }
}
