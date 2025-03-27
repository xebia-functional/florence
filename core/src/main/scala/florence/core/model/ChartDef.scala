package florence.core.model

enum ChartDef:

  case LineChart(
      title: Option[String],
      series: Vector[LineSeries],
      xAxis: AxisDef,
      yAxis: AxisDef
  )

final case class LineSeries(
    label: String,
    lineData: LineData
)

enum LineData:
  case Points(data: Vector[(Double, Double)])
  case FunctionPlot(f: Double => Double, start: Double, end: Double, sampleSize: Int)
  case GenericData[A](data: Vector[A], x: A => Double, y: A => Double)

enum AxisDef:
  case LinearScale(label: String, min: Option[Double], max: Option[Double])
  case CategoryScale(label: String, categories: Option[Vector[String]] = None)
