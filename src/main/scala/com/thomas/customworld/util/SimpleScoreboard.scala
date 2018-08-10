package scala.com.thomas.customworld.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.{DisplaySlot, Objective, Scoreboard, Team}

case class SimpleScoreboard (title:String, scoreboard: Scoreboard, scores: Array[String], teams: Array[Team]) {
  def fixDuplicates (x:String): String = {
    if (scores.contains(x)) { fixDuplicates(x+"§r") } else x
  }

  def add(x:String, score:Int): SimpleScoreboard = {
    copy(scores = scores :+ (((x:String) => if (x.length > 48) x.substring (0, 48) else x) apply fixDuplicates(x)))
  }

  def add(x:String): SimpleScoreboard = add(x, scores.length)

  def addMultiple (y:Array[String]): SimpleScoreboard = {
    copy (scores = scores ++ y map (x => ((x:String) => if (x.length > 48) x.substring (0, 48) else x) apply fixDuplicates(x)))
  }

  def blank(): SimpleScoreboard = {
    add(" ")
  }

  private def createTeam(text: String): (Team, String) = {
    var result: String = ""
    if (text.length <= 16) return (null, text)
    val team: Team = scoreboard.registerNewTeam("text-" + scoreboard.getTeams.size)
    val iterator = text.grouped(16)
    team.setPrefix(iterator.next())
    result = iterator.next()
    if (text.length > 32) team.setSuffix(iterator.next())
    (team, result)
  }

  def build(): SimpleScoreboard = {
    val obj: Objective = scoreboard.registerNewObjective(if (title.length > 16) title.substring(0, 15) else title, "dummy")
    obj.setDisplayName(title)
    obj.setDisplaySlot(DisplaySlot.SIDEBAR)
    var index: Int = scores.length

    scores foreach ((teamname:String) => {
      val (team, str) = createTeam(teamname)
      if (team != null) team.addEntry(str)
      obj.getScore(str).setScore(index)

      index -= 1
    })

    this
  }

  def getScoreboard: Scoreboard = scoreboard

  def send(players: Array[Player]): Unit = {
    for (p <- players) {
      p.setScoreboard(scoreboard)
    }
  }

  def this(x:String) {
    this (x, Bukkit.getScoreboardManager.getNewScoreboard, Array(), Array())
  }
}