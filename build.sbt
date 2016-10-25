lazy val `si2712-regression` =
  project.in(file(".")).enablePlugins(AutomateHeaderPlugin, GitVersioning)

libraryDependencies ++= Vector(
  "org.typelevel" %% "cats" % "0.7.2",
  Library.scalaTest % "test"
)

initialCommands := """|import com.github.baccata.si2712.regression._
                      |""".stripMargin
