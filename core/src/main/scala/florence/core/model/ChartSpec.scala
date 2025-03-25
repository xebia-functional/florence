package florence.core.model

// This will be a union type for all Chart types, e.g.:
// type ChartSpec = LineChartSpec[?] | BarChartSpec[?] | PieChartSpec[?]
type ChartSpec = LineChartSpec[?]

enum AxisSpec:
  case LinearScale(label: String, min: Option[Double], max: Option[Double])
  case CategoryScale(label: String, categories: Option[Vector[String]] = None)
