package florence.renderer

import munit.FunSuite

import florence.*
import florence.core.rendering.Drawing
import florence.instances.given

class RenderToImplicitResolutionTests extends FunSuite:

  test("plain typed line charts can render through renderTo"):
    val chart: Chart.LineChart[Double, Double] =
      lineChart("Demo", pointsSeries("s", 1.0 -> 2.0))

    given Renderer[Any] with
      override def render(drawing: Drawing, context: Any): Unit =
        ()

    chart.renderTo(())
