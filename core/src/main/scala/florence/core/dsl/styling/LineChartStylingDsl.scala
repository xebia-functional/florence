package florence.core.dsl.styling

import florence.core.model.ChartDef.LineChart
import florence.core.model.styling.*
import florence.core.model.styling.ChartStyleDef.LineChartStyle

object LineChartStylingDsl:

  type StyledLineChart = StyledChart[LineChart, LineChartStyle]

  def lineChartStyle(
      commonProps: CommonStyleProps = CommonStyleProps(),
      xAxis: AxisStyle = AxisStyle(),
      yAxis: AxisStyle = AxisStyle(),
      seriesStyles: Map[Int, LineSeriesStyle] = Map.empty,
      defaultSeriesStyle: LineSeriesStyle = LineSeriesStyle(),
      showPoints: Boolean = true
  ): LineChartStyle =
    LineChartStyle(commonProps, xAxis, yAxis, seriesStyles, defaultSeriesStyle, showPoints)

  extension (style: LineChartStyle)
    def withXAxis(axis: AxisStyle): LineChartStyle =
      style.copy(xAxis = axis)

    def withYAxis(axis: AxisStyle): LineChartStyle =
      style.copy(yAxis = axis)

    def withSeriesStyle(index: Int, seriesStyle: LineSeriesStyle): LineChartStyle =
      style.copy(seriesStyles = style.seriesStyles + (index -> seriesStyle))

    def withDefaultSeriesStyle(seriesStyle: LineSeriesStyle): LineChartStyle =
      style.copy(defaultSeriesStyle = seriesStyle)

    def withShowPoints(showPoints: Boolean): LineChartStyle =
      style.copy(showPoints = showPoints)
