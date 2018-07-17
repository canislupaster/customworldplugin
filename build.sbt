lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion := "2.12.5"
    )),
    name := "CustomWorldPlugin",
    resolvers += "spigot-repo" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
    resolvers += "fawe-repo" at "http://ci.athion.net/job/FastAsyncWorldEdit/ws/mvn/",
    libraryDependencies += "org.spigotmc" % "spigot-api" % "1.12.2-R0.1-SNAPSHOT" % "provided" intransitive(),
    libraryDependencies += "com.github.takezoe" %% "scala-jdbc" % "1.0.5" % "compile",
    libraryDependencies += "com.boydti" %% "fawe-api" % "latest" % "provided",
    version := "2",
    assemblyJarName in assembly := "CustomWorldPlugin.jar",
    assemblyOutputPath in assembly := file("../../CustomWorldPlugin.jar"),
    assemblyShadeRules in assembly ++= Seq(
      ShadeRule.rename("com.github.takezoe.scala.jdbc.**" -> "scalajbdc.@1").inAll
    )
  )
