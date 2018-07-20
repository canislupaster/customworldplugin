package com.thomas.customworld.commands

import java.util.UUID

import com.thomas.customworld.db.{DBConstructor, PlayerDB}
import com.thomas.customworld.messaging._
import com.thomas.customworld.{messaging, rank, util}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

import scala.Option

class RankCommand (sqldb: DBConstructor) extends CommandExecutor {
  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    val udb = new PlayerDB(sqldb())

    def GetPlayerRank (x:UUID) {
      InfoMsg("rankis", Some((udb getRank x).toString)).asInstanceOf[Message] sendClient sender
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
        udb.GetUUIDFromName(pname) match {
          case None => ErrorMsg("noplayer").asInstanceOf[Message] sendClient sender
          case Some(x) => GetPlayerRank(x)
        }

        true

      case Array(pname, newrankstr) =>

        if (sender match {
            case x:Player =>
              x.hasPermission("setrank")
            case _ => true
          })
        {
          (udb.GetUUIDFromName(pname) match {
              case Some(x:UUID) =>
                val newrank = rank.Ranks find (x => x.toString == newrankstr)
                newrank match {
                  case Some(y) =>
                    sender.getServer.getPlayer(x) match {
                      case null => ()
                      case player:Player =>
                        rank.updateRank (player, Some(udb getRank x), y)
                    }

                    udb SetRank(x, y)
                    udb close()
                    SuccessMsg()
                  case _ => ErrorMsg("invalidarg")
                }
              case None => ErrorMsg("noplayer")
          }) sendClient sender
        true } else {false}

      case _ => false
    }
  }
}