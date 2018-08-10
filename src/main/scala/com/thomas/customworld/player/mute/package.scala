package scala.com.thomas.customworld.player

import java.sql.Timestamp

package object mute {
  case class Mute (ip: String, time: Option[Timestamp], reason:String)
}
