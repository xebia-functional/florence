# Florence - A Scala Charting Library

Florence is a charting library for Scala.

## Project Structure

- **Core**: The foundation of the library containing models, DSLs, and rendering abstractions
- **Renderers**: Platform-specific rendering implementations
    - JavaScript: HTML5 Canvas renderer
    - JVM: (Available for extension)
- **Sandbox**: Examples showcasing the library

## Getting Started

### Setup

Add Florence to your project (in your `build.sbt`):

```scala
libraryDependencies += "com.xebia" %%% "florence-core" % "0.1.0-SNAPSHOT"

// For JavaScript canvas rendering
libraryDependencies += "com.xebia" %%% "florence-renderer-js" % "0.1.0-SNAPSHOT"
```

### Creating a Simple Chart

```scala
import florence.*
import florence.instances.given

// Create data points
val data = Vector(
  (1.0, 10.0),
  (2.0, 15.0),
  (3.0, 7.0),
  (4.0, 20.0)
)

// Create a basic line chart
val chart = lineChart(
  "My Chart Title",
  pointsSeries("Series 1", data*)
)
```

### Styling Your Chart

```scala
// Create a style
val style = lineChartStyle()
  .withDefaultSeriesStyle(
    LineSeriesStyle(
      colour = Some("#1f77b4"),
      markerType = MarkerType.Circle,
      markerSize = Some(3.0)
    )
  )
  .withWidth(800)
  .withHeight(400)

// Apply the style
val styledChart = chart.withStyling(style)
```

### Rendering (JavaScript/Canvas)

```scala
import florence.renderer.js.*
import florence.renderer.js.instances.given

// Get your canvas element
val canvas = document.getElementById("my-canvas").asInstanceOf[HTMLCanvasElement]

// Render the chart
styledChart.renderTo(canvas.getContext2D())
```

## Customising Charts

### Customising Axes

```scala
val chart = lineChart("My Chart")
  .withXAxis(AxisDef.CategoryScale("Month", categories = Some(Vector("Jan", "Feb", "Mar", "Apr"))))
  .withYAxis(AxisDef.LinearScale("Value", Some(0.0), Some(100.0)))
```

### Multiple Series

```scala
val chart = lineChart(
  "Multi-Series Chart",
  pointsSeries("Series 1", data1*),
  pointsSeries("Series 2", data2*),
  functionPlotSeries("y = x²", x => x * x, 0, 10, 100)
)
```

### Working with Generic Data

```scala
// Custom data class
case class SalesData(month: String, revenue: Double, expenses: Double, profit: Double)

// Sample data
val salesData = Vector(
  SalesData("Jan", 12000, 8000, 4000),
  SalesData("Feb", 15000, 9000, 6000),
  SalesData("Mar", 18000, 10000, 8000),
  SalesData("Apr", 14000, 9500, 4500)
)

// Create a line chart with generic data
val chart = lineChart(
  "Sales Performance",
  genericSeries[SalesData](
    "Revenue", 
    salesData, 
    // Extract x-value (convert month to numeric position)
    data => salesData.indexOf(data).toDouble + 1,
    // Extract y-value (revenue)
    data => data.revenue
  ),
  genericSeries[SalesData](
    "Expenses", 
    salesData, 
    data => salesData.indexOf(data).toDouble + 1, 
    data => data.expenses
  ),
  genericSeries[SalesData](
    "Profit", 
    salesData, 
    data => salesData.indexOf(data).toDouble + 1, 
    data => data.profit
  )
)
```

### Customising Individual Series

```scala
val style = lineChartStyle()
  .withSeriesStyle(0, LineSeriesStyle(colour = Some("red"), lineType = LineType.Solid))
  .withSeriesStyle(1, LineSeriesStyle(colour = Some("blue"), lineType = LineType.Dashed))
```

## Build and Run

### Running with Vite

For a development server with hot reloading:

1. Build the JavaScript modules:
   ```
   sbt sandboxJS/fastLinkJS
   ```

2. Install Vite (if not already installed):
   ```
   npm install -D vite
   ```

3. Start the Vite development server:
   ```
   npx vite
   ```

4. Open your browser and go to http://localhost:5173

## Project Status

Experimental

## License

[License details]
