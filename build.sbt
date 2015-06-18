name := "scalaz-traffic-lights"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  Seq(
    "org.scalaz" %% "scalaz-core" % "7.1.2",
    "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
  )
}