import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.linker.interface.ModuleKind
import sbtprojectmatrix.ProjectMatrixPlugin.autoImport._

val scala3Version = "3.6.4"

ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val commonSettings = Seq(
  organization := "com.xebia",
  scalaVersion := scala3Version
)

lazy val core = (projectMatrix in file("core"))
  .settings(
    commonSettings,
    name := "florence-core"
  )
  .jvmPlatform(Seq(scala3Version))
  .jsPlatform(Seq(scala3Version))
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
  )

lazy val coreJVM = core.jvm(scala3Version)
lazy val coreJS  = core.js(scala3Version)

lazy val rendererJS = (project in file("rendererJS"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(coreJS)
  .settings(
    commonSettings,
    name                            := "florence-renderer-js",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0"
  )

lazy val rendererJVM = (project in file("rendererJVM"))
  .dependsOn(coreJVM)
  .settings(
    commonSettings,
    name := "florence-renderer-jvm"
  )

lazy val root = (project in file("."))
  .aggregate(coreJVM, coreJS, rendererJS, rendererJVM)
  .settings(
    commonSettings,
    name           := "florence",
    publish / skip := true
  )
