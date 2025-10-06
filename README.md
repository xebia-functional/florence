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
// sbt
libraryDependencies += "com.xebia" %%% "florence" % "x.y.z"
// Mill
def mvnDeps = Seq(mvn"com.xebia::florence::x.y.z")
// Scala-CLI
//> using dep com.xebia::florence::x.y.z
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

// Fit by redrawing at canvas size (no distortion)
styledChart.renderAtCanvasSize(canvas.getContext2D())

// Or non-uniform scale to fill (may distort)
styledChart.renderFillCanvas(canvas.getContext2D())
```

## Customising Charts

### Customising Axes

```scala
val chart = lineChart("My Chart")
  .withXAxis(Axis.CategoryScale("Month", categories = Some(Vector("Jan", "Feb", "Mar", "Apr"))))
  .withYAxis(Axis.LinearScale("Value", Some(0.0), Some(100.0)))
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

## Building and running the sandbox/demo projects

There are a couple of sandbox modules, and each is set up to work with Vite.

Regardless of which sandbox you'd like to run, you'll need to have compiled it to JavaScript. You could compile your specific module, e.g. `./mill sandbox.fastLinkJS`, or everything (which isn't terribly inefficient because Mill caches everything): `./mill __.fastLinkJS`.

Let's use `sandbox` as the example that you'd like to run. Do the following in your terminal from the root of the project:

```bash
cd sandbox
yarn install
yarn preview
```

If you look at the output you'll now see an address, like this:

```bash
  ➜  Local:   http://localhost:4173/
```

Open that link in your browser to view the site.

## Project Status

Experimental

## License

[License details]

## Dev Environment

- Nix + direnv: enable the dev shell by running `direnv allow` in the repo. The `.envrc` uses `use flake` to load the environment defined in `flake.nix` (JDK 17, Mill, Node/Yarn).
