package florence.core.dsl.styling

import florence.core.model.*
import florence.core.model.styling.*

final case class StyledChart[S <: ChartSpec, T <: ChartStyleSpec](
    spec: S,
    style: T
)

type StyledLineChart[A] = StyledChart[LineChartSpec[A], LineChartStyleSpec]

object ChartStylingDsl:

  extension [A](spec: LineChartSpec[A])
    def withStyling(style: LineChartStyleSpec): StyledLineChart[A] =
      StyledChart(spec, style)
