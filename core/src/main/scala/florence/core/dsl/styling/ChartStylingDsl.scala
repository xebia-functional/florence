package florence.core.dsl.styling

import florence.core.model.*
import florence.core.model.styling.*
import florence.core.model.styling.ChartStyleDef.LineChartStyle

object ChartStylingDsl:

  export LineChartStylingDsl.*

  extension [C <: ChartDef, S <: ChartStyleDef](chart: C)
    def withStyling(style: S): StyledChart[C, S] =
      StyledChart(chart, style)
