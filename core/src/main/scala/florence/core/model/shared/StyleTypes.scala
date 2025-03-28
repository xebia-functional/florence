package florence.core.model.shared

// Shared types for styling and rendering

object StyleTypes:

  enum Alignment:
    case Left, Center, Right

  enum LegendPosition:
    case Top, Right, Bottom, Left, TopRight, TopLeft, BottomRight, BottomLeft

  enum LineType:
    case Solid, Dashed, Dotted, DashDot

  enum MarkerType:
    case None, Circle, Square, Triangle, Star, Plus

  enum SliceLabelPosition:
    case Inside, Outside, None

  final case class FontSpec(
      family: String = "sans-serif",
      size: Double = 12.0,
      weight: String = "normal"
  )

  final case class LineStyle(
      colour: String,
      width: Double = 1.0,
      dash: Option[List[Double]] = None
  )

  final case class Padding(
      top: Double = 10.0,
      right: Double = 10.0,
      bottom: Double = 10.0,
      left: Double = 10.0
  )

  final case class Margins(
      top: Double = 40,
      right: Double = 40,
      bottom: Double = 50,
      left: Double = 50
  )
end StyleTypes
