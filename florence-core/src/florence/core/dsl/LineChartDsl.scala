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

import scala.collection.mutable

import florence.core.model.*
import florence.core.model.Chart.LineChart

object LineChartDsl:

  def lineChart[Dom, Range](
      title: String,
      series: LineSeries[Dom, Range]*
  )(using domain: Domain[Dom], range: Domain[Range]): LineChart[Dom, Range] =
    LineChart(
      title = Some(title),
      series = series.toVector,
      xAxis = defaultAxis(label = "x", domain) { series.flatMap(_.domainValues) },
      yAxis = defaultAxis(label = "y", range) { series.flatMap(_.rangeValues) }
    )

  private def defaultAxis[Type](
      label: String,
      domain: Domain[Type]
  )(points: => Iterable[Type]): Axis[Type] =
    domain match
      case Domain.Reals(eq) =>
        eq.substituteContra(defaultNumericAxis(label))
      case Domain.Discrete(eq) =>
        eq.substituteContra(defaultCategoricalAxis(label, eq.substituteCo(points)))

  /** Returns the default numeric Axis specification, which is the range (-∞, +∞)
    */
  private def defaultNumericAxis(label: String): Axis[Double] =
    Axis.LinearScale(label, min = None, max = None)

  /** Returns the default Categorical Axis specification. In this function we infer the categories and the order
    * in which they should appear on the axis, so that the line chart construction is more user friendly.
    *
    * We assume that the categories that we should display in the axis are the unique labels from the [[categories]] sequence,
    * and we show them in the order that they first appear. For example:
    *
    * If we receive the following labels:
    *
    *     List("Mon", "Tue", "Wed", "Tue", "Mon", "Thu", "Sat")
    *
    * The order that we show in the axis is:
    *
    *     List("Mon", "Tue", "Wed", "Thu", "Sat").
    *
    * Since the user can specify multiple line series, we traverse all the series in the same order that the user specified, and we yield a single axis specification.
    * For example, if the user specified the following sequence of series:
    *
    *      series = List(List("Mon" -> 1.0, "Tue" -> 2.0, "Mon" -> 3.0, "Fri" -> 4.0), List("Sat" -> 5.0, "Sun" -> 10.0), List("Thu" -> 6.0, "Mon" -> 12.0, "Sat" -> 5.0))
    *
    * The order in which we display the categories in the x-axis is:
    *
    *      List("Mon", "Tue", "Fri", "Sat", "Sun", "Thu")
    *
    * The user can still manually build a [[Axis.CategoryScale]] to specify the order in which the labels should appear in the axis
    */
  private def defaultCategoricalAxis(
      label: String,
      categories: Iterable[String]
  ): Axis[String] =
    val seenCategories = mutable.Set.empty[String]
    val axisCategories = Vector.newBuilder[String]
    for category <- categories do
      if !seenCategories(category) then
        axisCategories += category
        seenCategories += category

    Axis.CategoryScale(label, categories = axisCategories.result())

  extension [Dom, Range](chart: LineChart[Dom, Range])
    def withXAxisLabel(newLabel: String): LineChart[Dom, Range] =
      withXAxis(chart.xAxis.withLabel(newLabel))

    def withYAxisLabel(newLabel: String): LineChart[Dom, Range] =
      withYAxis(chart.yAxis.withLabel(newLabel))

    def withXAxis(axis: Axis[Dom]): LineChart[Dom, Range] =
      chart.copy(xAxis = axis)

    def withYAxis(axis: Axis[Range]): LineChart[Dom, Range] =
      chart.copy(yAxis = axis)

    def withTitle(newTitle: String): LineChart[Dom, Range] =
      chart.copy(title = Some(newTitle))

    def withNoTitle: LineChart[Dom, Range] =
      chart.copy(title = None)

    def withSeries(series: Vector[LineSeries[Dom, Range]]): LineChart[Dom, Range] =
      chart.copy(series = series)

    def addSeries(line: LineSeries[Dom, Range]): LineChart[Dom, Range] =
      chart.copy(series = chart.series :+ line)

  def pointsSeries[Dom, Range](
      label: String,
      points: (Dom, Range)*
  )(using Domain[Dom], Domain[Range]): LineSeries[Dom, Range] =
    LineSeries(label, points.toVector)

  def functionPlotSeries(
      label: String,
      f: Double => Double,
      start: Double,
      end: Double,
      sampleSize: Int
  ): LineSeries[Double, Double] =
    val step   = (end - start) / (sampleSize - 1).max(1)
    val points = (0 until sampleSize).map(i => (start + i * step, f(start + i * step))).toVector

    LineSeries(label, points)

  def genericSeries[A](
      label: String,
      data: Vector[A],
      xFn: A => Double,
      yFn: A => Double
  ): LineSeries[Double, Double] =
    LineSeries(label, data.map(d => (xFn(d), yFn(d))))

end LineChartDsl
