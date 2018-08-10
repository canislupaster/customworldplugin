package scala.com.thomas.customworld.commands.mod

import scala.com.thomas.customworld.db.{DBConstructor, PlayerDB}
import scala.com.thomas.customworld.messaging._
import scala.com.thomas.customworld.player
import scala.com.thomas.customworld.player.rank
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import scala.com.thomas.customworld.util._

class RankCommand extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    val udb = new PlayerDB()

    def GetPlayerRank (x:UUID) {
      InfoMsg(ConfigMsg("rankis"), RuntimeMsg(udb.getPlayer(x).rank.toString)) sendClient sender
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
        udb.getPlayerFromName(pname) match {
          case None => ErrorMsg("noplayer").asInstanceOf[Message] sendClient sender
          case Some(x) => GetPlayerRank(x.playerid UUID) //TODO: oh boy this needs some cleanup
        }

        true

      case Array(pname, newrankstr) if sender.hasPermission("setrank") =>

        val currank = sender match {
          case x: Player if x.getName.toLowerCase == pname.toLowerCase =>
            ErrorMsg("norankyourself") sendClient sender
            None
          case x: Player => Some(rank.ranks indexOf udb.getPlayer(x.getUniqueId).rank)
          case _ => Some(Integer.MAX_VALUE)
        }

        currank match {
          case Some(r) => (udb.getPlayerFromName(pname) match {
            case Some(x) if rank.ranks.indexOf(x.rank) > r => ErrorMsg("norankhigher")
            case Some(x) =>
              val newrank = rank.ranks find (x => x.toString.toLowerCase == newrankstr.toLowerCase)
              newrank match {
                case Some(y) if rank.ranks.indexOf(y) > r => ErrorMsg("norankhigherrank")
                case Some(y) =>
                    sender.getServer.getPlayer(x.playerid UUID) match {
                      case rankplayer: Player =>
                        player.getPlayer(rankplayer).updateRank(y)
                      case null => ()
                    }

                    udb.updatePlayer(x.copy(rank=y))
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
