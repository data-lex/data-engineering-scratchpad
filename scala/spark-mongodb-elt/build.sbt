ThisBuild / version := "1.0.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.16"

lazy val root = (project in file("."))
  .settings(
    name := "spark-mongodb-elt",
    idePackagePrefix := Some("elt.mongodb"),
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.7.2",
      "javax.xml.bind" % "jaxb-api" % "2.3.1",
      "org.apache.spark" %% "spark-core" % "3.3.0" % Provided,
      "org.apache.spark" %% "spark-sql" % "3.3.0" % Provided,
      "org.json" % "json" % "20220320",
      "org.mongodb.spark" %% "mongo-spark-connector" % "3.0.2",
      "software.amazon.awssdk" % "secretsmanager" % "2.17.256",
      "software.amazon.awssdk" % "sns" % "2.17.256"
    )
  )
