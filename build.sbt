name := """adatb"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.apache.solr" % "solr-commons-csv" % "3.5.0",
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4"
)
