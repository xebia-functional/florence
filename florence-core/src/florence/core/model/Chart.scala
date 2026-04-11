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

package florence.core.model

enum Chart:

  case LineChart[Dom, Range](
      title: Option[String],
      series: Vector[LineSeries[Dom, Range]],
      xAxis: Axis[Dom],
      yAxis: Axis[Range]
  )

object Chart:
  object LineChart:
    type AnyChart = LineChart[?, ?]

/** A LineSeries is a function from [[Dom]] to [[Range]], that is, a set of pairs of type (Dom, Range)
  */
final case class LineSeries[Dom: Domain as domain, Range: Domain as range](
    label: String,
    data: Vector[(Dom, Range)]
):
  lazy val (domainValues, rangeValues) = data.unzip

  /** Returns the numeric positions of each data point in the chart
    */
  def getSeriesPoints(
      xAxis: Axis[Dom],
      yAxis: Axis[Range]
  ): Vector[(Double, Double)] =
    data.flatMap { (x, y) =>
      for
        xpos <- getAxisPosition(xAxis, x)
        ypos <- getAxisPosition(yAxis, y)
      yield (xpos, ypos)
    }

  /** Map the given [[value]] to a numeric position on the [[axis]]. Depending on the axis' constraints, a data point that exists in the [[LineSeries]]
    * might not be visible in the chart; for example, if a data point is outside of the range specified by the [[Axis.LinearScale]], we should exclude it
    */
  private def getAxisPosition[Type: Domain as domain](
      axis: Axis[Type],
      value: Type
  ): Option[Double] =
    domain match
      case Domain.Reals(eq) =>
        getNumericalAxisPosition(eq.substituteCo(axis), eq(value))

      case Domain.Discrete(eq) =>
        getCategoricalAxisPosition(eq.substituteCo(axis), eq(value))

  private def getNumericalAxisPosition(axis: Axis[Double], value: Double): Option[Double] =
    axis match
      case axis: Axis.LinearScale =>
        Option.when(axis.isInRange(value))(value)

  private def getCategoricalAxisPosition(axis: Axis[String], value: String): Option[Double] =
    axis match
      case axis: Axis.CategoryScale =>
        axis.positionByCategory.get(value).map(_.toDouble)

/** Represents a constraint over the values on an Axis of the Chart
  */
sealed trait Axis[Type]:
  def label: String

  def withLabel(newLabel: String): Axis[Type]

object Axis:

  type AnyAxis = Axis[?]

  /** Constrains the axis values using an inclusive range [min, max]
    */
  final case class LinearScale(
      override val label: String,
      min: Option[Double],
      max: Option[Double]
  ) extends Axis[Double]:
    override def withLabel(newLabel: String): Axis[Double] =
      this.copy(label = newLabel)

    def isInRange(x: Double): Boolean =
      min.fold(true)(_ <= x) && max.fold(true)(_ >= x)

  /** Constrains the axis of a Discrete Chart to the given sequence of [[categories]]. This means that the chart
    * will only show the data points that are contained within this sequence (even if the original [[LineSeries]] had more categories),
    * and the order in which the categories are laid on the axis will be the same as the order from the [[categories]] sequence (from left to right)
    */
  final case class CategoryScale(
      override val label: String,
      categories: Vector[String]
  ) extends Axis[String]:
    lazy val positionByCategory: Map[String, Int] =
      categories.lazyZip(1 to categories.size).toMap

    override def withLabel(newLabel: String): Axis[String] =
      this.copy(label = newLabel)
