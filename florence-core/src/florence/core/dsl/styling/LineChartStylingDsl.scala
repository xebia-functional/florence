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

package florence.core.dsl.styling

import florence.core.model.Chart.LineChart
import florence.core.model.styling.*
import florence.core.model.styling.ChartStyle.LineChartStyle

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
