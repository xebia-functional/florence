package florence.renderer

import munit.FunSuite

import florence.*
import florence.instances.given
import florence.core.rendering.Drawing
import florence.core.rendering.TextMeasurer

class RenderToImplicitResolutionTests extends FunSuite:

  test("plain typed line charts can render through renderTo"):
    val chart: Chart.LineChart[Double, Double] =
      lineChart("Demo", pointsSeries("s", 1.0 -> 2.0))

    given TextMeasurer with
      override def measureHeight(text: String, font: FontSpec): Double = text.size.toDouble
      override def measureWidth(text: String, font: FontSpec): Double  = text.size.toDouble

    given Renderer[Unit] with
      override def render(drawing: Drawing, context: Unit): Unit = context

    chart.renderTo(())
