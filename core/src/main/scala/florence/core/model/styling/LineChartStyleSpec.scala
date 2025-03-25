package florence.core.model.styling

import florence.core.model.shared.*

final case class LineChartStyleSpec(
    commonProps: CommonStyleProps = CommonStyleProps(),
    xAxis: AxisStyle = AxisStyle(),
    yAxis: AxisStyle = AxisStyle(),
    seriesStyles: Map[Int, LineSeriesStyle] = Map.empty,
    defaultSeriesStyle: LineSeriesStyle = LineSeriesStyle(),
    showPoints: Boolean = true
) extends HasCommonProps

final case class LineSeriesStyle(
    colour: Option[String] = Some("black"),
    lineWidth: Option[Double] = Some(2.0),
    lineType: LineType = LineType.Solid,
    markerType: MarkerType = MarkerType.None,
    markerSize: Option[Double] = Some(3.0),
    fillArea: Boolean = false,
    fillColour: Option[String] = None
)

given StyleCopier[LineChartStyleSpec] with
  def copy(style: LineChartStyleSpec, f: CommonStyleProps => CommonStyleProps): LineChartStyleSpec =
    style.copy(commonProps = f(style.commonProps))
