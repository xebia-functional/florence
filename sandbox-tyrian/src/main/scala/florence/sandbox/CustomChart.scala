package florence.sandbox

import org.scalajs.dom.HTMLCanvasElement

import florence.*
import florence.instances.given
import florence.renderer.js.*
import florence.renderer.js.instances.given

object CustomChart:

  val chart =
    lineChart(
      "Heathrow Min & Max Temps (2023–2025)",
      pointsSeries("TMax 2023", WeatherData.tmax2023*),
      pointsSeries("TMin 2023", WeatherData.tmin2023*),
      pointsSeries("TMax 2024", WeatherData.tmax2024*),
      pointsSeries("TMin 2024", WeatherData.tmin2024*),
      pointsSeries("TMax 2025", WeatherData.tmax2025*),
      pointsSeries("TMin 2025", WeatherData.tmin2025*)
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

  def renderChart(canvas: HTMLCanvasElement): Unit =
    chart
      .withStyling(style)
      .renderTo(canvas.getContext2D())
