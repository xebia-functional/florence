package florence

import munit.FunSuite

import florence.core.dsl.LineChartDsl.*
import florence.core.dsl.styling.LineChartStylingDsl.*
import florence.core.model.*
import florence.core.model.styling.*
import florence.core.model.shared.StyleTypes.*
import florence.core.rendering.*

class ResizeRenderingTests extends FunSuite:

  // Text measurement is not relevant for these tests
  private val mockTextMeasurer = new TextMeasurer:
    override def measureWidth(text: String, font: FontSpec): Double  = 0
    override def measureHeight(text: String, font: FontSpec): Double = 0

  private def hasXAxisAt(ops: List[DrawOp], width: Int, height: Int, margins: Margins): Boolean =
    ops.exists {
      case LineOp(x1, y1, x2, y2, style) =>
        val expectedY  = height - margins.bottom
        val expectedX1 = margins.left
        val expectedX2 = width - margins.right
        y1 == expectedY && y2 == expectedY && x1 == expectedX1 && x2 == expectedX2 && style.width == 2.0
      case _ => false
    }

  private def hasYAxisAt(ops: List[DrawOp], height: Int, margins: Margins): Boolean =
    ops.exists {
      case LineOp(x1, y1, x2, y2, style) =>
        val expectedX  = margins.left
        val expectedY1 = margins.top
        val expectedY2 = height - margins.bottom
        x1 == expectedX && x2 == expectedX && y1 == expectedY1 && y2 == expectedY2 && style.width == 2.0
      case _ => false
    }

  private def resizedStyle(
      base: ChartStyle.LineChartStyle,
      w: Int,
      h: Int,
      margins: Margins
  ): ChartStyle.LineChartStyle =
    base
      .withWidth(w)
      .withHeight(h)
      .withMargins(margins)
      .withXAxis(base.xAxis.copy(gridLines = false))
      .withYAxis(base.yAxis.copy(gridLines = false))

  private def simpleChart: Chart.LineChart =
    lineChart("t", pointsSeries("s", (1.0, 1.0), (2.0, 2.0)))

  test("axes respect resized width/height at 600x200"):
    val margins = Margins(20, 40, 30, 50)
    val style   = resizedStyle(lineChartStyle(), 600, 200, margins)
    val drawing = LineChartInterpreter(mockTextMeasurer).interpretLineChart(simpleChart, style)
    assert(hasXAxisAt(drawing.ops, 600, 200, margins))
    assert(hasYAxisAt(drawing.ops, 200, margins))

  test("axes respect resized width/height at 1300x300"):
    val margins = Margins(30, 60, 40, 70)
    val style   = resizedStyle(lineChartStyle(), 1300, 300, margins)
    val drawing = LineChartInterpreter(mockTextMeasurer).interpretLineChart(simpleChart, style)
    assert(hasXAxisAt(drawing.ops, 1300, 300, margins))
    assert(hasYAxisAt(drawing.ops, 300, margins))
