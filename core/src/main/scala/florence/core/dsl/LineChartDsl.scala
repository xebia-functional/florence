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

package florence.core.dsl

import florence.core.model.*
import florence.core.model.ChartDef.LineChart

object LineChartDsl:

  def lineChart(title: String, series: LineSeries*): LineChart =
    LineChart(
      title = Some(title),
      series = series.toVector,
      xAxis = AxisDef.LinearScale(label = "x", min = None, max = None),
      yAxis = AxisDef.LinearScale(label = "y", min = None, max = None)
    )

  extension [A](chart: LineChart)
    def withXAxis(axis: AxisDef): LineChart =
      chart.copy(xAxis = axis)

    def withYAxis(axis: AxisDef): LineChart =
      chart.copy(yAxis = axis)

    def withTitle(newTitle: String): LineChart =
      chart.copy(title = Some(newTitle))

    def withNoTitle: LineChart =
      chart.copy(title = None)

    def withSeries(series: Vector[LineSeries]): LineChart =
      chart.copy(series = series)

    def addSeries(line: LineSeries): LineChart =
      chart.copy(series = chart.series :+ line)

  def pointsSeries(
      label: String,
      points: (Double, Double)*
  ): LineSeries =
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
  ): LineSeries =
    LineSeries(
      label,
      LineData.FunctionPlot(f, start, end, sampleSize)
    )

  def genericSeries[A](
      label: String,
      data: Vector[A],
      xFn: A => Double,
      yFn: A => Double
  ): LineSeries =
    LineSeries(
      label,
      LineData.GenericData[A](data, xFn, yFn)
    )
end LineChartDsl
