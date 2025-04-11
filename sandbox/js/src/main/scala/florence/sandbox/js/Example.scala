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

package florence.sandbox.js

import org.scalajs.dom
import org.scalajs.dom.{HTMLCanvasElement, document}

import florence.*
import florence.instances.given
import florence.renderer.js.*
import florence.renderer.js.instances.given

object Example:

  def main(args: Array[String]): Unit =
    dom.document.addEventListener(
      "DOMContentLoaded",
      { (_: dom.Event) =>
        setupChart()
      }
    )

  private def setupChart(): Unit =
    val canvas = document.getElementById("chart-canvas").asInstanceOf[HTMLCanvasElement]
    if canvas == null then
      val newCanvas = document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
      newCanvas.id = "chart-canvas"
      newCanvas.width = 800
      newCanvas.height = 400
      document.body.appendChild(newCanvas)
      renderChart(newCanvas)
    else renderChart(canvas)

  private def renderChart(canvas: HTMLCanvasElement): Unit =
    // Taken from: https://www.metoffice.gov.uk/pub/data/weather/uk/climate/stationdata/heathrowdata.txt
    val tmax2023 = Vector(
      (1.0, 9.0),
      (2.0, 10.8),
      (3.0, 11.5),
      (4.0, 14.6),
      (5.0, 18.6),
      (6.0, 25.3),
      (7.0, 22.9),
      (8.0, 23.0),
      (9.0, 24.4),
      (10.0, 18.1),
      (11.0, 11.8),
      (12.0, 10.9)
    )
    val tmin2023 = Vector(
      (1.0, 2.5),
      (2.0, 3.4),
      (3.0, 5.2),
      (4.0, 5.9),
      (5.0, 9.3),
      (6.0, 13.5),
      (7.0, 14.0),
      (8.0, 13.8),
      (9.0, 14.7),
      (10.0, 10.2),
      (11.0, 5.4),
      (12.0, 5.9)
    )

    val tmax2024 = Vector(
      (1.0, 8.4),
      (2.0, 12.2),
      (3.0, 13.1),
      (4.0, 15.0),
      (5.0, 19.6),
      (6.0, 22.0),
      (7.0, 23.5),
      (8.0, 24.5),
      (9.0, 20.1),
      (10.0, 16.4),
      (11.0, 11.2),
      (12.0, 9.9)
    )
    val tmin2024 = Vector(
      (1.0, 2.5),
      (2.0, 6.0),
      (3.0, 5.9),
      (4.0, 7.2),
      (5.0, 10.6),
      (6.0, 11.1),
      (7.0, 13.8),
      (8.0, 14.6),
      (9.0, 11.9),
      (10.0, 9.4),
      (11.0, 6.0),
      (12.0, 5.2)
    )
    val tmax2025 = Vector(
      (1.0, 7.3),
      (2.0, 8.8)
    )
    val tmin2025 = Vector(
      (1.0, 1.4),
      (2.0, 2.8)
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
        .withXAxis(
          Axis.CategoryScale(
            "Month",
            categories = Some(Vector("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"))
          )
        )
        .withYAxis(Axis.LinearScale("Temperature (°C)", None, None))
    val style = lineChartStyle()
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
          font = FontSpec("sans-serif", 10.0, "normal"),
          itemSpacing = 16.0,
          symbolSize = 10.0
        )
      )
      .withTitle(TitleStyle(font = FontSpec("sans-serif", 16, "bold"), colour = "black"))
      .withWidth(800)
      .withHeight(400)
      .withMargins(Margins(40, 80, 50, 110))
    val styledChart = chart.withStyling(style)
    styledChart.renderTo(canvas.getContext2D())
  end renderChart
end Example
