package florence.core.model

final case class LineChartSpec[A](
    title: Option[String],
    series: Vector[LineSeries[A]],
    xAxis: AxisSpec,
    yAxis: AxisSpec
)

final case class LineSeries[A](
    label: String,
    lineData: LineData[A]
)

enum LineData[A]:
  case Points(data: Vector[(Double, Double)])
  case FunctionPlot(f: Double => Double, start: Double, end: Double, sampleSize: Int)
  case GenericData(data: Vector[A], x: A => Double, y: A => Double)
