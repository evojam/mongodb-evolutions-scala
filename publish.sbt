import scala.util.Properties

credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  Properties.envOrElse("SONATYPE_USERNAME", ""),
  Properties.envOrElse("SONATYPE_PASSWORD", ""))

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
    <scm>
      <url>git@github.com:evojam/mongodb-evolutions-scala.git</url>
      <connection>scm:git:git@github.com:evojam/mongodb-evolutions-scala.git</connection>
    </scm>
    <developers>
      <developer>
        <id>dumpstate</id>
        <name>Albert Sadowski</name>
      </developer>
      <developer>
        <id>duketon</id>
        <name>Michael Kendra</name>
        <url>http://michaelkendra.me/</url>
      </developer>
      <developer>
        <id>abankowski</id>
        <name>Artur Bańkowski</name>
      </developer>
    </developers>)

licenses := Seq("Apache 2.0 License" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("https://github.com/evojam/mongodb-evolutions-scala"))
