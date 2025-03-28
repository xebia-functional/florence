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

package florence.core.model.styling

import florence.core.model.shared.StyleTypes.*

enum ChartStyleDef:

  case LineChartStyle(
      commonProps: CommonStyleProps = CommonStyleProps(),
      xAxis: AxisStyle = AxisStyle(),
      yAxis: AxisStyle = AxisStyle(),
      seriesStyles: Map[Int, LineSeriesStyle] = Map.empty,
      defaultSeriesStyle: LineSeriesStyle = LineSeriesStyle(),
      showPoints: Boolean = true
  )

case class CommonStyleProps(
    background: BackgroundStyle = BackgroundStyle(),
    border: BorderStyle = BorderStyle(),
    title: TitleStyle = TitleStyle(),
    legend: LegendStyle = LegendStyle(),
    padding: Padding = Padding(),
    width: Int = 800,
    height: Int = 400,
    margins: Margins = Margins(top = 40, right = 40, bottom = 50, left = 70)
)

final case class BackgroundStyle(
    colour: String = "#ffffff",
    opacity: Double = 1.0
)

final case class BorderStyle(
    colour: String = "#000000",
    width: Int = 0,
    radius: Int = 0
)

final case class TitleStyle(
    font: FontSpec = FontSpec("sans-serif", 16.0, "bold"),
    colour: String = "black",
    alignment: Alignment = Alignment.Center,
    margin: Double = 20.0
)

final case class AxisStyle(
    lineColour: Option[String] = None,
    lineWidth: Option[Double] = None,
    labelFont: Option[FontSpec] = None,
    tickColour: Option[String] = None,
    tickLength: Option[Double] = None,
    gridLines: Boolean = true,
    gridLineColour: Option[String] = None,
    gridLineWidth: Option[Double] = None,
    gridLineDash: Option[Vector[Double]] = None
)

final case class LegendStyle(
    visible: Boolean = true,
    position: LegendPosition = LegendPosition.Right,
    font: FontSpec = FontSpec("sans-serif", 12.0, "normal"),
    itemSpacing: Double = 10.0,
    symbolSize: Double = 15.0,
    border: Option[BorderStyle] = None,
    background: Option[String] = None,
    padding: Padding = Padding(5, 5, 5, 5)
)

final case class LineSeriesStyle(
    colour: Option[String] = Some("black"),
    lineWidth: Option[Double] = Some(2.0),
    lineType: LineType = LineType.Solid,
    markerType: MarkerType = MarkerType.None,
    markerSize: Option[Double] = Some(3.0),
    fillArea: Boolean = false,
    fillColour: Option[String] = None
)
