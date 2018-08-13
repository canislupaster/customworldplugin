package scala.com.thomas.customworld.commands.build

import scala.com.thomas.customworld.commands.base.{CommandPart, PermissionCommand}
import scala.com.thomas.customworld.db.BuildDB
import scala.com.thomas.customworld.messaging.{ErrorMsg, SuccessMsg}
import scala.com.thomas.customworld.utility._

class MakeThemeCommand extends PermissionCommand("theme", (sender, cmd, _, args) => {
    args match {
      case Array(TimeParser(start),TimeParser(end), name) =>
        if (new BuildDB().autoClose(_.addTheme(name, start, end)) > 0) SomeArr(SuccessMsg) else SomeArr(ErrorMsg("alreadyexists"))
      case _ => None
    }
  })