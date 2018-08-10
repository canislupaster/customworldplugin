package scala.com.thomas.customworld.player

import java.sql.Timestamp

package object ip {
  case class IpBan (ip: String, time: Option[Timestamp], reason:String)
}
