import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.linker.interface.ModuleKind
import sbtprojectmatrix.ProjectMatrixPlugin.autoImport._

Global / onChangedBuildSource := ReloadOnSourceChanges

val scala3Version = "3.7.3"

ThisBuild / organization  := "com.xebia"
ThisBuild / scalaVersion  := scala3Version
ThisBuild / versionScheme := Some("early-semver")

addCommandAlias(
  "ci-test",
  "scalafmtCheckAll; scalafmtSbtCheck; scalafix --check; ++test"
)
addCommandAlias("ci-docs", "github; documentation/mdoc; headerCreateAll")
addCommandAlias("ci-publish", "github; ci-release")

lazy val noPublishSettings = Seq(
  publish         := {},
  publishLocal    := {},
  publishArtifact := false,
  publish / skip  := true
)

lazy val core = (projectMatrix in file("core"))
  .settings(
    name := "florence-core"
  )
  .jvmPlatform(Seq(scala3Version))
  .jsPlatform(Seq(scala3Version))

lazy val coreJVM = core.jvm(scala3Version)
lazy val coreJS  = core.js(scala3Version)

lazy val rendererJS = (project in file("renderer/js"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(coreJS)
  .settings(
    name                            := "florence-renderer-js",
    scalaJSUseMainModuleInitializer := false,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.1"
  )

lazy val rendererJVM = (project in file("renderer/jvm"))
  .dependsOn(coreJVM)
  .settings(
    name := "florence-renderer-jvm"
  )

lazy val sandboxJS = (project in file("sandbox/js"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(rendererJS)
  .settings(noPublishSettings)
  .settings(
    name                            := "florence-sandbox-js",
    scalaJSUseMainModuleInitializer := true,
    scalaJSMainModuleInitializer := Some(
      org.scalajs.linker.interface.ModuleInitializer
        .mainMethod("florence.examples.js.Example", "main")
    ),
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
  )

lazy val sandboxJVM = (project in file("sandbox/jvm"))
  .dependsOn(rendererJVM)
  .settings(noPublishSettings)
  .settings(
    name := "florence-sandbox-jvm"
  )

lazy val sandboxTyrian =
  (project in file("sandbox-tyrian"))
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(rendererJS)
    .settings(noPublishSettings)
    .settings(
      name := "tyrian-sandbox",
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "tyrian-io" % "0.14.0",
        "org.scalameta"   %%% "munit"     % "1.1.1" % Test
      ),
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
    )

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .settings(noPublishSettings)
  .settings(mdocOut := file("."))
  .settings(scalaVersion := scala3Version)

lazy val root = (project in file("."))
  .aggregate(coreJVM, coreJS, rendererJS, rendererJVM, sandboxJS, sandboxJVM, sandboxTyrian)
  .settings(noPublishSettings)
  .settings(
    name := "florence"
  )
