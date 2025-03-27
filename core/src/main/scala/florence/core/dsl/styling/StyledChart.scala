package florence.core.dsl.styling

import florence.core.model.ChartDef
import florence.core.model.ChartDef.LineChart
import florence.core.model.styling.ChartStyleDef
import florence.core.model.styling.ChartStyleDef.LineChartStyle

final case class StyledChart[C <: ChartDef, S <: ChartStyleDef](
    chart: C,
    style: S
)
