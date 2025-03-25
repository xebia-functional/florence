package florence.core.dsl.styling

import florence.core.model.styling.*

object LineChartStylingDsl:
  def lineChartStyle(): LineChartStyleSpec = LineChartStyleSpec()

  extension (style: LineChartStyleSpec)
    def withXAxis(axis: AxisStyle): LineChartStyleSpec =
      style.copy(xAxis = axis)

    def withYAxis(axis: AxisStyle): LineChartStyleSpec =
      style.copy(yAxis = axis)

    def withSeriesStyle(index: Int, seriesStyle: LineSeriesStyle): LineChartStyleSpec =
      style.copy(seriesStyles = style.seriesStyles + (index -> seriesStyle))

    def withDefaultSeriesStyle(seriesStyle: LineSeriesStyle): LineChartStyleSpec =
      style.copy(defaultSeriesStyle = seriesStyle)
