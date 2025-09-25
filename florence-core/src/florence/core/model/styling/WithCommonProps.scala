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
import florence.core.model.styling.ChartStyle.LineChartStyle

trait WithCommonProps[T]:
  def getCommonProps(t: T): CommonStyleProps
  def withCommonProps(t: T, props: CommonStyleProps): T

object WithCommonProps:

  extension [T](style: T)(using withCommonProps: WithCommonProps[T])
    def background: BackgroundStyle = withCommonProps.getCommonProps(style).background
    def border: BorderStyle         = withCommonProps.getCommonProps(style).border
    def title: TitleStyle           = withCommonProps.getCommonProps(style).title
    def legend: LegendStyle         = withCommonProps.getCommonProps(style).legend
    def padding: Padding            = withCommonProps.getCommonProps(style).padding
    def width: Int                  = withCommonProps.getCommonProps(style).width
    def height: Int                 = withCommonProps.getCommonProps(style).height
    def margins: Margins            = withCommonProps.getCommonProps(style).margins

    def withBackground(bg: BackgroundStyle): T =
      withCommonProps.withCommonProps(
        style,
        withCommonProps.getCommonProps(style).copy(background = bg)
      )

    def withBorder(border: BorderStyle): T =
      withCommonProps.withCommonProps(
        style,
        withCommonProps.getCommonProps(style).copy(border = border)
      )

    def withTitle(title: TitleStyle): T =
      withCommonProps.withCommonProps(
        style,
        withCommonProps.getCommonProps(style).copy(title = title)
      )

    def withLegend(legend: LegendStyle): T =
      withCommonProps.withCommonProps(
        style,
        withCommonProps.getCommonProps(style).copy(legend = legend)
      )

    def withPadding(padding: Padding): T =
      withCommonProps.withCommonProps(
        style,
        withCommonProps.getCommonProps(style).copy(padding = padding)
      )

    def withWidth(width: Int): T =
      withCommonProps.withCommonProps(
        style,
        withCommonProps.getCommonProps(style).copy(width = width)
      )

    def withHeight(height: Int): T =
      withCommonProps.withCommonProps(
        style,
        withCommonProps.getCommonProps(style).copy(height = height)
      )

    def withMargins(margins: Margins): T =
      withCommonProps.withCommonProps(
        style,
        withCommonProps.getCommonProps(style).copy(margins = margins)
      )

  end extension

  given WithCommonProps[LineChartStyle] with
    def getCommonProps(style: LineChartStyle): CommonStyleProps =
      style.commonProps

    def withCommonProps(
        style: LineChartStyle,
        props: CommonStyleProps
    ): LineChartStyle =
      style.copy(commonProps = props)

  given WithCommonProps[ChartStyle] with

    def getCommonProps(style: ChartStyle): CommonStyleProps = style match
      case s: LineChartStyle =>
        summon[WithCommonProps[LineChartStyle]].getCommonProps(s)

    def withCommonProps(style: ChartStyle, props: CommonStyleProps): ChartStyle =
      style match
        case s: LineChartStyle =>
          summon[WithCommonProps[LineChartStyle]].withCommonProps(s, props)

end WithCommonProps
