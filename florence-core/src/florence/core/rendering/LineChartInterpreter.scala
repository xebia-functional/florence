/*
 * Copyright 2025 Xebia Functional Open Source <https://www.xebia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package florence.core.rendering

import scala.collection.mutable

import florence.core.dsl.styling.LineChartStylingDsl.*
import florence.core.model.*
import florence.core.model.Chart.LineChart
import florence.core.model.shared.FontSizeSyntax.px
import florence.core.model.shared.StyleTypes.*
import florence.core.model.shared.TextBaseline
import florence.core.model.styling.*
import florence.core.model.styling.ChartStyle.LineChartStyle
import florence.core.model.styling.WithCommonProps.*
import florence.core.model.styling.WithCommonProps.given

final class LineChartInterpreter(textMeasurer: TextMeasurer):

  /** The length of a tick mark line in pixels
    * (should this be configurable by the user?)
    */
  private val tickMarkLength = 5.0

  /** The default gap between the main chart components in pixels
    * (e.g., title, labels, tick numbers, plot)
    * (should this be configurable by the user?)
    */
  private val componentGap = 5.0

  private val defaultAxisLabelFont     = FontSpec("sans-serif", 12.px, "normal")
  private val defaultAxisTickLabelFont = FontSpec("sans-serif", 10.px, "normal")

  def interpretLineChart(
      chart: LineChart.AnyChart,
      style: LineChartStyle = LineChartStyle()
  ): Drawing =
    val chartSetup = setupChart(chart, style)
    val operations = Vector.newBuilder[DrawOp]
    operations ++= drawBackground(style)
    operations ++= drawTitle(chart, chartSetup, style)

    val gridAndLabels = drawGridAndLabels(chart, chartSetup, style)
    operations ++= gridAndLabels.operations

    operations ++= drawAxes(
      chart,
      chartSetup,
      style,
      gridAndLabels.maxXLabelHeight,
      gridAndLabels.maxYLabelWidth
    )
    operations ++= drawSeries(chart, chartSetup, style)
    operations ++= drawLegend(chart, chartSetup, style)
    Drawing(operations.result().toList)

  private case class ChartSetup(
      width: Double,
      height: Double,
      margins: Margins,
      xMin: Double,
      xMax: Double,
      yMin: Double,
      yMax: Double,
      plotWidth: Double,
      plotHeight: Double
  ):
    def transformX(x: Double): Double =
      margins.left + (x - xMin) * plotWidth / (xMax - xMin)

    def transformY(y: Double): Double =
      height - margins.bottom - (y - yMin) * plotHeight / (yMax - yMin)

  private def setupChart(
      chart: LineChart.AnyChart,
      style: LineChartStyle
  ): ChartSetup =
    val width                                    = style.width
    val height                                   = style.height
    val margins                                  = style.margins
    val plotWidth                                = width - margins.left - margins.right
    val plotHeight                               = height - margins.top - margins.bottom
    val (dataXMin, dataXMax, dataYMin, dataYMax) = calculateDataRanges(chart)
    val (xMin, xMax)                             = computeAxisRange(chart.xAxis, dataXMin, dataXMax)
    val (yMin, yMax)                             = computeAxisRange(chart.yAxis, dataYMin, dataYMax)
    ChartSetup(width, height, margins, xMin, xMax, yMin, yMax, plotWidth, plotHeight)

  private def computeAxisRange(
      axis: Axis.AnyAxis,
      dataMin: Double,
      dataMax: Double
  ): (Double, Double) =
    axis match
      case Axis.LinearScale(_, min, max) =>
        // Use provided min/max or fallback to data ranges
        (min.getOrElse(dataMin), max.getOrElse(dataMax))
      case Axis.CategoryScale(_, categories) =>
        // Axis padding: extra 5% on both sides of the axis
        val categoryCount = categories.size
        val padding       = Math.max(0.5, categoryCount * 0.05)
        (1.0 - padding, categoryCount + padding)

  private def calculateDataRanges(chart: LineChart.AnyChart): (Double, Double, Double, Double) =
    if chart.series.isEmpty then return (0.0, 100.0, 0.0, 100.0)
    val allPoints = chart.series.flatMap(_.getSeriesPoints(chart.xAxis, chart.yAxis))
    if allPoints.isEmpty then return (0.0, 100.0, 0.0, 100.0)
    val xValues = allPoints.map(_._1)
    val yValues = allPoints.map(_._2)
    val minX    = xValues.min
    val maxX    = xValues.max
    val minY    = yValues.min
    val maxY    = yValues.max
    paddedRange(minX, maxX, minY, maxY)

  private def paddedRange(
      minX: Double,
      maxX: Double,
      minY: Double,
      maxY: Double
  ): (Double, Double, Double, Double) =
    val xRange     = maxX - minX
    val yRange     = maxY - minY
    val paddedMinX = if xRange > 0 then minX - 0.05 * xRange else minX - 5.0
    val paddedMaxX = if xRange > 0 then maxX + 0.05 * xRange else maxX + 5.0
    val paddedMinY = if yRange > 0 then minY - 0.05 * yRange else minY - 5.0
    val paddedMaxY = if yRange > 0 then maxY + 0.05 * yRange else maxY + 5.0
    (paddedMinX, paddedMaxX, paddedMinY, paddedMaxY)

  private def drawBackground(style: LineChartStyle): Vector[DrawOp] =
    val bgColour = style.background.colour
    Vector(ClearOp(Some(bgColour)))

  private def drawTitle(
      chart: LineChart.AnyChart,
      setup: ChartSetup,
      style: LineChartStyle
  ): Vector[DrawOp] =
    val result = Vector.newBuilder[DrawOp]
    chart.title.foreach { title =>
      val titleStyle = style.title
      result += TextOp(
        text = title,
        x = setup.width / 2,
        y = setup.margins.top - (componentGap + titleStyle.margin),
        font = FontSpec(
          family = titleStyle.font.family,
          size = titleStyle.font.size,
          weight = titleStyle.font.weight
        ),
        colour = titleStyle.colour,
        alignment = titleStyle.alignment,
        baseline = TextBaseline.Bottom
      )
    }
    result.result()

  private def drawAxes(
      chart: LineChart.AnyChart,
      setup: ChartSetup,
      style: LineChartStyle,
      maxXLabelHeight: Double,
      maxYLabelWidth: Double
  ): Vector[DrawOp] =
    val result      = Vector.newBuilder[DrawOp]
    val xAxisColour = style.xAxis.lineColour.getOrElse("black")
    val xAxisWidth  = style.xAxis.lineWidth.getOrElse(2.0)
    val yAxisColour = style.yAxis.lineColour.getOrElse("black")
    val yAxisWidth  = style.yAxis.lineWidth.getOrElse(2.0)

    result += LineOp(
      setup.margins.left,
      setup.height - setup.margins.bottom,
      setup.width - setup.margins.right,
      setup.height - setup.margins.bottom,
      LineStyle(xAxisColour, xAxisWidth)
    )

    result += LineOp(
      setup.margins.left,
      setup.margins.top,
      setup.margins.left,
      setup.height - setup.margins.bottom,
      LineStyle(yAxisColour, yAxisWidth)
    )

    val xOrigin = setup.margins.left                  // x-position of the origin of the plot
    val yOrigin = setup.height - setup.margins.bottom // y-position of the origin of the plot

    result += TextOp(
      chart.xAxis.label,
      xOrigin + setup.plotWidth / 2,
      // accounts for the tick marks and tick label sizes with their gaps in between
      yOrigin + tickMarkLength + componentGap + maxXLabelHeight + componentGap,
      style.xAxis.labelFont.getOrElse(defaultAxisLabelFont),
      xAxisColour,
      Alignment.Center,
      TextBaseline.Hanging
    )

    // Add y-axis label using GroupOp: place + rotate -90
    result += GroupOp(
      operations = List(
        TextOp(
          chart.yAxis.label,
          0,
          0,
          style.yAxis.labelFont.getOrElse(defaultAxisLabelFont),
          yAxisColour,
          Alignment.Center,
          TextBaseline.Bottom
        )
      ),
      transform = Some(
        Transform(
          // accounts for the tick marks and tick label sizes with their gaps in between
          translateX = xOrigin - (tickMarkLength + componentGap + maxYLabelWidth + componentGap),
          translateY = yOrigin - setup.plotHeight / 2,
          rotation = -Math.PI / 2
        )
      )
    )

    result.result()
  end drawAxes

  private def drawGridAndLabels(
      chart: LineChart.AnyChart,
      setup: ChartSetup,
      style: LineChartStyle
  ): (maxXLabelHeight: Double, maxYLabelWidth: Double, operations: Vector[DrawOp]) =
    val result          = Vector.newBuilder[DrawOp]
    var maxXLabelHeight = componentGap
    var maxYLabelWidth  = componentGap
    if style.xAxis.gridLines then maxXLabelHeight = drawXAxisElements(chart, setup, style, result)
    if style.yAxis.gridLines then maxYLabelWidth = drawYAxisElements(setup, style, result)
    (maxXLabelHeight, maxYLabelWidth, result.result())

  /** Draws x-axis tick marks and labels, and returns the maximum tick label height in pixels
    */
  private def drawXAxisElements(
      chart: LineChart.AnyChart,
      setup: ChartSetup,
      style: LineChartStyle,
      result: mutable.ReusableBuilder[DrawOp, Vector[DrawOp]]
  ): Double =
    chart.xAxis match
      case Axis.CategoryScale(_, categories) =>
        drawCategoricalXAxis(categories, setup, style, result)
      case Axis.LinearScale(_, _, _) =>
        drawNumericXAxis(setup, style, result)
  end drawXAxisElements

  /** Draws x-axis tick marks and labels for a categorical scale, and returns the maximum tick label height in pixels
    */
  private def drawCategoricalXAxis(
      categories: Vector[String],
      setup: ChartSetup,
      style: LineChartStyle,
      result: mutable.ReusableBuilder[DrawOp, Vector[DrawOp]]
  ): Double =
    val xAxisColour = style.xAxis.lineColour.getOrElse("black")
    val xAxisWidth  = style.xAxis.lineWidth.getOrElse(2.0)
    val xGridColour = style.xAxis.gridLineColour.getOrElse("#e0e0e0")
    val xGridWidth  = style.xAxis.gridLineWidth.getOrElse(1.0)
    val xGridDash   = style.xAxis.gridLineDash
    val labelFont   = style.xAxis.labelFont.getOrElse(defaultAxisTickLabelFont)

    var maxXLabelHeight = 0.0
    for i <- categories.indices do
      val x             = setup.margins.left + (i + 0.5) * setup.plotWidth / categories.size
      val y             = setup.height - setup.margins.bottom // y-position of the axis line
      val tickLabelYPos = y + tickMarkLength + componentGap   // y-position of the tick label
      drawVerticalTickMark(x, y, tickMarkLength, xAxisColour, xAxisWidth, result)
      drawVerticalGridLine(x, setup.margins.top, y, xGridColour, xGridWidth, xGridDash, result)
      val labelText = categories(i)
      maxXLabelHeight = maxXLabelHeight.max(textMeasurer.measureHeight(labelText, labelFont))
      drawLabel(
        labelText,
        x,
        tickLabelYPos,
        labelFont,
        xAxisColour,
        Alignment.Center,
        TextBaseline.Hanging, // top of the label text is at (x, tickLabelYPos)
        result
      )
    maxXLabelHeight

  /** Draws x-axis tick marks and labels for a numeric scale, and returns the maximum tick label height in pixels
    */
  private def drawNumericXAxis(
      setup: ChartSetup,
      style: LineChartStyle,
      result: mutable.ReusableBuilder[DrawOp, Vector[DrawOp]]
  ): Double =
    val xAxisColour = style.xAxis.lineColour.getOrElse("black")
    val xAxisWidth  = style.xAxis.lineWidth.getOrElse(2.0)
    val xGridColour = style.xAxis.gridLineColour.getOrElse("#e0e0e0")
    val xGridWidth  = style.xAxis.gridLineWidth.getOrElse(1.0)
    val xGridDash   = style.xAxis.gridLineDash
    val labelFont   = style.xAxis.labelFont.getOrElse(defaultAxisTickLabelFont)
    val range       = setup.xMax - setup.xMin
    val step        = humanFriendlyStep(range / 10.0)
    val start       = Math.ceil(setup.xMin / step) * step
    val end         = Math.floor(setup.xMax / step) * step
    val numSteps    = ((end - start) / step).toInt + 1

    var maxXLabelHeight = 0.0
    for i <- 0 until numSteps do
      val value         = start + i * step
      val x             = setup.transformX(value)
      val y             = setup.height - setup.margins.bottom // y-position of the axis line
      val tickLabelYPos = y + tickMarkLength + componentGap   // y-position of the tick label
      drawVerticalTickMark(x, y, tickMarkLength, xAxisColour, xAxisWidth, result)
      drawVerticalGridLine(x, setup.margins.top, y, xGridColour, xGridWidth, xGridDash, result)
      val labelText = if step >= 1.0 then f"$value%.0f" else f"$value%.1f"
      maxXLabelHeight = maxXLabelHeight.max(textMeasurer.measureHeight(labelText, labelFont))
      drawLabel(
        labelText,
        x,
        tickLabelYPos,
        labelFont,
        xAxisColour,
        Alignment.Center,
        TextBaseline.Hanging, // top of the label text is at (x, tickLabelYPos)
        result
      )
    maxXLabelHeight

  /** Draws y-axis tick marks and labels for a numeric scale, and returns the maximum tick label width in pixels
    */
  private def drawYAxisElements(
      setup: ChartSetup,
      style: LineChartStyle,
      result: mutable.ReusableBuilder[DrawOp, Vector[DrawOp]]
  ): Double =
    val yAxisColour = style.yAxis.lineColour.getOrElse("black")
    val yAxisWidth  = style.yAxis.lineWidth.getOrElse(2.0)
    val yGridColour = style.yAxis.gridLineColour.getOrElse("#e0e0e0")
    val yGridWidth  = style.yAxis.gridLineWidth.getOrElse(1.0)
    val yGridDash   = style.yAxis.gridLineDash
    val labelFont   = style.yAxis.labelFont.getOrElse(defaultAxisTickLabelFont)
    val range       = setup.yMax - setup.yMin
    val step        = humanFriendlyStep(range / 5.0)
    val start       = Math.ceil(setup.yMin / step) * step
    val end         = Math.floor(setup.yMax / step) * step
    val numSteps    = ((end - start) / step).toInt + 1

    var maxLabelWidth = 0.0
    for i <- 0 until numSteps do
      val value         = start + i * step
      val x             = setup.margins.left                  // x-position of the axis line
      val y             = setup.transformY(value)
      val tickLabelXPos = x - (tickMarkLength + componentGap) // x-position of the tick label
      drawHorizontalTickMark(x, y, tickMarkLength, yAxisColour, yAxisWidth, result)
      drawHorizontalGridLine(
        x,
        setup.width - setup.margins.right,
        y,
        yGridColour,
        yGridWidth,
        yGridDash,
        result
      )
      val labelText = if step >= 1.0 then f"$value%.0f" else f"$value%.1f"
      maxLabelWidth = maxLabelWidth.max(textMeasurer.measureWidth(labelText, labelFont))
      drawLabel(
        labelText,
        tickLabelXPos,
        y,
        labelFont,
        yAxisColour,
        Alignment.Right,
        TextBaseline.Middle, // the center of the label text is at (tickLabelXPos, y)
        result
      )
    maxLabelWidth
  end drawYAxisElements

  private def drawVerticalTickMark(
      x: Double,
      y: Double,
      length: Double,
      color: String,
      width: Double,
      result: mutable.ReusableBuilder[DrawOp, Vector[DrawOp]]
  ): Unit =
    result += LineOp(
      x,
      y,
      x,
      y + length,
      LineStyle(color, width)
    )

  private def drawHorizontalTickMark(
      x: Double,
      y: Double,
      length: Double,
      color: String,
      width: Double,
      result: mutable.ReusableBuilder[DrawOp, Vector[DrawOp]]
  ): Unit =
    result += LineOp(
      x - length,
      y,
      x,
      y,
      LineStyle(color, width)
    )

  private def drawVerticalGridLine(
      x: Double,
      yStart: Double,
      yEnd: Double,
      color: String,
      width: Double,
      dash: Option[Vector[Double]],
      result: mutable.ReusableBuilder[DrawOp, Vector[DrawOp]]
  ): Unit =
    result += LineOp(
      x,
      yStart,
      x,
      yEnd,
      LineStyle(color, width, dash.map(_.toList))
    )

  private def drawHorizontalGridLine(
      xStart: Double,
      xEnd: Double,
      y: Double,
      color: String,
      width: Double,
      dash: Option[Vector[Double]],
      result: mutable.ReusableBuilder[DrawOp, Vector[DrawOp]]
  ): Unit =
    result += LineOp(
      xStart,
      y,
      xEnd,
      y,
      LineStyle(color, width, dash.map(_.toList))
    )

  private def drawLabel(
      text: String,
      x: Double,
      y: Double,
      font: FontSpec,
      color: String,
      alignment: Alignment,
      baseline: TextBaseline,
      result: mutable.ReusableBuilder[DrawOp, Vector[DrawOp]]
  ): Unit =
    result += TextOp(
      text,
      x,
      y,
      font,
      color,
      alignment,
      baseline
    )

  private def humanFriendlyStep(roughStep: Double): Double =
    val orderOfMagnitude = Math.pow(
      10,
      Math.floor(Math.log10(roughStep))
    )
    val normalised = roughStep / orderOfMagnitude // normalise to between 1 and 10
    if normalised < 1.5 then orderOfMagnitude // prefer exact multiples
    else if normalised < 3.5 then 2 * orderOfMagnitude // decent spacing
    else if normalised < 7.5 then 5 * orderOfMagnitude // declutter ticks
    else 10 * orderOfMagnitude // declutter labels

  private def drawSeries(
      chart: LineChart.AnyChart,
      setup: ChartSetup,
      style: LineChartStyle
  ): Vector[DrawOp] =
    val result = Vector.newBuilder[DrawOp]
    chart.series.zipWithIndex.foreach { case (series, index) =>
      val seriesStyle = style.seriesStyles.getOrElse(index, style.defaultSeriesStyle)
      val colour = seriesStyle.colour.orElse(style.defaultSeriesStyle.colour).getOrElse("black")
      val lineWidth =
        seriesStyle.lineWidth.orElse(style.defaultSeriesStyle.lineWidth).getOrElse(2.0)
      val lineDash = seriesStyle.lineType match
        case LineType.Solid   => None
        case LineType.Dashed  => Some(List(6.0, 2.0))
        case LineType.Dotted  => Some(List(2.0, 2.0))
        case LineType.DashDot => Some(List(6.0, 2.0, 2.0, 2.0))
      val points = series.getSeriesPoints(chart.xAxis, chart.yAxis)
      val screenPoints = points.map { case (x, y) =>
        (setup.transformX(x), setup.transformY(y))
      }
      if screenPoints.size > 1 then
        result += PolylineOp(
          screenPoints.toList,
          LineStyle(colour, lineWidth, lineDash)
        )
        if seriesStyle.fillArea then
          val fillPoints = screenPoints.toList ++ List(
            (screenPoints.last._1, setup.height - setup.margins.bottom),
            (screenPoints.head._1, setup.height - setup.margins.bottom)
          )
          val fillColour = seriesStyle.fillColour.getOrElse(colour)
          result += PolylineOp(
            fillPoints,
            LineStyle(fillColour, width = 0),
            closed = true
          )
      // Draw markers
      val showPoints = style.showPoints && seriesStyle.markerType != MarkerType.None
      if showPoints then
        val markerSize = seriesStyle.markerSize
          .orElse(style.defaultSeriesStyle.markerSize)
          .getOrElse(3.0)
        screenPoints.foreach { case (x, y) =>
          result ++= drawMarker(seriesStyle.markerType, x, y, colour, markerSize, lineWidth)
        }
    }
    result.result()
  end drawSeries

  private def drawMarker(
      markerType: MarkerType,
      x: Double,
      y: Double,
      colour: String,
      markerSize: Double,
      lineWidth: Double
  ): Vector[DrawOp] =
    markerType match
      case MarkerType.Circle =>
        Vector(
          CircleOp(
            x = x,
            y = y,
            radius = markerSize,
            fill = Some(colour),
            stroke = None
          )
        )

      case MarkerType.Square =>
        Vector(
          RectOp(
            x = x - markerSize / 2,
            y = y - markerSize / 2,
            width = markerSize,
            height = markerSize,
            fill = Some(colour),
            stroke = None
          )
        )

      case MarkerType.Triangle =>
        val height = markerSize * math.sqrt(3) / 2
        val points = List(
          (x, y - height * 2 / 3),
          (x - markerSize / 2, y + height / 3),
          (x + markerSize / 2, y + height / 3),
          (x, y - height * 2 / 3)
        )
        Vector(PolylineOp(points, LineStyle(colour, 1.0), closed = true))

      case MarkerType.Star =>
        // Create a 5-point star by generating 10 points (outer & inner)
        val starPoints = (0 until 10).map { i =>
          val radius = if i % 2 == 0 then markerSize else markerSize / 2
          val angle  = (math.Pi / 2) + i * (math.Pi / 5)
          val px     = x + radius * math.cos(angle)
          val py     = y - radius * math.sin(angle)
          (px, py)
        }.toList
        Vector(PolylineOp(starPoints, LineStyle(colour, 1.0), closed = true))

      case MarkerType.Plus =>
        Vector(
          LineOp(x - markerSize, y, x + markerSize, y, LineStyle(colour, lineWidth)),
          LineOp(x, y - markerSize, x, y + markerSize, LineStyle(colour, lineWidth))
        )

      case MarkerType.None =>
        Vector.empty

  private def drawLegend(
      chart: LineChart.AnyChart,
      setup: ChartSetup,
      style: LineChartStyle
  ): Vector[DrawOp] =
    val result      = Vector.newBuilder[DrawOp]
    val legendStyle = style.legend
    if legendStyle.visible then
      chart.series.zipWithIndex.foreach { case (series, index) =>
        val seriesStyle = style.seriesStyles.getOrElse(index, style.defaultSeriesStyle)
        val colour = seriesStyle.colour.orElse(style.defaultSeriesStyle.colour).getOrElse("black")
        val lineWidth = seriesStyle.lineWidth.getOrElse(2.0)
        val lineType  = seriesStyle.lineType
        val lineDash = lineType match
          case LineType.Solid   => None
          case LineType.Dashed  => Some(List(6.0, 2.0))
          case LineType.Dotted  => Some(List(2.0, 2.0))
          case LineType.DashDot => Some(List(6.0, 2.0, 2.0, 2.0))
        val legendPos = legendStyle.position
        val (legendX, legendY) = legendPos match
          case LegendPosition.Right =>
            (
              setup.width - setup.margins.right - 100,
              setup.margins.top + 20 + (index * legendStyle.itemSpacing)
            )
          case LegendPosition.Left =>
            (setup.margins.left, setup.margins.top + index * legendStyle.itemSpacing)
          case LegendPosition.Top =>
            (setup.margins.left + index * legendStyle.itemSpacing * 5, setup.margins.top / 2)
          case LegendPosition.Bottom =>
            (
              setup.margins.left + index * legendStyle.itemSpacing * 5,
              setup.height - setup.margins.bottom / 2
            )
          case _ =>
            (
              setup.width - setup.margins.right - legendStyle.symbolSize * 3,
              setup.margins.top + index * legendStyle.itemSpacing
            )
        result += LineOp(
          legendX,
          legendY,
          legendX + legendStyle.symbolSize,
          legendY,
          LineStyle(colour, lineWidth, lineDash)
        )
        result += TextOp(
          series.label,
          legendX + legendStyle.symbolSize + 5,
          legendY + 4,
          FontSpec(
            legendStyle.font.family,
            legendStyle.font.size,
            legendStyle.font.weight
          ),
          colour,
          Alignment.Left,
          TextBaseline.Alphabetic
        )
      }
    end if
    result.result()
  end drawLegend

end LineChartInterpreter

object LineChartInterpreterInstances:

  given lineChartInterpreter(using
      textMeasurer: TextMeasurer
  ): Interpreter[LineChart.AnyChart, Drawing] with
    def interpret(chart: LineChart.AnyChart): Drawing =
      LineChartInterpreter(textMeasurer).interpretLineChart(chart)

  given lineChartWithStyleInterpreter(using
      textMeasurer: TextMeasurer
  ): Interpreter[(LineChart.AnyChart, LineChartStyle), Drawing] with

    def interpret(args: (LineChart.AnyChart, LineChartStyle)): Drawing =
      val (chart, style) = args
      LineChartInterpreter(textMeasurer).interpretLineChart(chart, style)

  given styledLineChartInterpreter[Dom, Range](using
      textMeasurer: TextMeasurer
  ): Interpreter[StyledLineChart[Dom, Range], Drawing] with
    def interpret(styledChart: StyledLineChart[Dom, Range]): Drawing =
      LineChartInterpreter(textMeasurer).interpretLineChart(styledChart.chart, styledChart.style)
