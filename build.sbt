import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.linker.interface.ModuleKind
import sbtprojectmatrix.ProjectMatrixPlugin.autoImport._

val scala3Version = "3.6.4"

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
  .settings(
    scalaJSUseMainModuleInitializer := false,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
  )

lazy val coreJVM = core.jvm(scala3Version)
lazy val coreJS  = core.js(scala3Version)

lazy val rendererJS = (project in file("renderer/js"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(coreJS)
  .settings(
    name                            := "florence-renderer-js",
    scalaJSUseMainModuleInitializer := false,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0"
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

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .settings(noPublishSettings)
  .settings(mdocOut := file("."))
  .settings(scalaVersion := scala3Version)

lazy val root = (project in file("."))
  .aggregate(coreJVM, coreJS, rendererJS, rendererJVM, sandboxJS, sandboxJVM)
  .settings(noPublishSettings)
  .settings(
    name := "florence"
  )
