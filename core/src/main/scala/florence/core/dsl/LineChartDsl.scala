package florence.core.dsl

import florence.core.model.*

object LineChartDsl:

  def lineChart[A](title: String, series: LineSeries[A]*): LineChartSpec[A] =
    LineChartSpec[A](
      title = Some(title),
      series = series.toVector,
      xAxis = AxisSpec.LinearScale(label = "x", min = None, max = None),
      yAxis = AxisSpec.LinearScale(label = "y", min = None, max = None)
    )

  extension [A](spec: LineChartSpec[A])
    def withXAxis(axis: AxisSpec): LineChartSpec[A] =
      spec.copy(xAxis = axis)

    def withYAxis(axis: AxisSpec): LineChartSpec[A] =
      spec.copy(yAxis = axis)

    def withTitle(newTitle: String): LineChartSpec[A] =
      spec.copy(title = Some(newTitle))

    def addSeries(line: LineSeries[A]): LineChartSpec[A] =
      spec.copy(series = spec.series :+ line)

  def pointsSeries(
      label: String,
      points: (Double, Double)*
  ): LineSeries[Nothing] =
    LineSeries(
      label,
      LineData.Points(points.toVector)
    )

  def functionPlotSeries(
      label: String,
      f: Double => Double,
      start: Double,
      end: Double,
      sampleSize: Int
  ): LineSeries[Nothing] =
    LineSeries(
      label,
      LineData.FunctionPlot(f, start, end, sampleSize)
    )

  def genericSeries[A](
      label: String,
      data: Vector[A],
      xFn: A => Double,
      yFn: A => Double
  ): LineSeries[A] =
    LineSeries(
      label,
      LineData.GenericData(data, xFn, yFn)
    )
end LineChartDsl
