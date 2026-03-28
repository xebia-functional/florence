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

final case class LineSeries[Dom, Range](
    label: String,
    data: Vector[(Dom, Range)]
)(using val domain: Domain[Dom], val range: Domain[Range]):
  lazy val (domainValues, rangeValues) = data.unzip

  /** Returns the actual numeric positions of each data point
    */
  lazy val getSeriesPoints: Vector[(Double, Double)] =
    getDomainPositions(domain, domainValues).zip(getDomainPositions(range, rangeValues))

  private def getDomainPositions[Type](domain: Domain[Type], values: Vector[Type]): Vector[Double] =
    domain match
      case Domain.Reals(eq)   => eq.substituteCo(values)
      case Domain.Discrete(_) => Vector.range(1, values.size + 1).map(_.toDouble)

enum Axis[Type]:
  val label: String

  case LinearScale(
      override val label: String,
      min: Option[Double],
      max: Option[Double]
  ) extends Axis[Double]

  case CategoryScale(
      override val label: String,
      categories: Vector[String]
  ) extends Axis[String]

  def withLabel(newLabel: String): Axis[Type] = this match
    case axis: LinearScale   => axis.copy(label = newLabel)
    case axis: CategoryScale => axis.copy(label = newLabel)

object Axis:
  type AnyAxis = Axis[?]
