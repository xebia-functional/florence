package florence.core.model.styling

import florence.core.model.shared.*

// This will be a union type for all Chart styles, e.g.:
// type ChartStyleSpec = LineChartStyleSpec | BarChartStyleSpec | PieChartStyleSpec
type ChartStyleSpec = LineChartStyleSpec

case class CommonStyleProps(
    background: BackgroundStyle = BackgroundStyle(),
    border: BorderStyle = BorderStyle(),
    title: TitleStyle = TitleStyle(),
    legend: LegendStyle = LegendStyle(),
    padding: Padding = Padding(),
    width: Int = 800,
    height: Int = 400,
    margins: Margins = Margins(top = 40, right = 40, bottom = 50, left = 70)
)

trait HasCommonProps:
  def commonProps: CommonStyleProps

trait StyleCopier[T <: HasCommonProps]:
  def copy(t: T, f: CommonStyleProps => CommonStyleProps): T

extension [T <: HasCommonProps](style: T)(using copier: StyleCopier[T])
  def background: BackgroundStyle = style.commonProps.background
  def border: BorderStyle         = style.commonProps.border
  def title: TitleStyle           = style.commonProps.title
  def legend: LegendStyle         = style.commonProps.legend
  def padding: Padding            = style.commonProps.padding
  def width: Int                  = style.commonProps.width
  def height: Int                 = style.commonProps.height
  def margins: Margins            = style.commonProps.margins

  def withBackground(bg: BackgroundStyle): T =
    copier.copy(style, props => props.copy(background = bg))
  def withBorder(border: BorderStyle): T =
    copier.copy(style, props => props.copy(border = border))
  def withTitle(title: TitleStyle): T =
    copier.copy(style, props => props.copy(title = title))
  def withLegend(legend: LegendStyle): T =
    copier.copy(style, props => props.copy(legend = legend))
  def withPadding(padding: Padding): T =
    copier.copy(style, props => props.copy(padding = padding))
  def withWidth(width: Int): T =
    copier.copy(style, props => props.copy(width = width))
  def withHeight(height: Int): T =
    copier.copy(style, props => props.copy(height = height))
  def withMargins(margins: Margins): T =
    copier.copy(style, props => props.copy(margins = margins))

final case class BackgroundStyle(
    colour: String = "#ffffff",
    opacity: Double = 1.0
)

final case class BorderStyle(
    colour: String = "#000000",
    width: Int = 0,
    radius: Int = 0
)

final case class TitleStyle(
    font: FontSpec = FontSpec("sans-serif", 16.0, "bold"),
    colour: String = "black",
    alignment: Alignment = Alignment.Center,
    margin: Double = 20.0
)

final case class AxisStyle(
    lineColour: Option[String] = None,
    lineWidth: Option[Double] = None,
    labelFont: Option[FontSpec] = None,
    tickColour: Option[String] = None,
    tickLength: Option[Double] = None,
    gridLines: Boolean = true,
    gridLineColour: Option[String] = None,
    gridLineWidth: Option[Double] = None,
    gridLineDash: Option[Vector[Double]] = None
)

final case class LegendStyle(
    visible: Boolean = true,
    position: LegendPosition = LegendPosition.Right,
    font: FontSpec = FontSpec("sans-serif", 12.0, "normal"),
    itemSpacing: Double = 10.0,
    symbolSize: Double = 15.0,
    border: Option[BorderStyle] = None,
    background: Option[String] = None,
    padding: Padding = Padding(5, 5, 5, 5)
)
