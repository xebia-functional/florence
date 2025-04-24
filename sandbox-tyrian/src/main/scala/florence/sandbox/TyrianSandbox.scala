package florence.sandbox

import scala.concurrent.duration.*
import scala.scalajs.js.annotation.*

import cats.effect.IO
import org.scalajs.dom.{HTMLCanvasElement, document}
import tyrian.*
import tyrian.Html.*

@JSExportTopLevel("TyrianApp")
object TyrianSandbox extends TyrianIOApp[Msg, Model]:

  def router: Location => Msg =
    Routing.none(Msg.NoOp)

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    ((), Cmd.Emit(Msg.CheckCanvasReady))

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
    case Msg.NoOp =>
      (model, Cmd.None)

    case Msg.RenderChart =>
      val task: IO[Msg] =
        IO.delay {
          val c = document.getElementById(Constants.CanvasId)
          if c != null then
            println("Rendering the chart.")
            CustomChart.renderChart(c.asInstanceOf[HTMLCanvasElement])
            Msg.NoOp
          else
            println("The chart canvas element isn't available yet, retrying.")
            Msg.CheckCanvasReady
        }

      (model, Cmd.Run(task))

    case Msg.CheckCanvasReady =>
      println("The chart canvas element isn't available yet, retrying.")
      (model, Cmd.emitAfterDelay(Msg.RenderChart, 0.5.seconds))

  def view(model: Model): Html[Msg] =
    div(
      canvas(
        id     := Constants.CanvasId,
        width  := 800,
        height := 400
      )()
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None
