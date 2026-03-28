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

package florence.sandbox

import scala.annotation.nowarn

import org.scalajs.dom
import org.scalajs.dom.{HTMLCanvasElement, document}

import florence.*
import florence.instances.given

object Example:

  def main(args: Array[String]): Unit =
    dom.document.addEventListener(
      "DOMContentLoaded",
      { (_: dom.Event) =>
        setupChart()
      }
    )

  @nowarn("msg=unused")
  private def setupChart(): Unit =
    val canvas = document.getElementById("chart-canvas").asInstanceOf[HTMLCanvasElement]
    if canvas == null then
      val newCanvas = document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
      newCanvas.id = "chart-canvas"
      newCanvas.width = 1100
      newCanvas.height = 800
      document.body.appendChild(newCanvas)
      renderChart(newCanvas)
    else renderChart(canvas)

  private def renderChart(canvas: HTMLCanvasElement): Unit =
    // Taken from: https://www.metoffice.gov.uk/pub/data/weather/uk/climate/stationdata/heathrowdata.txt
    val tmax2023 = Vector(
      ("Jan", 9.0),
      ("Feb", 10.8),
      ("Mar", 11.5),
      ("Apr", 14.6),
      ("May", 18.6),
      ("Jun", 25.3),
      ("Jul", 22.9),
      ("Aug", 23.0),
      ("Sep", 24.4),
      ("Oct", 18.1),
      ("Nov", 11.8),
      ("Dec", 10.9)
    )
    val tmin2023 = Vector(
      ("Jan", 2.5),
      ("Feb", 3.4),
      ("Mar", 5.2),
      ("Apr", 5.9),
      ("May", 9.3),
      ("Jun", 13.5),
      ("Jul", 14.0),
      ("Aug", 13.8),
      ("Sep", 14.7),
      ("Oct", 10.2),
      ("Nov", 5.4),
      ("Dec", 5.9)
    )

    val tmax2024 = Vector(
      ("Jan", 8.4),
      ("Feb", 12.2),
      ("Mar", 13.1),
      ("Apr", 15.0),
      ("May", 19.6),
      ("Jun", 22.0),
      ("Jul", 23.5),
      ("Aug", 24.5),
      ("Sep", 20.1),
      ("Oct", 16.4),
      ("Nov", 11.2),
      ("Dec", 9.9)
    )
    val tmin2024 = Vector(
      ("Jan", 2.5),
      ("Feb", 6.0),
      ("Mar", 5.9),
      ("Apr", 7.2),
      ("May", 10.6),
      ("Jun", 11.1),
      ("Jul", 13.8),
      ("Aug", 14.6),
      ("Sep", 11.9),
      ("Oct", 9.4),
      ("Nov", 6.0),
      ("Dec", 5.2)
    )
    val tmax2025 = Vector(
      ("Jan", 7.3),
      ("Feb", 8.8)
    )
    val tmin2025 = Vector(
      ("Jan", 1.4),
      ("Feb", 2.8)
    )

    val chart =
      lineChart(
        "Heathrow Min & Max Temps (2023–2025)",
        pointsSeries("TMax 2023", tmax2023*),
        pointsSeries("TMin 2023", tmin2023*),
        pointsSeries("TMax 2024", tmax2024*),
        pointsSeries("TMin 2024", tmin2024*),
        pointsSeries("TMax 2025", tmax2025*),
        pointsSeries("TMin 2025", tmin2025*)
      )
        .withXAxisLabel("Month")
        .withYAxisLabel("Temperature (°C)")
    val style = lineChartStyle(
      xAxis = AxisStyle(labelFont = Some(FontSpec("sans-serif", 1.rem, "bold"))),
      yAxis = AxisStyle(labelFont = Some(FontSpec("sans-serif", 2.rem, "bold")))
    )
      .withDefaultSeriesStyle(
        LineSeriesStyle(
          markerType = MarkerType.Circle,
          markerSize = Some(3.0)
        )
      )
      .withSeriesStyle(
        0,
        LineSeriesStyle(colour = Some("#1f77b4"), markerType = MarkerType.Circle)
      ) // TMax 2023 - blue
      .withSeriesStyle(
        1,
        LineSeriesStyle(
          colour = Some("#1f77b4"),
          markerType = MarkerType.Circle,
          lineType = LineType.Dashed
        )
      )
      .withSeriesStyle(
        2,
        LineSeriesStyle(colour = Some("#ff7f0e"), markerType = MarkerType.Square)
      ) // TMax 2024 - orange
      .withSeriesStyle(
        3,
        LineSeriesStyle(
          colour = Some("#ff7f0e"),
          markerType = MarkerType.Square,
          lineType = LineType.Dashed
        )
      )
      .withSeriesStyle(
        4,
        LineSeriesStyle(colour = Some("#9467bd"), markerType = MarkerType.Triangle)
      ) // TMax 2025 - purple
      .withSeriesStyle(
        5,
        LineSeriesStyle(
          colour = Some("#9467bd"),
          markerType = MarkerType.Triangle,
          lineType = LineType.Dashed
        )
      )
      .withLegend(
        LegendStyle(
          position = LegendPosition.Right,
          font = FontSpec("sans-serif", 10.px, "normal"),
          itemSpacing = 16.0,
          symbolSize = 10.0
        )
      )
      .withTitle(
        TitleStyle(font = FontSpec("sans-serif", 3.rem, "bold"), colour = "black", margin = 5)
      )
      .withWidth(1100)
      .withHeight(800)
      .withMargins(Margins(60, 80, 50, 110))
    val styledChart = chart.withStyling(style)
    styledChart.renderAtCanvasSize(canvas.getContext2D())
  end renderChart
end Example
